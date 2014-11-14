package projectpon.game.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;

import projectpon.engine.GameDialog;
import projectpon.engine.GameSound;
import projectpon.game.Configuration;
import projectpon.game.SessionConfiguration;
import projectpon.game.dialogs.panels.AiOptionsPanel;
import projectpon.game.dialogs.panels.CheatOptionsPanel;
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
	
	public JCheckBox cheatsEnableCheckbox;
	
	public OptionsDialog() {
		super("Options");
		
		this.setLayout(new BorderLayout());
		
		optionsTabs = new JTabbedPane();
		optionsTabs.addTab("CPU Player", new AiOptionsPanel(this));
		optionsTabs.addTab("Sounds", new SoundOptionsPanel(this));
		inputPanel = new InputOptionsPanel(this);
		optionsTabs.addTab("Input", inputPanel);
		if (SessionConfiguration.cheatsActivated) {
			optionsTabs.addTab("Cheats", new CheatOptionsPanel(this));
		}
		
		this.add(optionsTabs, BorderLayout.CENTER);
		this.add(setupButtonsPanel(), BorderLayout.SOUTH);
		this.setSize(380, 560);
	}
	
	private JPanel setupButtonsPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveConfiguration();
				dispose();
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
		Configuration.set("aiOptions", "speed", aiSpeedSlider.getValue());
		Configuration.set("soundOptions", "globalEnabled",
				soundsGlobalCheckbox.isSelected());
		Configuration.set("soundOptions", "globalVolume",
				soundsGlobalSlider.getValue());
		Configuration.set("soundOptions", "musicEnabled",
				soundsMusicCheckbox.isSelected());
		Configuration.set("soundOptions", "musicVolume",
				soundsMusicSlider.getValue());
		Configuration.set("soundOptions", "soundsEnabled",
				soundsSfxCheckbox.isSelected());
		Configuration.set("soundOptions", "soundsVolume",
				soundsSfxSlider.getValue());
		Configuration.set("inputPrimaryPlayer", "type",
				(primaryInputTypeKeyboardRadio.isSelected()) ? "keyboard" : "mouse");
		Configuration.set("inputPrimaryPlayer", "keyUp", primaryKeyUp);
		Configuration.set("inputPrimaryPlayer", "keyDown", primaryKeyDown);
		Configuration.set("inputPrimaryPlayer", "keyLaunch", primaryKeyLaunch);
		Configuration.set("inputPrimaryPlayer", "keySpeed", primaryKeySpeedSlider.getValue());
		Configuration.set("inputPrimaryPlayer", "mbLaunch", primaryMbLaunch);
		Configuration.set("inputSecondaryPlayer", "type",
				(secondaryInputTypeKeyboardRadio.isSelected()) ? "keyboard" : "mouse");
		Configuration.set("inputSecondaryPlayer", "keyUp", secondaryKeyUp);
		Configuration.set("inputSecondaryPlayer", "keyDown", secondaryKeyDown);
		Configuration.set("inputSecondaryPlayer", "keyLaunch", secondaryKeyLaunch);
		Configuration.set("inputSecondaryPlayer", "keySpeed", secondaryKeySpeedSlider.getValue());
		Configuration.set("inputSecondaryPlayer", "mbLaunch", secondaryMbLaunch);
		Configuration.validate(true);
		Configuration.save();
		
		GameSound.setGlobalEnabled(
				Configuration.getBoolean("soundOptions", "globalEnabled"));
		GameSound.setGlobalVolume(
				Configuration.getInt("soundOptions", "globalVolume"));
		GameSound.setMusicEnabled(
				Configuration.getBoolean("soundOptions", "musicEnabled"));
		GameSound.setMusicVolume(
				Configuration.getInt("soundOptions", "musicVolume"));
		GameSound.setSoundsEnabled(
				Configuration.getBoolean("soundOptions", "soundsEnabled"));
		GameSound.setSoundsVolume(
				Configuration.getInt("soundOptions", "soundsVolume"));
		
		if (!GameSound.isBGMPlaying() && 
				Configuration.getBoolean("soundOptions", "globalEnabled") &&
				Configuration.getBoolean("soundOptions", "musicEnabled")) {
			GameSound.playMusic("title");
		}
		
		if (SessionConfiguration.cheatsActivated) {
			SessionConfiguration.cheatsEnabled = cheatsEnableCheckbox.isSelected();
		}
	}
}
