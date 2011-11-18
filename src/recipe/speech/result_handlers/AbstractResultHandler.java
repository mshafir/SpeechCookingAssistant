/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech.result_handlers;

import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.IOException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import javax.swing.SwingUtilities;

import recipe.interfaces.ISpeechCommandHandler;
import recipe.interfaces.ISpeechEventListener;
import recipe.speech.RASpeechRecognizer;
import recipe.speech.RecognizerState;

/**
 * This is the abstract class for handling speech results.
 * @author Michael
 */
public abstract class AbstractResultHandler extends Thread  {
    private HashMap<String,ISpeechCommandHandler> commands;
    private boolean bRunning = true;
    private LinkedList<ISpeechEventListener> listeners;
    
    public AbstractResultHandler() {
        commands = new HashMap<String, ISpeechCommandHandler>();
        listeners = new LinkedList<ISpeechEventListener>();
    }
    
    /**
     * Add a listener that responds to recognizer state.
     * @param listener
     */
    public void addListener(ISpeechEventListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Loads the grammar and begins recongition.
     * @throws GrammarException
     * @throws JSGFGrammarParseException
     * @throws IOException
     * @throws JSGFGrammarException
     */
    public void go() throws GrammarException, JSGFGrammarParseException, 
            IOException, JSGFGrammarException {
        RASpeechRecognizer.getInstance().loadGrammar();
        if (loadCommands())
            RASpeechRecognizer.getInstance().start(this);
    }
    
    /**
     * Registers a command that a grammar rule can trigger by its name.
     * Command names map uniquely to SpeechCommandHandler objects that perform the command. 
     * @param commandName
     * @param command
     */
    protected void registerCommand(String commandName, ISpeechCommandHandler command) {
        if (!commands.containsKey(commandName))
            commands.put(commandName, command);
    }
        
    /**
     * Adds a rule to the grammar.
     * @param grammarRule  the rule to add
     * @param command  the registered command this rule triggers
     * @param commandArg  an argument to be passed to that command when the rule is triggered
     * @throws GrammarException
     * @throws IOException
     * @throws JSGFGrammarParseException
     * @throws JSGFGrammarException
     */
    protected void addGrammarRule(String grammarRule, 
            String command, String commandArg ) throws GrammarException, 
            IOException, JSGFGrammarParseException, JSGFGrammarException {
       String uid = UUID.randomUUID().toString().replaceAll("-", "");
       addGrammarRule("r"+uid,grammarRule,command,commandArg);
    }
    
    protected void addGrammarRule(String ruleName, String grammarRule, 
            String command, String commandArg ) throws GrammarException, 
            IOException, JSGFGrammarParseException, JSGFGrammarException {
    	String ruleText = grammarRule + "{ this.command=\""+command+"\"; "
        						+ " this.arg=\""+commandArg+"\"; }";
		System.out.println(ruleText);
		RASpeechRecognizer.getInstance().addRule(ruleName, ruleText);
    }
    
    /**
     * Stops the result recognition loop.
     */
    public void stopRecognition() {
        bRunning = false;
    }
    
    public abstract boolean loadCommands();
    
    /**
     * Notifies listeners of a state change in the recognizer.
     * @param state
     * @param arg
     */
    public void executeListeners(RecognizerState state,String arg) {
        for (ISpeechEventListener listener: listeners) {
            SwingUtilities.invokeLater(new DoHandle(listener,state,arg));  
        }
    }
    
    private class DoHandle implements Runnable {
        public ISpeechEventListener listener;
        public RecognizerState state;
        public String arg;
        public DoHandle(ISpeechEventListener listener,RecognizerState state,String arg){
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
        executeListeners(RecognizerState.Ready,"");
        while (bRunning) {
            Result result = RASpeechRecognizer.getInstance().getRecognizer().recognize();
            if (result != null) {
                String bestResult = result.getBestFinalResultNoFiller();
                if (!bestResult.equals("")) {
                    executeListeners(RecognizerState.Processing,bestResult);
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
            executeListeners(RecognizerState.Ready,"");
        }
        executeListeners(RecognizerState.Off,"");
    }
}
