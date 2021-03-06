/**
 * SoundOptionsPanel.java
 * 
 * A JPanel for sound settings tab
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.dialogs.panels;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

import th.in.xerodotc.projectpon.game.Configuration;
import th.in.xerodotc.projectpon.game.dialogs.OptionsDialog;

public class SoundOptionsPanel extends JPanel {
	private static final long serialVersionUID = 6529134613388052291L;
	
	private OptionsDialog parent; // parent OptionDialog
	
	private JPanel musicPanel; // music settings panel
	private JPanel soundsPanel; // sounds settings panel
	
	/**
	 * Setup the whole panel
	 * 
	 * @param o		Parent OptionDialog
	 */
	public SoundOptionsPanel(OptionsDialog o) {
		parent = o;
		
		this.setBorder(new EmptyBorder(16, 16, 16, 16));
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 16));
		this.add(setupGlobalPanel());
		musicPanel = setupMusicPanel();
		this.add(musicPanel);
		soundsPanel = setupSoundsPanel();
		this.add(soundsPanel);
		
		refreshState();
	}
	
	/**
	 * Setup global sound settings panel
	 * 
	 * @return JPanel for global sound settings
	 */
	private JPanel setupGlobalPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridLayout(2, 1));
		
		JPanel enabledPanel = new JPanel();
		enabledPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		parent.soundsGlobalCheckbox = new JCheckBox("Enable sounds");
		parent.soundsGlobalCheckbox.setSelected(
				Configuration.getBoolean("soundOptions", "globalEnabled"));
		parent.soundsGlobalCheckbox.addActionListener(new RefreshStateTrigger());
		enabledPanel.add(parent.soundsGlobalCheckbox);
		
		JPanel volumePanel = new JPanel();
		volumePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		volumePanel.add(new JLabel("Volume: "));
		parent.soundsGlobalSlider = new JSlider(
				Configuration.getMinBound("soundOptions", "globalVolume"),
				Configuration.getMaxBound("soundOptions", "globalVolume"),
				Configuration.getInt("soundOptions", "globalVolume"));
		volumePanel.add(parent.soundsGlobalSlider);
		
		panel.add(enabledPanel);
		panel.add(volumePanel);
		
		return panel;
	}
	
	/**
	 * Setup music settings panel
	 * 
	 * @return JPanel for music settings
	 */
	private JPanel setupMusicPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridLayout(2, 1));
		panel.setBorder(BorderFactory.createTitledBorder("Music"));
		
		JPanel enabledPanel = new JPanel();
		enabledPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		parent.soundsMusicCheckbox = new JCheckBox("Enable music");
		parent.soundsMusicCheckbox.setSelected(
				Configuration.getBoolean("soundOptions", "musicEnabled"));
		parent.soundsMusicCheckbox.addActionListener(new RefreshStateTrigger());
		enabledPanel.add(parent.soundsMusicCheckbox);
		
		JPanel volumePanel = new JPanel();
		volumePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		volumePanel.add(new JLabel("Volume: "));
		parent.soundsMusicSlider = new JSlider(
				Configuration.getMinBound("soundOptions", "musicVolume"),
				Configuration.getMaxBound("soundOptions", "musicVolume"),
				Configuration.getInt("soundOptions", "musicVolume"));
		volumePanel.add(parent.soundsMusicSlider);
		
		panel.add(enabledPanel);
		panel.add(volumePanel);
		
		return panel;
	}
	
	/**
	 * Setup sound effects settings panel
	 * 
	 * @return JPanel for sound effects settings
	 */
	private JPanel setupSoundsPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridLayout(2, 1));
		panel.setBorder(BorderFactory.createTitledBorder("Sound effects"));
		
		JPanel enabledPanel = new JPanel();
		enabledPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		parent.soundsSfxCheckbox = new JCheckBox("Enable sound effects");
		parent.soundsSfxCheckbox.setSelected(
				Configuration.getBoolean("soundOptions", "soundsEnabled"));
		parent.soundsSfxCheckbox.addActionListener(new RefreshStateTrigger());
		enabledPanel.add(parent.soundsSfxCheckbox);
		
		JPanel volumePanel = new JPanel();
		volumePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		volumePanel.add(new JLabel("Volume: "));
		parent.soundsSfxSlider = new JSlider(
				Configuration.getMinBound("soundOptions", "soundsVolume"),
				Configuration.getMaxBound("soundOptions", "soundsVolume"),
				Configuration.getInt("soundOptions", "soundsVolume"));
		volumePanel.add(parent.soundsSfxSlider);
		
		panel.add(enabledPanel);
		panel.add(volumePanel);
		
		return panel;
	}
	
	/**
	 * ActionListener for refreshing state
	 */
	private class RefreshStateTrigger implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			refreshState();
		}
	}
	
	/**
	 * Refresh checkboxes/sliders state
	 */
	private void refreshState() {
		if (!parent.soundsGlobalCheckbox.isSelected()) {
			parent.soundsGlobalSlider.setEnabled(false);
			parent.soundsMusicCheckbox.setEnabled(false);
			parent.soundsMusicSlider.setEnabled(false);
			parent.soundsSfxCheckbox.setEnabled(false);
			parent.soundsSfxSlider.setEnabled(false);
			musicPanel.setEnabled(false);
			soundsPanel.setEnabled(false);
		} else {
			parent.soundsGlobalSlider.setEnabled(true);
			parent.soundsMusicCheckbox.setEnabled(true);
			parent.soundsSfxCheckbox.setEnabled(true);
			parent.soundsMusicSlider.setEnabled(parent.soundsMusicCheckbox.isSelected());
			parent.soundsSfxSlider.setEnabled(parent.soundsSfxCheckbox.isSelected());
			musicPanel.setEnabled(true);
			soundsPanel.setEnabled(true);
		}
	}
}
