//Imports are listed in full to show what's being used
//could just import javax.swing.* and java.awt.* etc..
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.UIManager;
import javax.swing.Icon;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;

public class MessageDialogFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7169507698351284621L;
	private JPanel confirmOptBtnPanel;
	private JPanel msgTypeBtnPanel;
	private JPanel textPanel;
	private JPanel optPanel;
	private JPanel dialogTypePanel;
	private JPanel makeDialogPanel;
	private JPanel optionTypePanel;
	private ButtonGroup confirmOptBtnGrp;
	private ButtonGroup msgTypeBtnGrp;
	private ButtonGroup chooseOptPaneGrp;
	private ButtonGroup optionBtnGrp;
	private JTextField messageText;
	private JTextField titleText;
	private JTextArea tracker;

	//Using some standard Java icons
	private Icon optionIcon = UIManager.getIcon("FileView.computerIcon");
	private Icon warningIcon = UIManager.getIcon("OptionPane.warningIcon");
	private Icon infoIcon = UIManager.getIcon("OptionPane.informationIcon");
	private Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");

	//Application start point   
	public static void main(String[] args) {

		//Use the event dispatch thread for Swing components
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				//create GUI frame
				new MessageDialogFrame().setVisible(true);          
			}
		});

	}


	public MessageDialogFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Dialog Box Example");
		setSize(500,450);
		setLocationRelativeTo(null);

		setLayout(new BorderLayout());

		optPanel = new JPanel();
		optPanel.setLayout(new GridLayout(2,2));

		add(optPanel,BorderLayout.NORTH);


		textPanel = new JPanel();
		textPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		JLabel messageLbl = new JLabel("Enter Dialog Message:");
		messageText = new JTextField("Default Message",20);


		JLabel titleLbl = new JLabel("Enter Dialog Title:");
		titleText  = new JTextField("Default Title",20);

		textPanel.add(titleLbl);
		textPanel.add(titleText);
		textPanel.add(messageLbl);
		textPanel.add(messageText);
		optPanel.add(textPanel);

		JRadioButton confirmButton = new JRadioButton("Confirm Dialog");
		confirmButton.setActionCommand("Confirm Dialog");
		confirmButton.setSelected(true);
		confirmButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{

				confirmOptBtnPanel.setVisible(true); 
				optionTypePanel.setVisible(false);
				msgTypeBtnPanel.setVisible(true);
			}
		}
				);

		JRadioButton optionButton = new JRadioButton("Option Dialog");
		optionButton.setActionCommand("Option Dialog");
		optionButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{

				confirmOptBtnPanel.setVisible(false); 
				optionTypePanel.setVisible(true);
				msgTypeBtnPanel.setVisible(false);
			}
		}
				);

		JRadioButton messageButton = new JRadioButton("Message Dialog");
		messageButton.setActionCommand("Message Dialog");   
		messageButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{

				confirmOptBtnPanel.setVisible(false);
				optionTypePanel.setVisible(false);
				msgTypeBtnPanel.setVisible(true);
			}
		}
				);

		chooseOptPaneGrp = new ButtonGroup();
		chooseOptPaneGrp.add(confirmButton);
		chooseOptPaneGrp.add(messageButton);
		chooseOptPaneGrp.add(optionButton);

		dialogTypePanel = new JPanel();
		dialogTypePanel.setLayout(new BoxLayout(dialogTypePanel, BoxLayout.Y_AXIS));
		dialogTypePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		dialogTypePanel.add(confirmButton);
		dialogTypePanel.add(messageButton);
		dialogTypePanel.add(optionButton);
		optPanel.add(dialogTypePanel);


		msgTypeBtnGrp = new ButtonGroup();
		msgTypeBtnPanel = MakeButtonGroupPanel(msgTypeBtnGrp,"ERROR_MESSAGE", "INFORMATION_MESSAGE"
				, "WARNING_MESSAGE", "QUESTION_MESSAGE", "PLAIN_MESSAGE");
		optPanel.add(msgTypeBtnPanel);

		confirmOptBtnGrp = new ButtonGroup();
		confirmOptBtnPanel = MakeButtonGroupPanel(confirmOptBtnGrp,"YES_NO_OPTION","YES_NO_CANCEL_OPTION"
				,"OK_CANCEL_OPTION");
		optPanel.add(confirmOptBtnPanel);  



		optionTypePanel = new JPanel();
		optionTypePanel.setLayout(new GridLayout(2,1));
		optionTypePanel.setBorder(BorderFactory.createLineBorder(Color.black));



		JRadioButton stringButton = new JRadioButton("'Press Me!', ' No...Press Me!', 'Me! ME! ME!'");
		stringButton.setActionCommand("'Press Me!', ' No...Press Me!', 'Me! ME! ME!'");
		stringButton.setSelected(true);

		JRadioButton iconButton = new JRadioButton("Icons:");
		iconButton.setActionCommand("Icons:");

		JLabel warningLabel = new JLabel(warningIcon);
		JLabel infoLabel = new JLabel(infoIcon);
		JLabel errorLabel = new JLabel(errorIcon);

		optionTypePanel.add(stringButton);
		JPanel iconPanel = new JPanel();
		iconPanel.add(iconButton);
		iconPanel.add(warningLabel);
		iconPanel.add(infoLabel);
		iconPanel.add(errorLabel);
		optionTypePanel.add(iconPanel);
		optionTypePanel.setVisible(false);

		optionBtnGrp = new ButtonGroup();
		optionBtnGrp.add(stringButton);
		optionBtnGrp.add(iconButton);

		add(optionTypePanel,BorderLayout.WEST);

		tracker = new JTextArea("Click Tracker:");
		tracker.setEnabled(false);
		tracker.setLineWrap(true);
		tracker.setBorder(BorderFactory.createLineBorder(Color.black));


		add(tracker,BorderLayout.CENTER);

		makeDialogPanel = new JPanel();
		JButton makeDialog = new JButton("Make Dialog");
		makeDialog.setActionCommand("Make Dialog");
		makeDialog.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				CreateDialog();
			}
		});

		makeDialogPanel.add(makeDialog);
		add(makeDialogPanel, BorderLayout.SOUTH);

	}


	private JPanel MakeButtonGroupPanel(ButtonGroup btnGroup, String... buttons)
	{
		JPanel btnGrpPanel = new JPanel();
		btnGrpPanel.setLayout(new BoxLayout(btnGrpPanel, BoxLayout.Y_AXIS));
		btnGrpPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		for (String button : buttons )
		{
			JRadioButton radioButton = new JRadioButton(button);
			radioButton.setActionCommand(button);
			btnGroup.add(radioButton);
			btnGrpPanel.add(radioButton);
			if (button.equals(buttons[0]))
			{
				radioButton.setSelected(true);
			}
		}

		return btnGrpPanel;

	}

	private int GetFieldValue(String selected)
	{
		try{
			Field JOptField = JOptionPane.class.getField(selected);

			//Pass null as it's a static field
			return JOptField.getInt(null);
		}
		catch (Exception e)
		{
			//default field value 
			return 0;
		}

	}


	private void CreateDialog()
	{
		int messageType = GetFieldValue(msgTypeBtnGrp.getSelection().getActionCommand());
		int buttonsType = GetFieldValue(confirmOptBtnGrp.getSelection().getActionCommand());

		String message = messageText.getText();
		String title = titleText.getText();

		if (chooseOptPaneGrp.getSelection().getActionCommand().equals("Message Dialog")) 
		{    
			JOptionPane.showMessageDialog(this, message, title, messageType);
			tracker.setText("Message Dialog...");
		}
		else if(chooseOptPaneGrp.getSelection().getActionCommand().equals("Option Dialog"))
		{
			Object[] options = getChoices();
			int choice = JOptionPane.showOptionDialog(this, message, title, buttonsType, messageType
					,optionIcon, options, options[0]);

			String clicked = (choice == -1)?"dialog closed..":options[choice].toString() + " button clicked";
			tracker.setText(clicked);
		}
		else
		{
			int choice = JOptionPane.showConfirmDialog(this, message, title, buttonsType, messageType);
			String clicked = (choice == -1)?"dialog closed..":"button "+ choice + " clicked";
			tracker.setText(clicked);
		}
	}

	private Object[] getChoices()
	{  
		if (optionBtnGrp.getSelection().getActionCommand().equals("Icons:"))
		{
			return new Icon[] {errorIcon, infoIcon, warningIcon};
		}
		else
		{
			return new String[] {"Me! ME! ME!", "No...Press Me!", "Press Me!"};
		}

	}


}
