/**
 * ConnectDialog.java
 * 
 * A dialog for connect to a network game
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
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
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import th.in.xerodotc.projectpon.engine.GameDialog;
import th.in.xerodotc.projectpon.engine.GameEngine;
import th.in.xerodotc.projectpon.engine.GameNetwork;
import th.in.xerodotc.projectpon.engine.exceptions.NetworkException;
import th.in.xerodotc.projectpon.game.Configuration;
import th.in.xerodotc.projectpon.game.scenes.PongScene;

public class ConnectDialog extends GameDialog {
	private static final long serialVersionUID = 2371553969727612471L;
	
	// reference to this object for using in anonymous class
	private final ConnectDialog THIS = this;
	private PongScene pscene; // PongScene to be switched to
	private JPanel mainPanel; // main dialog panel
	private JTextField addressField; // address field
	private JSpinner portSettingSpinner; // port number spinner
	private JButton startButton; // a start button
	
	private boolean ok = false; // ready to play?
	private boolean noWarning = false; // don't warning about connection error
	
	/**
	 * Setup the whole dialog
	 * 
	 * @param scene		PongScene instance
	 */
	public ConnectDialog(PongScene scene) {
		super("Connect to a network game");
		pscene = scene;
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		JPanel connectPanel = new JPanel();
		connectPanel.setLayout(new BoxLayout(connectPanel, BoxLayout.Y_AXIS));
		connectPanel.add(setupAddressPanel());
		connectPanel.add(setupPortPanel());
		mainPanel.add(Box.createRigidArea(new Dimension(32, 8)), BorderLayout.NORTH);
		mainPanel.add(connectPanel, BorderLayout.CENTER);
		mainPanel.add(setupButtonsPanel(), BorderLayout.SOUTH);
		mainPanel.setPreferredSize(new Dimension(320, 100));
		this.add(mainPanel);
		this.pack();
		this.getRootPane().setDefaultButton(startButton);
	}
	
	/**
	 * Setup address field panel
	 * 
	 * @return JPanel for address field
	 */
	private JPanel setupAddressPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
		panel.add(new JLabel("Server address: "));
		addressField = new JTextField(
				Configuration.get("networkOptions", "lastRemoteServerAddr"));
		addressField.setPreferredSize(new Dimension(180, 20));
		panel.add(addressField);
		return panel;
	}
	
	/**
	 * Setup port spinner panel
	 * 
	 * @return JPanel for port number spinner
	 */
	private JPanel setupPortPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
		panel.add(new JLabel("Server port: "));
		portSettingSpinner = new JSpinner(new SpinnerNumberModel(
				Configuration.getInt("networkOptions", "lastRemoteServerPort"),
				Configuration.getMinBound("networkOptions", "lastRemoteServerPort"),
				Configuration.getMaxBound("networkOptions", "lastRemoteServerPort"),
				1));
		panel.add(portSettingSpinner);
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
		
		startButton = new JButton("Connect");
		JButton cancelButton = new JButton("Cancel");
		
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String addr = addressField.getText();
				final int port = (int) portSettingSpinner.getValue();
				
				if (addr.equals("")) {
					JOptionPane.showMessageDialog(THIS, "Enter server address!",
							"Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				GameNetwork.Client.addOnConnectedListener(
						new GameNetwork.Client.ConnectListener() {
							@Override
							public void onFailed(NetworkException e) {
								errorNotify(e.getMessage());
								setConnecting(true);
							}
							
							@Override
							public void onConnected(Socket remote) {
								// save last connection infos to configurations
								Configuration.set("networkOptions", "lastRemoteServerAddr",
										addr);
								Configuration.set("networkOptions", "lastRemoteServerPort",
										port);
								Configuration.save();
								
								try {
									/*
									 * Retrieve game conditions from server
									 */
									InputStream in = remote.getInputStream();
									OutputStream out = remote.getOutputStream();
									
									byte[] buffer = new byte[256];
									in.read(buffer);
									String data = new String(buffer).trim();
									String[] score = data.split(" ");
									
									if (score.length < 2) {
										out.write("false".getBytes());
										throw new Exception("invalid response");
									}
									
									String conditionsString = "<u>Game conditions</u><br>";
									conditionsString += "Minimum winning score: " + score[0] + "<br>";
									conditionsString += "Maximum winning score: " + score[1] + "<br>";
									conditionsString += "Required score difference: " + score[2] + "<br>";
									conditionsString += "<br>";
								
									int confirm = JOptionPane.showConfirmDialog(
											THIS, "<html>" + conditionsString + "Accept this server's conditions?</html>", "Confirmation",
											JOptionPane.OK_CANCEL_OPTION);
									
									/**
									 * Send a response to server
									 */
									switch (confirm) {
									case JOptionPane.OK_OPTION:
										out.write("true".getBytes());
										GameNetwork.setSocket(remote);
										GameEngine.setScene(pscene);
										ok = true;
										THIS.dispose();
										break;
										
									case JOptionPane.CANCEL_OPTION:
										out.write("false".getBytes());
										GameNetwork.Client.disconnect();
										setConnecting(true);
										break;
									}
								} catch (Exception e1) {
									errorNotify(e1.getMessage());
									setConnecting(true);
								}
							}
						}
					);
				
				// Connect to server!
				GameNetwork.Client.connect(addr, port);
				setConnecting(false);
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				THIS.dispose();
			}
		});
		
		panel.add(startButton);
		panel.add(cancelButton);
		return panel;
	}
	
	/**
	 * Set form status
	 */
	private void setConnecting(boolean en) {
		addressField.setEnabled(en);
		portSettingSpinner.setEnabled(en);
		startButton.setEnabled(en);
		if (!en) {
			startButton.setText("Connecting...");
		} else {
			startButton.setText("Connect");
		}
	}
	
	/**
	 * Notify an error about connecting to server
	 * 
	 * @param msg		Error messages
	 */
	private void errorNotify(String msg) {
		if (noWarning) {
			return;
		}
		
		JOptionPane.showMessageDialog(this,
				"Can't connect to server\n\n" + msg, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Disconnect if window is closed
	 * (except when ready to play)
	 */
	@Override
	public void windowClosed(WindowEvent arg0) {
		if (!ok) {
			try {
				noWarning = true;
				GameNetwork.Client.disconnect();
			} catch (NetworkException e) {
				e.printStackTrace();
			}
		}
		super.windowClosed(arg0);
	}
}
