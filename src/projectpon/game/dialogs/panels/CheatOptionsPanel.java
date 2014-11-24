/**
 * CheatOptionsPanel.java
 * 
 * A JPanel for cheat settings tab
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.dialogs.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import projectpon.game.SessionConfiguration;
import projectpon.game.dialogs.OptionsDialog;

public class CheatOptionsPanel extends JPanel {
	private static final long serialVersionUID = 2485963170473411523L;
	
	private OptionsDialog parent; // parent OptionDialog
	
	/**
	 * Setup the whole panel
	 * 
	 * @param o		Parent OptionDialog
	 */
	public CheatOptionsPanel(OptionsDialog o) {
		parent = o;
		
		this.setBorder(new EmptyBorder(16, 16, 16, 16));
		
		this.setLayout(new BorderLayout(0, 16));
		parent.cheatsEnableCheckbox = new JCheckBox("Enable cheat hotkeys");
		parent.cheatsEnableCheckbox.setSelected(SessionConfiguration.cheatsEnabled);
		this.add(parent.cheatsEnableCheckbox, BorderLayout.NORTH);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		String cheatsExplainText = "<html>";
		cheatsExplainText += "<u>Cheat hotkeys list</u><br>";
		cheatsExplainText += "I - Spawn new item<br>";
		cheatsExplainText += "S - Obtain sticky paddle item<br>";
		cheatsExplainText += "U - Revoke effects of the blind item<br>";
		cheatsExplainText += "W - Obtain wall item<br>";
		cheatsExplainText += "Y - Revoke effects of the invert item<br>";
		cheatsExplainText += "1 - Shrink paddle<br>";
		cheatsExplainText += "2 - Expand paddle<br>";
		cheatsExplainText += "<br>";
		cheatsExplainText += "These hotkeys only work in play against<br>computer mode.";
		cheatsExplainText += "</html>";
		panel.add(new JLabel(cheatsExplainText));
		this.add(panel, BorderLayout.CENTER);
	}
}
