package projectpon.game.dialogs.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

import projectpon.game.Configuration;
import projectpon.game.dialogs.OptionsDialog;

public class InputOptionsPanel extends JPanel {
	private static final long serialVersionUID = 3653085506607854000L;
	
	private OptionsDialog parent;
	private JPanel primaryInputConfigPanel;
	private JPanel secondaryInputConfigPanel;
	
	public InputOptionsPanel(OptionsDialog o) {
		parent = o;
		
		this.setBorder(new EmptyBorder(16, 16, 16, 16));
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 16));
		this.add(setupPrimaryInputPanel());
		
		refreshState();
	}
	
	private JPanel setupPrimaryInputPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Primary input"));
		panel.setLayout(new BorderLayout());
		
		JPanel inputTypePanel = new JPanel();
		inputTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
		inputTypePanel.add(new JLabel("Input device: "));
		
		parent.primaryInputTypeMouseRadio = new JRadioButton("Mouse");
		parent.primaryInputTypeKeyboardRadio = new JRadioButton("Keyboard");
		ButtonGroup inputSelect = new ButtonGroup();
		inputSelect.add(parent.primaryInputTypeMouseRadio);
		inputSelect.add(parent.primaryInputTypeKeyboardRadio);
		
		inputTypePanel.add(parent.primaryInputTypeMouseRadio);
		inputTypePanel.add(parent.primaryInputTypeKeyboardRadio);
		
		parent.primaryInputTypeMouseRadio.addActionListener(new RefreshStateTrigger());
		parent.primaryInputTypeKeyboardRadio.addActionListener(new RefreshStateTrigger());
		
		if (Configuration.get("inputPrimaryPlayer", "type").equals("mouse")) {
			parent.primaryInputTypeMouseRadio.setSelected(true);
		} else if (Configuration.get("inputPrimaryPlayer", "type").equals("keyboard")) {
			parent.primaryInputTypeKeyboardRadio.setSelected(true);
		}
		
		primaryInputConfigPanel = new JPanel();
		primaryInputConfigPanel.setLayout(new CardLayout());
		
		JPanel inputMousePanel = new JPanel();
		inputMousePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
		inputMousePanel.add(new JLabel("Launch button: "));
		inputMousePanel.add(new JButton("Left button"));
		
		JPanel inputKeyboardPanel = new JPanel();
		inputKeyboardPanel.setLayout(new GridLayout(5, 1, 0, 1));
		
		JPanel inputKeyboardUpKeyPanel = new JPanel();
		inputKeyboardUpKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardUpKeyPanel.add(new JLabel("Up key: "));
		inputKeyboardUpKeyPanel.add(new JButton(KeyEvent.getKeyText(KeyEvent.VK_UP)));
		JPanel inputKeyboardDownKeyPanel = new JPanel();
		inputKeyboardDownKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardDownKeyPanel.add(new JLabel("Down key: "));
		inputKeyboardDownKeyPanel.add(new JButton(KeyEvent.getKeyText(KeyEvent.VK_DOWN)));
		JPanel inputKeyboardLaunchKeyPanel = new JPanel();
		inputKeyboardLaunchKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardLaunchKeyPanel.add(new JLabel("Launch key: "));
		inputKeyboardLaunchKeyPanel.add(new JButton(KeyEvent.getKeyText(KeyEvent.VK_SPACE)));
		JPanel inputKeyboardSpeedPanel = new JPanel();
		inputKeyboardSpeedPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardSpeedPanel.add(new JLabel("Key speed: "));
		inputKeyboardSpeedPanel.add(new JSlider());
		
		inputKeyboardPanel.add(inputKeyboardUpKeyPanel);
		inputKeyboardPanel.add(inputKeyboardDownKeyPanel);
		inputKeyboardPanel.add(inputKeyboardLaunchKeyPanel);
		inputKeyboardPanel.add(Box.createRigidArea(new Dimension(1, 1)));
		inputKeyboardPanel.add(inputKeyboardSpeedPanel);
		
		primaryInputConfigPanel.add(inputMousePanel, "Mouse");
		primaryInputConfigPanel.add(inputKeyboardPanel, "Keyboard");
		
		panel.add(inputTypePanel, BorderLayout.NORTH);
		panel.add(primaryInputConfigPanel, BorderLayout.CENTER);
		
		return panel;
	}
	
	private class RefreshStateTrigger implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			refreshState();
		}
	}
	
	private void refreshState() {
		CardLayout cl = (CardLayout) primaryInputConfigPanel.getLayout();
		if (parent.primaryInputTypeMouseRadio.isSelected()) {
			cl.show(primaryInputConfigPanel, "Mouse");
		} else if (parent.primaryInputTypeKeyboardRadio.isSelected()) {
			cl.show(primaryInputConfigPanel, "Keyboard");
		}
	}
}
