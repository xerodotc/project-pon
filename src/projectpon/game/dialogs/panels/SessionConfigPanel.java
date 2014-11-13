package projectpon.game.dialogs.panels;

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

import projectpon.game.Configuration;
import projectpon.game.SessionConfiguration;
import projectpon.game.dialogs.NewGameDialog;

public class SessionConfigPanel extends JPanel {
	private static final long serialVersionUID = -6058937613283538217L;
	
	private NewGameDialog parentWindow = null;
	private JSpinner minPointsSpinner = null;
	private JSpinner maxPointsSpinner = null;
	private JSpinner diffPointsSpinner = null;
	
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
	}
	
	private JPanel setupMinPointsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
		panel.add(new JLabel("Minimum winning score: "));
		
		minPointsSpinner = new JSpinner(
				new SpinnerNumberModel(SessionConfiguration.minimumWinScore,
						Configuration.getMinBound("lastSession", "minPoints"),
						Configuration.getMaxBound("lastSession", "minPoints"), 1));
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
	
	private JPanel setupMaxPointsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
		panel.add(new JLabel("Maximum winning score: "));
		
		maxPointsSpinner = new JSpinner(
				new SpinnerNumberModel(SessionConfiguration.maximumWinScore,
						Configuration.getMinBound("lastSession", "maxPoints"),
						Configuration.getMaxBound("lastSession", "maxPoints"), 1));
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
	
	private JPanel setupDiffPointsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
		panel.add(new JLabel("Required score difference: "));
		
		diffPointsSpinner = new JSpinner(
				new SpinnerNumberModel(SessionConfiguration.minimumWinDiff,
						Configuration.getMinBound("lastSession", "diffPoints"),
						Configuration.getMaxBound("lastSession", "diffPoints"), 1));
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
	
	private JPanel setupButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
		
		JButton startButton = new JButton((parentWindow.isServer()) ?
				"Next" : "Play");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
				parentWindow.next();
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
	
	private boolean isValuesValid() {
		return getIntValue(minPointsSpinner) <= getIntValue(maxPointsSpinner) &&
				getIntValue(diffPointsSpinner) <= getIntValue(minPointsSpinner); 
	}
	
	private int getIntValue(JSpinner spinner) {
		return (int) spinner.getValue();
	}
}
