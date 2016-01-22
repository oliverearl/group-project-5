package uk.ac.aber.cs221.group5.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;

import uk.ac.aber.cs221.group5.logic.MemberList;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;

public class LoginWindowGUI {

	private JFrame frmLogIn;
	private JTextField txtEmailField;
	private MemberList memberList = new MemberList();
	
	/**
	 * Create the application.
	 */
	public LoginWindowGUI() {
		initialize();
	}
	
	/**
	 * Creates a new thread for the login window and
	 * sets that to visible through the event queue
	 * @throws Exception
	 */
	public void launchWindow() throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
					LoginWindowGUI window = new LoginWindowGUI();
					window.frmLogIn.setVisible(true);
			}
		});
	}
	
	public void passMemberList(MemberList recievingList){
		for(int memberCount = 0; memberCount < recievingList.getLength(); memberCount++){
			memberList.addMember(recievingList.getMember(memberCount));
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmLogIn = new JFrame();
		frmLogIn.setTitle("Log In");
		frmLogIn.setResizable(false);
		frmLogIn.setBounds(100, 100, 296, 233);
		frmLogIn.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmLogIn.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblEmail = new JLabel("Email:");
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.insets = new Insets(0, 0, 5, 5);
		gbc_lblEmail.gridx = 1;
		gbc_lblEmail.gridy = 1;
		frmLogIn.getContentPane().add(lblEmail, gbc_lblEmail);
		
		txtEmailField = new JTextField();
		GridBagConstraints gbc_txtEmailField = new GridBagConstraints();
		gbc_txtEmailField.gridwidth = 2;
		gbc_txtEmailField.insets = new Insets(0, 0, 5, 5);
		gbc_txtEmailField.anchor = GridBagConstraints.NORTH;
		gbc_txtEmailField.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEmailField.gridx = 2;
		gbc_txtEmailField.gridy = 1;
		frmLogIn.getContentPane().add(txtEmailField, gbc_txtEmailField);
		txtEmailField.setColumns(10);
		
		JButton btnLogin = new JButton("Log In");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				if(memberList.memberExists(txtEmailField.getText())){
					frmLogIn.dispose();
				}
				else{
					JOptionPane.showMessageDialog(null, "Error: Login Failed - Check your email was entered correctly", "InfoBox: Login Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		GridBagConstraints gbc_btnLogin = new GridBagConstraints();
		gbc_btnLogin.gridwidth = 2;
		gbc_btnLogin.insets = new Insets(0, 0, 5, 5);
		gbc_btnLogin.gridx = 2;
		gbc_btnLogin.gridy = 3;
		frmLogIn.getContentPane().add(btnLogin, gbc_btnLogin);
		
		JButton btnCancel = new JButton("Cancel");
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.gridwidth = 2;
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 4;
		frmLogIn.getContentPane().add(btnCancel, gbc_btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				System.exit(0);	//Terminates the program
			}
		});
		
		
	}
	
}
