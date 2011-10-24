/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech;

import recipe.speech.SpeechCommandHandler;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import java.io.IOException;
import java.net.URL;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.jsgf.JSGFRuleGrammar;
import edu.cmu.sphinx.jsgf.JSGFRuleGrammarManager;
import edu.cmu.sphinx.jsgf.rule.JSGFRule;
import edu.cmu.sphinx.tools.tags.ActionTagsParser;
import java.util.HashMap;

/**
 *
 * @author Michael
 */
public abstract class SpeechResultHandler extends Thread  {
    private RASpeechRecognizer recognizer;
    private HashMap<String,SpeechCommandHandler> commands;
    private boolean bRunning = true;
    
    public SpeechResultHandler(RASpeechRecognizer rec) {
        recognizer = rec;
        commands = new HashMap<String, SpeechCommandHandler>();
    }
    
    public void go() throws GrammarException {
        recognizer.clearRules();
        loadCommands();
        recognizer.start(this);
    }
    
    protected void loadCommandRule(String name, String rule, 
            String command, String arg, SpeechCommandHandler handler ) 
            throws GrammarException, IOException, JSGFGrammarParseException, JSGFGrammarException {
        this.loadCommandRule(name, rule, command, arg);
        if (!commands.containsKey(command))
            commands.put(command, handler);
    }
    
    protected void loadCommandRule(String name, String rule, 
            String command, String arg ) throws GrammarException, 
            IOException, JSGFGrammarParseException, JSGFGrammarException {
        String ruleText = rule + "{ this.command=\""+command+"\"; "
                    + " this.arg=\""+arg+"\"; }";
        System.out.println(ruleText);
        recognizer.addRule(name, ruleText);
        
    }
    
    public void stopRecognition() {
        bRunning = false;
    }
    
    public abstract void loadCommands() throws GrammarException;
    
    @Override
    public void run() {
        //recognizer.jsgfGrammar.dumpGrammar("test");
        while (bRunning) {
            Result result = recognizer.getRecognizer().recognize();
            if (result != null) {
                String bestResult = result.getBestFinalResultNoFiller();
                RuleGrammar ruleGrammar = recognizer.getRuleGrammar();
                //RuleGrammar ruleGrammar = new BaseRuleGrammar (jsapiRecognizer, 
                //        jsgfGrammar.getRuleGrammar());
                try {
                    RuleParse ruleParse = ruleGrammar.parse(bestResult, null);
                    if (ruleParse != null) {
                        recognizer.getTagsParser().parseTags(ruleParse);
                        String command = (String) recognizer.getTagsParser().get("command");
                        String arg = (String) recognizer.getTagsParser().get("arg");
                        System.out.println("\n  " + command +' ' + arg + '\n');
                        commands.get(command).doCommand(arg);
                        recognizer.microphone.clear();
                    }
                } catch (GrammarException ex) {
                    Logger.getLogger(RASpeechRecognizer.class.getName()).log(
                            Level.SEVERE, null, ex);
                    System.out.println("Result action failed:"+ex.getMessage());
                }
            }
        }
    }
}
