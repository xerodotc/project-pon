/**
 * SessionConfigPanel.java
 * 
 * A JPanel for new game to be started settings
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.dialogs.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import th.in.xerodotc.projectpon.game.Configuration;
import th.in.xerodotc.projectpon.game.SessionConfiguration;
import th.in.xerodotc.projectpon.game.dialogs.NewGameDialog;

public class SessionConfigPanel extends JPanel {
	private static final long serialVersionUID = -6058937613283538217L;
	
	private NewGameDialog parentWindow = null; // parent NewGameDialog
	private JSpinner minPointsSpinner = null; // minimum winning score
	private JSpinner maxPointsSpinner = null; // maximum winning score
	private JSpinner diffPointsSpinner = null; // required score difference
	private JButton startButton = null; // the start button
	
	/**
	 * Setup the whole panel
	 * 
	 * @param parent	Parent NewGameDialog
	 */
	public SessionConfigPanel(NewGameDialog parent) {
		parentWindow = parent;
		this.setLayout(new BorderLayout());
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		labelPanel.add(new JLabel("<html><u>Game conditions</u></html>"));
		optionsPanel.add(labelPanel);
		optionsPanel.add(setupMinPointsPanel());
		optionsPanel.add(setupMaxPointsPanel());
		optionsPanel.add(setupDiffPointsPanel());
		optionsPanel.add(Box.createRigidArea(new Dimension(32, 8)));
		this.add(optionsPanel, BorderLayout.CENTER);
		this.add(setupButtonsPanel(), BorderLayout.SOUTH);
		this.setPreferredSize(new Dimension(270, 150));
		parentWindow.getRootPane().setDefaultButton(startButton);
	}
	
	/**
	 * Setup minimum winning score setting panel
	 * 
	 * @return JPanel for minimum winning score setting
	 */
	private JPanel setupMinPointsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
		panel.add(new JLabel("Minimum winning score: "));
		
		minPointsSpinner = new JSpinner(
				new SpinnerNumberModel(SessionConfiguration.minimumWinScore,
						Configuration.getMinBound("lastSession", "minPoints"),
						Configuration.getMaxBound("lastSession", "minPoints"), 1));
		
		// reset to previous value, if the value is invalid
		minPointsSpinner.addChangeListener(new ChangeListener() {
			int prevValue = SessionConfiguration.minimumWinScore;
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (!isValuesValid()) {
					minPointsSpinner.setValue(prevValue);
				} else {
					prevValue = getIntValue(minPointsSpinner);
				}
			}
		});
		
		panel.add(minPointsSpinner);
		
		return panel;
	}
	
	/**
	 * Setup maximum winning score setting panel
	 * 
	 * @return JPanel for maximum winning score setting
	 */
	private JPanel setupMaxPointsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
		panel.add(new JLabel("Maximum winning score: "));
		
		maxPointsSpinner = new JSpinner(
				new SpinnerNumberModel(SessionConfiguration.maximumWinScore,
						Configuration.getMinBound("lastSession", "maxPoints"),
						Configuration.getMaxBound("lastSession", "maxPoints"), 1));
		// reset to previous value, if the value is invalid
		maxPointsSpinner.addChangeListener(new ChangeListener() {
			int prevValue = SessionConfiguration.maximumWinScore;
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (!isValuesValid()) {
					maxPointsSpinner.setValue(prevValue);
				} else {
					prevValue = getIntValue(maxPointsSpinner);
				}
			}
		});
		
		panel.add(maxPointsSpinner);
		
		return panel;
	}
	
	/**
	 * Setup required score difference setting panel
	 * 
	 * @return JPanel for required score difference setting
	 */
	private JPanel setupDiffPointsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
		panel.add(new JLabel("Required score difference: "));
		
		diffPointsSpinner = new JSpinner(
				new SpinnerNumberModel(SessionConfiguration.minimumWinDiff,
						Configuration.getMinBound("lastSession", "diffPoints"),
						Configuration.getMaxBound("lastSession", "diffPoints"), 1));
		// reset to previous value, if the value is invalid
		diffPointsSpinner.addChangeListener(new ChangeListener() {
			int prevValue = SessionConfiguration.minimumWinDiff;
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (!isValuesValid()) {
					diffPointsSpinner.setValue(prevValue);
				} else {
					prevValue = getIntValue(diffPointsSpinner);
				}
			}
		});
		
		panel.add(diffPointsSpinner);
		
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
		
		startButton = new JButton((parentWindow.isServer()) ?
				"Next" : "Play");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// save to SessionConfiguration
				SessionConfiguration.minimumWinScore = getIntValue(minPointsSpinner);
				SessionConfiguration.maximumWinScore = getIntValue(maxPointsSpinner);
				SessionConfiguration.minimumWinDiff = getIntValue(diffPointsSpinner);
				Configuration.set("lastSession", "minPoints",
						SessionConfiguration.minimumWinScore);
				Configuration.set("lastSession", "maxPoints",
						SessionConfiguration.maximumWinScore);
				Configuration.set("lastSession", "diffPoints",
						SessionConfiguration.minimumWinDiff);
				Configuration.save();
				parentWindow.next(); // perform next action
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
	 * Check are the settings is valid
	 * 
	 * @return True if settings are valid
	 */
	private boolean isValuesValid() {
		return getIntValue(minPointsSpinner) <= getIntValue(maxPointsSpinner) &&
				getIntValue(diffPointsSpinner) <= getIntValue(minPointsSpinner); 
	}
	
	/**
	 * Get spinner's integer value
	 * 
	 * @param spinner	JSpinner
	 * @return			Integer value of specified JSpinner
	 */
	private int getIntValue(JSpinner spinner) {
		return (int) spinner.getValue();
	}
}
