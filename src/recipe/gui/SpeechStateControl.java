package recipe.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JLabel;

import recipe.interfaces.ISpeechEventListener;
import recipe.speech.RecognizerState;

public class SpeechStateControl extends javax.swing.JComponent implements ISpeechEventListener {
    
	JLabel lblQuestion;
	JLabel lblResponse;
	HashMap<RecognizerState,ImagePanel> stateImages;
	
    public SpeechStateControl(Dimension size) {
    	this.setOpaque(false);
    	setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
    	
    	stateImages = new HashMap<RecognizerState, ImagePanel>();
    	addStateImage(RecognizerState.Ready,"images/speaker0.png");
    	addStateImage(RecognizerState.Processing,"images/speaker1.png");
    	addStateImage(RecognizerState.Speaking,"images/speaker2.png");
    	
    	ImagePanel bar = new ImagePanel("images/bar.png",
                new Dimension(this.getWidth(),100));
    	bar.setVisible(true);
        this.add(bar);
        bar.setBounds(0, this.getHeight()-100, bar.getWidth(), bar.getHeight());
    	
    	lblQuestion = new JLabel();
    	lblResponse = new JLabel();
    	lblQuestion.setBounds(200,30,500,20);
    	lblQuestion.setForeground(Color.WHITE);
    	lblResponse.setBounds(200,60,500,20);
    	lblResponse.setForeground(Color.WHITE);
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
            lblQuestion.setText(arg.replaceAll("sue chef", "Sous-chef,") +"?");
            lblResponse.setText("...");
        } else if (state == RecognizerState.Speaking) {
            lblResponse.setText(arg);
        }
    }
    
    private void setSpeaker(RecognizerState state) {
    	for (ImagePanel p : stateImages.values()) {
    		p.setVisible(false);
    	}
    	if (stateImages.containsKey(state))
    		stateImages.get(state).setVisible(true);
    }

}
