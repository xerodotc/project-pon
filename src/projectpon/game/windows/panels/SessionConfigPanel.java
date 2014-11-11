package projectpon.game.windows.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import projectpon.game.Configuration;
import projectpon.game.SessionConfiguration;
import projectpon.game.windows.NewGameWindow;

public class SessionConfigPanel extends JPanel {
	private static final long serialVersionUID = -6058937613283538217L;
	
	private NewGameWindow parentWindow = null;
	private JSpinner minPointsSpinner = null;
	private JSpinner maxPointsSpinner = null;
	private JSpinner diffPointsSpinner = null;
	
	public SessionConfigPanel(NewGameWindow parent) {
		parentWindow = parent;
		this.setLayout(new BorderLayout());
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.add(setupMinPointsPanel());
		optionsPanel.add(setupMaxPointsPanel());
		optionsPanel.add(setupDiffPointsPanel());
		this.add(optionsPanel, BorderLayout.CENTER);
		this.add(setupButtonsPanel(), BorderLayout.SOUTH);
	}
	
	private JPanel setupMinPointsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
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
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
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
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 4));
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
