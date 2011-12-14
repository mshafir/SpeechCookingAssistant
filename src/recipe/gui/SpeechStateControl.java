package recipe.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import recipe.core.Recipe;
import recipe.interfaces.ISpeechEventListener;
import recipe.speech.RASpeechRecognizer;
import recipe.speech.RecognizerState;

public class SpeechStateControl extends javax.swing.JComponent implements ISpeechEventListener {
    
	JLabel lblQuestion;
	JLabel lblResponse;
	JButton question;
	HashMap<RecognizerState,ImagePanel> stateImages;
	
    public SpeechStateControl(Dimension size) {
    	this.setOpaque(false);
    	setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
    	
        lblQuestion = new JLabel();
    	lblResponse = new JLabel();
    	this.add(lblQuestion);
    	this.add(lblResponse);
    	lblQuestion.setBounds(200,this.getHeight()-90,500,20);
    	lblQuestion.setForeground(Color.WHITE);
    	lblResponse.setBounds(200,this.getHeight()-70,500,40);
    	lblResponse.setForeground(Color.WHITE);
        
    	stateImages = new HashMap<RecognizerState, ImagePanel>();
    	addStateImage(RecognizerState.Ready,"images/speaker0.png");
    	addStateImage(RecognizerState.Processing,"images/speaker1.png");
    	addStateImage(RecognizerState.Speaking,"images/speaker2.png");
    	
    	question = new JButton(new ImageIcon("images/question.png"));
    	question.setOpaque(true);
    	question.setBounds(this.getWidth()-82, this.getHeight()-100,64,64);
    	question.setVisible(false);
    	question.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
            	RASpeechRecognizer.getInstance().help();
            }
        });
    	this.add(question);
    	
    	ImagePanel bar = new ImagePanel("images/bar.png",
                new Dimension(this.getWidth(),100));
    	bar.setVisible(true);
        this.add(bar);
        bar.setBounds(0, this.getHeight()-100, bar.getWidth(), bar.getHeight());
    }
    
    private final void addStateImage(RecognizerState state, String path) {
    	ImagePanel img = new ImagePanel(path, new Dimension(150,150));
    	img.setBounds(0,0,img.getWidth(),img.getHeight());
    	img.setVisible(false);
    	this.add(img);
    	stateImages.put(state, img);
    }
    
    @Override
    public void handleEvent(RecognizerState state, String arg) {
        setSpeaker(state);
        if (state == RecognizerState.Processing) {
            lblQuestion.setText("<html><p>"+
            		arg.replaceAll("sue chef", "Sous-chef,") +"?"+
            		"</p></html>");
            lblResponse.setText("...");
        } else if (state == RecognizerState.Speaking) {
            lblResponse.setText("<html><p>"+arg+"</p></html>");
        } else if (state == RecognizerState.Off) {
        	lblQuestion.setText("");
        	lblResponse.setText("");
        }
    }
    
    private void setSpeaker(RecognizerState state) {
    	for (ImagePanel p : stateImages.values()) {
    		p.setVisible(false);
    		question.setVisible(false);
    	}
    	if (stateImages.containsKey(state))
    	{
    		stateImages.get(state).setVisible(true);
    		question.setVisible(true);
    	}
    }

}
