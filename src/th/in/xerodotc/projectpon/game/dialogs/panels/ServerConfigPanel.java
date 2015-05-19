/**
 * ServerConfigPanel.java
 * 
 * A JPanel for server settings
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.dialogs.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import th.in.xerodotc.projectpon.engine.GameEngine;
import th.in.xerodotc.projectpon.engine.GameNetwork;
import th.in.xerodotc.projectpon.engine.exceptions.NetworkException;
import th.in.xerodotc.projectpon.game.Configuration;
import th.in.xerodotc.projectpon.game.SessionConfiguration;
import th.in.xerodotc.projectpon.game.dialogs.NewGameDialog;
import th.in.xerodotc.projectpon.game.scenes.PongScene;

public class ServerConfigPanel extends JPanel {
	private static final long serialVersionUID = -751758399016257989L;

	private NewGameDialog parentWindow = null; // parent NewGameDialog
	private PongScene pscene = null; // the PongScene to be switched to
	private JSpinner portSettingSpinner = null; // port number spinner
	private JLabel serverStatusLabel = null; // server status label
	private JButton startButton = null; // start button
	
	// server status string format
	private static final String SERVER_STATUS_STRING_FORMAT =
			"<html>Server status: <font color=\"%s\">%s</font></html>";
	
	/**
	 * Setup the whole panel
	 * 
	 * @param parent		Parent NewGameDialog
	 * @param scene			PongScene to be switched to
	 */
	public ServerConfigPanel(NewGameDialog parent, PongScene scene) {
		parentWindow = parent;
		pscene = scene;
		
		this.setLayout(new BorderLayout(0, 16));
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(setupConditionsPanel());
		topPanel.add(setupPortSettingPanel());
		topPanel.add(setupServerStatusPanel());
		
		this.add(topPanel, BorderLayout.CENTER);
		this.add(setupButtonsPanel(), BorderLayout.SOUTH);
		this.setPreferredSize(new Dimension(300, 200));
		parentWindow.getRootPane().setDefaultButton(startButton);
	}
	
	/**
	 * Setup game condition label panel
	 * 
	 * @return JPanel for game condition label
	 */
	private JPanel setupConditionsPanel() {
		JPanel panel = new JPanel();
		JPanel subpanel1 = new JPanel();
		JPanel subpanel2 = new JPanel();
		JPanel subpanel3 = new JPanel();
		panel.setLayout(new BorderLayout());
		subpanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
		subpanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
		subpanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		String conditionsString = "<html>";
		conditionsString += "Minimum winning score: " + SessionConfiguration.minimumWinScore + "<br>";
		conditionsString += "Maximum winning score: " + SessionConfiguration.maximumWinScore + "<br>";
		conditionsString += "Required score difference: " + SessionConfiguration.minimumWinDiff + "<br>";
		conditionsString += "</html>";
		subpanel1.add(new JLabel("<html><u>Game conditions</u></html>"));
		JLabel conditionsTextArea = new JLabel(conditionsString);
		subpanel2.add(conditionsTextArea);
		subpanel3.add(Box.createRigidArea(new Dimension(32, 16)));
		
		panel.add(subpanel1, BorderLayout.NORTH);
		panel.add(subpanel2, BorderLayout.CENTER);
		panel.add(subpanel3, BorderLayout.SOUTH);
		
		return panel;
	}
	
	/**
	 * Setup port number spinner panel
	 * 
	 * @return JPanel for port number spinner
	 */
	private JPanel setupPortSettingPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
		panel.add(new JLabel("Server port: "));
		portSettingSpinner = new JSpinner(new SpinnerNumberModel(
				Configuration.getInt("networkOptions", "localPort"),
				Configuration.getMinBound("networkOptions", "localPort"),
				Configuration.getMaxBound("networkOptions", "localPort"),
				1));
		panel.add(portSettingSpinner);
		
		return panel;
	}
	
	/**
	 * Setup server status label panel
	 * 
	 * @return JPanel for server status panel
	 */
	private JPanel setupServerStatusPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
		serverStatusLabel = new JLabel(getServerStatusString("Not running", Color.RED));
		panel.add(serverStatusLabel);
		
		return panel;
	}
	
	/**
	 * Setup buttons panel
	 * 
	 * @return JPanel for buttons
	 */
	private JPanel setupButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
		
		startButton = new JButton("Start server");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * Stop the server
				 */
				if (GameNetwork.Server.isStarted()) {
					try {
						GameNetwork.Server.stop();
					} catch (NetworkException e) {
						e.printStackTrace();
					}
					setServerStatus("Stopped", Color.RED);
					portSettingSpinner.setEnabled(true);
					startButton.setText("Start server");
					return;
				}
				
				/*
				 * Save the settings
				 */
				Configuration.set("networkOptions", "localPort",
						(int) portSettingSpinner.getValue());
				Configuration.save();
				
				try {
					GameNetwork.Server.addOnAcceptedListener(
							new GameNetwork.Server.AcceptListener() {
								@Override
								public void onAccepted(Socket remote) {
									try {
										/*
										 * Send game conditions to client
										 */
										setServerStatus("Client connected", Color.BLUE);
										
										OutputStream out = remote.getOutputStream();
										InputStream in = remote.getInputStream();
										
										out.write(
												String.format("%d %d %d",
														SessionConfiguration.minimumWinScore,
														SessionConfiguration.maximumWinScore,
														SessionConfiguration.minimumWinDiff).getBytes());
										out.flush();
										
										byte[] buffer = new byte[256];
										in.read(buffer);
										
										/*
										 * Start the game if client accepted the conditions
										 * Else, accept new client
										 */
										String response = new String(buffer).trim();
										if (Boolean.parseBoolean(response)) {
											GameNetwork.setSocket(remote);
											try {
												GameNetwork.Server.stop();
											} catch (NetworkException e1) {
												e1.printStackTrace();
											}
											GameEngine.setScene(pscene);
											parentWindow.dispose();
										} else {
											remote.close();
											GameNetwork.Server.acceptClient();
											setServerStatus("Waiting for new client...", Color.GREEN);
										}
									} catch (IOException e) {
										JOptionPane.showMessageDialog(parentWindow, e, "Error", JOptionPane.ERROR_MESSAGE);
										GameNetwork.Server.acceptClient();
									}
								}
							});
					
					// Start the server and accept client!
					GameNetwork.Server.start(Configuration.getInt("networkOptions", "localPort"));
					GameNetwork.Server.acceptClient();
					portSettingSpinner.setEnabled(false);
					startButton.setText("Stop server");
					setServerStatus("Waiting for client...", Color.GREEN);
				} catch (NetworkException e) {
					JOptionPane.showMessageDialog(parentWindow, e, "Error", JOptionPane.ERROR_MESSAGE);
					try {
						GameNetwork.Server.stop();
					} catch (NetworkException e1) {
						e1.printStackTrace();
					}
					parentWindow.dispose();
				}
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				parentWindow.dispose();
			}
		});
		
		panel.add(startButton);
		panel.add(cancelButton);
		
		return panel;
	}
	
	/**
	 * Set server status label
	 * 
	 * @param status	Server status
	 * @param color		Server status color
	 */
	private void setServerStatus(String status, Color color) {
		serverStatusLabel.setText(getServerStatusString(status, color));
	}
	
	/**
	 * Get formatted server status string
	 * 
	 * @param status	Server status
	 * @param color		Server status color (java.awt.Color)
	 */
	private String getServerStatusString(String status, Color color) {
		String colorString = "black";
		if (color == Color.RED) {
			colorString = "red";
		} else if (color == Color.BLUE) {
			colorString = "blue";
		} else if (color == Color.GREEN) {
			colorString = "green";
		} else if (color == Color.YELLOW) {
			colorString = "yellow";
		}
		return getServerStatusString(status, colorString);
	}
	
	/**
	 * Get formatted server status string
	 * 
	 * @param status	Server status
	 * @param color		Server status color
	 */
	private String getServerStatusString(String status, String color) {
		return String.format(SERVER_STATUS_STRING_FORMAT, color, status);
	}
}
