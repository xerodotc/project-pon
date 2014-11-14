package projectpon.game.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;

import projectpon.engine.GameDialog;
import projectpon.game.dialogs.panels.AiOptionsPanel;
import projectpon.game.dialogs.panels.InputOptionsPanel;
import projectpon.game.dialogs.panels.SoundOptionsPanel;

public class OptionsDialog extends GameDialog {
	private static final long serialVersionUID = 149608132079614747L;
	
	private JTabbedPane optionsTabs;
	private InputOptionsPanel inputPanel;
	
	public JSlider aiSpeedSlider;
	
	public JCheckBox soundsGlobalCheckbox;
	public JSlider soundsGlobalSlider;
	public JCheckBox soundsMusicCheckbox;
	public JSlider soundsMusicSlider;
	public JCheckBox soundsSfxCheckbox;
	public JSlider soundsSfxSlider;
	
	public JRadioButton primaryInputTypeMouseRadio;
	public JRadioButton primaryInputTypeKeyboardRadio;
	public int primaryKeyUp;
	public int primaryKeyDown;
	public int primaryKeyLaunch;
	public JSlider primaryKeySpeedSlider;
	public int primaryMbLaunch;
	public JRadioButton secondaryInputTypeMouseRadio;
	public JRadioButton secondaryInputTypeKeyboardRadio;
	public int secondaryKeyUp;
	public int secondaryKeyDown;
	public int secondaryKeyLaunch;
	public JSlider secondaryKeySpeedSlider;
	public int secondaryMbLaunch;
	
	public OptionsDialog() {
		super("Options");
		
		this.setLayout(new BorderLayout());
		
		optionsTabs = new JTabbedPane();
		optionsTabs.addTab("CPU Player", new AiOptionsPanel(this));
		optionsTabs.addTab("Sounds", new SoundOptionsPanel(this));
		inputPanel = new InputOptionsPanel(this);
		optionsTabs.addTab("Input", inputPanel);
		
		this.add(optionsTabs, BorderLayout.CENTER);
		this.add(setupButtonsPanel(), BorderLayout.SOUTH);
		this.setSize(380, 600);
	}
	
	private JPanel setupButtonsPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveConfiguration();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		panel.add(saveButton);
		panel.add(cancelButton);
		
		return panel;
	}
	
	@Override
	public void childWindowClosed() {
		super.childWindowClosed();
		inputPanel.refreshButtonLabel();
	}
	
	private void saveConfiguration() {
		
	}
}
