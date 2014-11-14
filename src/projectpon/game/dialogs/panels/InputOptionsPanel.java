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
import projectpon.game.dialogs.InputChangeDialog;
import projectpon.game.dialogs.OptionsDialog;

public class InputOptionsPanel extends JPanel {
	private static final long serialVersionUID = 3653085506607854000L;
	
	private OptionsDialog parent;
	private JPanel primaryInputConfigPanel;
	private JPanel secondaryInputConfigPanel;
	
	private JButton primaryInputKeyUpButton;
	private JButton primaryInputKeyDownButton;
	private JButton primaryInputKeyLaunchButton;
	private JButton primaryInputMbLaunchButton;
	
	private JButton secondaryInputKeyUpButton;
	private JButton secondaryInputKeyDownButton;
	private JButton secondaryInputKeyLaunchButton;
	private JButton secondaryInputMbLaunchButton; 
	
	public InputOptionsPanel(OptionsDialog o) {
		parent = o;
		
		this.setBorder(new EmptyBorder(16, 16, 16, 16));
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 16));
		this.add(setupPrimaryInputPanel());
		this.add(setupSecondaryInputPanel());
		
		refreshState();
		refreshButtonLabel();
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
		parent.primaryMbLaunch = Configuration.getInt("inputPrimaryPlayer",
				"mbLaunch");
		primaryInputMbLaunchButton = new JButton();
		primaryInputMbLaunchButton.addActionListener(
				new InputChangePopupTrigger(primaryInputMbLaunchButton));
		inputMousePanel.add(primaryInputMbLaunchButton);
		
		JPanel inputKeyboardPanel = new JPanel();
		inputKeyboardPanel.setLayout(new GridLayout(5, 1, 0, 1));
		
		JPanel inputKeyboardUpKeyPanel = new JPanel();
		inputKeyboardUpKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardUpKeyPanel.add(new JLabel("Up key: "));
		parent.primaryKeyUp = Configuration.getInt("inputPrimaryPlayer",
				"keyUp");
		primaryInputKeyUpButton = new JButton();
		primaryInputKeyUpButton.addActionListener(
				new InputChangePopupTrigger(primaryInputKeyUpButton));
		inputKeyboardUpKeyPanel.add(primaryInputKeyUpButton);
		JPanel inputKeyboardDownKeyPanel = new JPanel();
		inputKeyboardDownKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardDownKeyPanel.add(new JLabel("Down key: "));
		parent.primaryKeyDown = Configuration.getInt("inputPrimaryPlayer",
				"keyDown");
		primaryInputKeyDownButton = new JButton();
		primaryInputKeyDownButton.addActionListener(
				new InputChangePopupTrigger(primaryInputKeyDownButton));
		inputKeyboardDownKeyPanel.add(primaryInputKeyDownButton);
		JPanel inputKeyboardLaunchKeyPanel = new JPanel();
		inputKeyboardLaunchKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardLaunchKeyPanel.add(new JLabel("Launch key: "));
		parent.primaryKeyLaunch = Configuration.getInt("inputPrimaryPlayer",
				"keyLaunch");
		primaryInputKeyLaunchButton = new JButton();
		primaryInputKeyLaunchButton.addActionListener(
				new InputChangePopupTrigger(primaryInputKeyLaunchButton));
		inputKeyboardLaunchKeyPanel.add(primaryInputKeyLaunchButton);
		JPanel inputKeyboardSpeedPanel = new JPanel();
		inputKeyboardSpeedPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardSpeedPanel.add(new JLabel("Key speed: "));
		parent.primaryKeySpeedSlider = new JSlider(
				Configuration.getMinBound("inputPrimaryPlayer", "keySpeed"),
				Configuration.getMaxBound("inputPrimaryPlayer", "keySpeed"),
				Configuration.getInt("inputPrimaryPlayer", "keySpeed"));
		inputKeyboardSpeedPanel.add(parent.primaryKeySpeedSlider);
		
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
	
	private JPanel setupSecondaryInputPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Secondary input (Player 2)"));
		panel.setLayout(new BorderLayout());
		
		JPanel inputTypePanel = new JPanel();
		inputTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
		inputTypePanel.add(new JLabel("Input device: "));
		
		parent.secondaryInputTypeMouseRadio = new JRadioButton("Mouse");
		parent.secondaryInputTypeKeyboardRadio = new JRadioButton("Keyboard");
		ButtonGroup inputSelect = new ButtonGroup();
		inputSelect.add(parent.secondaryInputTypeMouseRadio);
		inputSelect.add(parent.secondaryInputTypeKeyboardRadio);
		
		inputTypePanel.add(parent.secondaryInputTypeMouseRadio);
		inputTypePanel.add(parent.secondaryInputTypeKeyboardRadio);
		
		parent.secondaryInputTypeMouseRadio.addActionListener(new RefreshStateTrigger());
		parent.secondaryInputTypeKeyboardRadio.addActionListener(new RefreshStateTrigger());
		
		if (Configuration.get("inputSecondaryPlayer", "type").equals("mouse")) {
			parent.secondaryInputTypeMouseRadio.setSelected(true);
		} else if (Configuration.get("inputSecondaryPlayer", "type").equals("keyboard")) {
			parent.secondaryInputTypeKeyboardRadio.setSelected(true);
		}
		
		secondaryInputConfigPanel = new JPanel();
		secondaryInputConfigPanel.setLayout(new CardLayout());
		
		JPanel inputMousePanel = new JPanel();
		inputMousePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
		inputMousePanel.add(new JLabel("Launch button: "));
		parent.secondaryMbLaunch = Configuration.getInt("inputSecondaryPlayer",
				"mbLaunch");
		secondaryInputMbLaunchButton = new JButton();
		secondaryInputMbLaunchButton.addActionListener(
				new InputChangePopupTrigger(secondaryInputMbLaunchButton));
		inputMousePanel.add(secondaryInputMbLaunchButton);
		
		JPanel inputKeyboardPanel = new JPanel();
		inputKeyboardPanel.setLayout(new GridLayout(5, 1, 0, 1));
		
		JPanel inputKeyboardUpKeyPanel = new JPanel();
		inputKeyboardUpKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardUpKeyPanel.add(new JLabel("Up key: "));
		parent.secondaryKeyUp = Configuration.getInt("inputSecondaryPlayer",
				"keyUp");
		secondaryInputKeyUpButton = new JButton();
		secondaryInputKeyUpButton.addActionListener(
				new InputChangePopupTrigger(secondaryInputKeyUpButton));
		inputKeyboardUpKeyPanel.add(secondaryInputKeyUpButton);
		JPanel inputKeyboardDownKeyPanel = new JPanel();
		inputKeyboardDownKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardDownKeyPanel.add(new JLabel("Down key: "));
		parent.secondaryKeyDown = Configuration.getInt("inputSecondaryPlayer",
				"keyDown");
		secondaryInputKeyDownButton = new JButton();
		secondaryInputKeyDownButton.addActionListener(
				new InputChangePopupTrigger(secondaryInputKeyDownButton));
		inputKeyboardDownKeyPanel.add(secondaryInputKeyDownButton);
		JPanel inputKeyboardLaunchKeyPanel = new JPanel();
		inputKeyboardLaunchKeyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardLaunchKeyPanel.add(new JLabel("Launch key: "));
		parent.secondaryKeyLaunch = Configuration.getInt("inputSecondaryPlayer",
				"keyLaunch");
		secondaryInputKeyLaunchButton = new JButton();
		secondaryInputKeyLaunchButton.addActionListener(
				new InputChangePopupTrigger(secondaryInputKeyLaunchButton));
		inputKeyboardLaunchKeyPanel.add(secondaryInputKeyLaunchButton);
		JPanel inputKeyboardSpeedPanel = new JPanel();
		inputKeyboardSpeedPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
		inputKeyboardSpeedPanel.add(new JLabel("Key speed: "));
		parent.secondaryKeySpeedSlider = new JSlider(
				Configuration.getMinBound("inputSecondaryPlayer", "keySpeed"),
				Configuration.getMaxBound("inputSecondaryPlayer", "keySpeed"),
				Configuration.getInt("inputPrimaryPlayer", "keySpeed"));
		inputKeyboardSpeedPanel.add(parent.secondaryKeySpeedSlider);
		
		inputKeyboardPanel.add(inputKeyboardUpKeyPanel);
		inputKeyboardPanel.add(inputKeyboardDownKeyPanel);
		inputKeyboardPanel.add(inputKeyboardLaunchKeyPanel);
		inputKeyboardPanel.add(Box.createRigidArea(new Dimension(1, 1)));
		inputKeyboardPanel.add(inputKeyboardSpeedPanel);
		
		secondaryInputConfigPanel.add(inputMousePanel, "Mouse");
		secondaryInputConfigPanel.add(inputKeyboardPanel, "Keyboard");
		
		panel.add(inputTypePanel, BorderLayout.NORTH);
		panel.add(secondaryInputConfigPanel, BorderLayout.CENTER);
		
		return panel;
	}
	
	private class RefreshStateTrigger implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			refreshState();
		}
	}
	
	private void refreshState() {
		CardLayout clp = (CardLayout) primaryInputConfigPanel.getLayout();
		if (parent.primaryInputTypeMouseRadio.isSelected()) {
			clp.show(primaryInputConfigPanel, "Mouse");
		} else if (parent.primaryInputTypeKeyboardRadio.isSelected()) {
			clp.show(primaryInputConfigPanel, "Keyboard");
		}
		
		CardLayout cls = (CardLayout) secondaryInputConfigPanel.getLayout();
		if (parent.secondaryInputTypeMouseRadio.isSelected()) {
			cls.show(secondaryInputConfigPanel, "Mouse");
		} else if (parent.secondaryInputTypeKeyboardRadio.isSelected()) {
			cls.show(secondaryInputConfigPanel, "Keyboard");
		}
	}
	
	private static String getButtonText(int mb) {
		switch (mb){
		case MouseEvent.BUTTON1:
			return "Left button";
			
		case MouseEvent.BUTTON2:
			return "Middle button";
			
		case MouseEvent.BUTTON3:
			return "Right button";
			
		case 0:
			return "NONE";
			
		default:
			return String.format("Button %d", mb);
		}
	}
	
	private static String getKeyText(int key) {
		if (key == 0) {
			return "NONE";
		}
		
		return KeyEvent.getKeyText(key);
	}
	
	public void refreshButtonLabel() {
		primaryInputKeyUpButton.setText(
				getKeyText(parent.primaryKeyUp));
		primaryInputKeyDownButton.setText(
				getKeyText(parent.primaryKeyDown));
		primaryInputKeyLaunchButton.setText(
				getKeyText(parent.primaryKeyLaunch));
		primaryInputMbLaunchButton.setText(
				getButtonText(parent.primaryMbLaunch));
		
		secondaryInputKeyUpButton.setText(
				getKeyText(parent.secondaryKeyUp));
		secondaryInputKeyDownButton.setText(
				getKeyText(parent.secondaryKeyDown));
		secondaryInputKeyLaunchButton.setText(
				getKeyText(parent.secondaryKeyLaunch));
		secondaryInputMbLaunchButton.setText(
				getButtonText(parent.secondaryMbLaunch));
	}
	
	private class InputChangePopupTrigger implements ActionListener {
		private JButton caller;
		
		public InputChangePopupTrigger(JButton caller) {
			this.caller = caller;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			inputChangePopup(caller);
		}
	}
	
	private void inputChangePopup(JButton caller) {
		String input = "";
		String key = "";
		
		if (caller == primaryInputKeyUpButton) {
			input = "primary";
			key = "keyUp";
		} else if (caller == primaryInputKeyDownButton) {
			input = "primary";
			key = "keyDown";
		} else if (caller == primaryInputKeyLaunchButton) {
			input = "primary";
			key = "keyLaunch";
		} else if (caller == primaryInputMbLaunchButton) {
			input = "primary";
			key = "mbLaunch";
		} else if (caller == secondaryInputKeyUpButton) {
			input = "secondary";
			key = "keyUp";
		} else if (caller == secondaryInputKeyDownButton) {
			input = "secondary";
			key = "keyDown";
		} else if (caller == secondaryInputKeyLaunchButton) {
			input = "secondary";
			key = "keyLaunch";
		} else if (caller == secondaryInputMbLaunchButton) {
			input = "secondary";
			key = "mbLaunch";
		}
		
		parent.launchChildDialog(new InputChangeDialog(parent, input, key));
	}
}
