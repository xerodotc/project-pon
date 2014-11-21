/**
 * InputChangeDialog.java
 * 
 * A dialog for listen newinput binding for changing input
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.dialogs;

import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import projectpon.engine.GameDialog;

public class InputChangeDialog extends GameDialog {
	private static final long serialVersionUID = 2137221810706374083L;

	/**
	 * Setup the whole dialog
	 * 
	 * @param parent		Parent OptionDialog
	 * @param input			Input to be changed
	 * @param key			Key to be changed
	 */
	public InputChangeDialog(final OptionsDialog parent, final String input, final String key) {
		super("Assign new input");
		
		String noun = "key";
		
		/*
		 * If input type is mouse
		 * also listen for mouse input
		 */
		if (key.equals("mbLaunch")) {
			noun = "button";
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (input.equals("primary")) {
						parent.primaryMbLaunch = e.getButton();
					} else if (input.equals("secondary")) {
						parent.secondaryMbLaunch = e.getButton();
					}
					dispose();
				}
			});
		}
		
		// Add key listener to listen for new input
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// ESC for cancel
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
					return;
				}
				
				switch (key) {
				case "keyUp":
					if (input.equals("primary")) {
						parent.primaryKeyUp = e.getKeyCode();
					} else if (input.equals("secondary")) {
						parent.secondaryKeyUp = e.getKeyCode();
					}
					break;
					
				case "keyDown":
					if (input.equals("primary")) {
						parent.primaryKeyDown = e.getKeyCode();
					} else if (input.equals("secondary")) {
						parent.secondaryKeyDown = e.getKeyCode();
					}
					break;
					
				case "keyLaunch":
					if (input.equals("primary")) {
						parent.primaryKeyLaunch = e.getKeyCode();
					} else if (input.equals("secondary")) {
						parent.secondaryKeyLaunch = e.getKeyCode();
					}
					break;
					
				default:
					return;
				}
				
				dispose();
			}
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panel.setBorder(new EmptyBorder(16, 16, 16, 16));
		panel.add(new JLabel("<html>Press a new " + noun + " to assign.<br>" +
				"Press ESC to cancel assignment.</html>"));
		
		this.add(panel);
		this.pack();
	}
}
