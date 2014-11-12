package projectpon.game.dialogs.panels;

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

import projectpon.engine.GameEngine;
import projectpon.engine.GameNetwork;
import projectpon.engine.exceptions.NetworkException;
import projectpon.game.Configuration;
import projectpon.game.SessionConfiguration;
import projectpon.game.dialogs.NewGameDialog;
import projectpon.game.scenes.PongScene;

public class ServerConfigPanel extends JPanel {
	private static final long serialVersionUID = -751758399016257989L;

	private NewGameDialog parentWindow = null;
	private PongScene pscene = null;
	private JSpinner portSettingSpinner = null;
	private JLabel serverStatusLabel = null;
	
	private static final String SERVER_STATUS_STRING_FORMAT =
			"<html>Server status: <font color=\"%s\">%s</font></html>";
	
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
		this.setPreferredSize(new Dimension(300, 190));
	}
	
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
	
	private JPanel setupServerStatusPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
		serverStatusLabel = new JLabel(getServerStatusString("Not running", Color.RED));
		panel.add(serverStatusLabel);
		
		return panel;
	}
	
	private JPanel setupButtonsPanel() {
		JPanel panel = new JPanel();
panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
		
		final JButton startButton = new JButton("Start server");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
				
				Configuration.set("networkOptions", "localPort",
						(int) portSettingSpinner.getValue());
				Configuration.save();
				
				try {
					GameNetwork.Server.addOnAcceptedListener(
							new GameNetwork.Server.AcceptListener() {
								@Override
								public void onAccepted(Socket remote) {
									try {
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
				if (GameNetwork.Server.isStarted()) {
					try {
						GameNetwork.Server.stop();
					} catch (NetworkException e) {
						e.printStackTrace();
					}
					startButton.setText("Start server");
				}
				parentWindow.dispose();
			}
		});
		
		panel.add(startButton);
		panel.add(cancelButton);
		
		return panel;
	}
	
	private void setServerStatus(String status, Color color) {
		serverStatusLabel.setText(getServerStatusString(status, color));
	}
	
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
	
	private String getServerStatusString(String status, String color) {
		return String.format(SERVER_STATUS_STRING_FORMAT, color, status);
	}
}
