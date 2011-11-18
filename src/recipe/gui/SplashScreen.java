package recipe.gui;

import java.awt.Dimension;

import javax.swing.JLabel;

public class SplashScreen extends javax.swing.JFrame {
	
	private JLabel lblStatus;
	
	public SplashScreen() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sous-Chef");
        setBackground(new java.awt.Color(102, 102, 102));
        setBounds(new java.awt.Rectangle(0, 0, 600, 350));
        setFocusableWindowState(false);
        setForeground(java.awt.Color.black);
        setMinimumSize(new java.awt.Dimension(600, 350));
        setResizable(false);
        setLocationRelativeTo(null);
        
        JLabel lblTitle = new JLabel();
        lblTitle.setText("Recipe Assistant");
        lblTitle.setFont(new java.awt.Font("Comic Sans MS", 0, 35));
        lblTitle.setForeground(java.awt.Color.black);
        lblTitle.setBounds(300, 50, 300, 60);
        this.add(lblTitle);
        
        JLabel lblVersion = new JLabel();
        lblVersion.setText("Version 1.0");
        lblVersion.setFont(new java.awt.Font("Comic Sans MS", 0, 12));
        lblVersion.setForeground(java.awt.Color.black);
        lblVersion.setBounds(300, 110, 200, 20);
        this.add(lblVersion);
        
        lblStatus = new JLabel();
        lblStatus.setText("");
        lblStatus.setFont(new java.awt.Font("Comic Sans MS", 0, 12));
        lblStatus.setForeground(java.awt.Color.black);
        lblStatus.setBounds(300, 300, 200, 20);
        this.add(lblStatus);
        
        ImagePanel back = new ImagePanel("images/splash.png",
                new Dimension(this.getWidth(),this.getHeight()));
        this.add(back);
        
        this.setUndecorated(true);
	}
	
	public void setStatus(String text) {
		this.lblStatus.setText(text);
	}
}