/**
 * AiOptionsPanel.java
 * 
 * A JPanel for AI settings tab
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.dialogs.panels;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

import th.in.xerodotc.projectpon.game.Configuration;
import th.in.xerodotc.projectpon.game.dialogs.OptionsDialog;

public class AiOptionsPanel extends JPanel {
	private static final long serialVersionUID = -6842047110373517916L;

	private OptionsDialog parent = null; // parent OptionDialog
	
	/**
	 * Setup the whole panel
	 * 
	 * @param o		Parent OptionDialog
	 */
	public AiOptionsPanel(OptionsDialog o) {
		parent = o;
		
		this.setBorder(new EmptyBorder(16, 16, 16, 16));
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.add(setupAiSpeedPanel());
	}
	
	/**
	 * Setup AI speed setting panel 
	 * 
	 * @return JPanel for AI speed setting
	 */
	private JPanel setupAiSpeedPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
		panel.add(new JLabel("CPU Player Speed: "));
		parent.aiSpeedSlider = new JSlider(
				Configuration.getMinBound("aiOptions", "speed"),
				Configuration.getMaxBound("aiOptions", "speed"),
				Configuration.getInt("aiOptions", "speed"));
		panel.add(parent.aiSpeedSlider);
		
		return panel;
	}
}
