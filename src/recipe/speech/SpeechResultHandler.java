/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech;

import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.IOException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.SwingUtilities;

/**
 *
 * @author Michael
 */
public abstract class SpeechResultHandler extends Thread  {
    private HashMap<String,SpeechCommandHandler> commands;
    private boolean bRunning = true;
    private LinkedList<SpeechEventListener> listeners;
    
    public SpeechResultHandler() {
        commands = new HashMap<String, SpeechCommandHandler>();
        listeners = new LinkedList<SpeechEventListener>();
    }
    
    public void addListener(SpeechEventListener listener) {
        listeners.add(listener);
    }
    
    public void go() throws GrammarException, JSGFGrammarParseException, 
            IOException, JSGFGrammarException {
        RASpeechRecognizer.getInstance().loadGrammar();
        loadCommands();
        RASpeechRecognizer.getInstance().start(this);
    }
    
    protected void addAction(String actionTrigger, SpeechCommandHandler actionCommand) {
        if (!commands.containsKey(actionTrigger))
            commands.put(actionTrigger, actionCommand);
    }
        
    protected void loadCommandRule(String name, String rule, 
            String trigger, String triggerArg ) throws GrammarException, 
            IOException, JSGFGrammarParseException, JSGFGrammarException {
        String ruleText = rule + "{ this.command=\""+trigger+"\"; "
                    + " this.arg=\""+triggerArg+"\"; }";
        System.out.println(ruleText);
        RASpeechRecognizer.getInstance().addRule(name, ruleText);
    }
    
    public void stopRecognition() {
        bRunning = false;
    }
    
    public abstract void loadCommands() throws GrammarException;
    
    public void executeListeners(int state,String arg) {
        for (SpeechEventListener listener: listeners) {
            SwingUtilities.invokeLater(new DoHandle(listener,state,arg));  
        }
    }
    
    private class DoHandle implements Runnable {
        public SpeechEventListener listener;
        public int state;
        public String arg;
        public DoHandle(SpeechEventListener listener,int state,String arg){
            this.listener = listener;
            this.state = state;
            this.arg = arg;
        }
        @Override
        public void run() {
            listener.handleEvent(state, arg);
        }
    }
    
    @Override
    public synchronized void run() {
        executeListeners(0,"");
        while (bRunning) {
            Result result = RASpeechRecognizer.getInstance().getRecognizer().recognize();
            if (result != null) {
                String bestResult = result.getBestFinalResultNoFiller();
                if (!bestResult.equals("")) {
                    executeListeners(1,bestResult);
                    RuleGrammar ruleGrammar = RASpeechRecognizer.getInstance().getRuleGrammar();
                    try {
                        RuleParse ruleParse = ruleGrammar.parse(bestResult, null);
                        if (ruleParse != null) {
                            RASpeechRecognizer.getTagsParser().parseTags(ruleParse);
                            String command = (String) RASpeechRecognizer.getTagsParser().get("command");
                            String arg = (String) RASpeechRecognizer.getTagsParser().get("arg");
                            System.out.println("\n  " + command +' ' + arg + '\n');
                            commands.get(command).doCommand(arg,this);
                            RASpeechRecognizer.getInstance().clearMic();
                        }
                    } catch (GrammarException ex) {
                        System.out.println("Result action failed:"+ex.getMessage());
                    }
                }
            }
            executeListeners(0,"");
        }
        executeListeners(-1,"");
    }
}
