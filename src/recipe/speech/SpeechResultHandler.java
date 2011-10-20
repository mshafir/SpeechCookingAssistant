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
        recognizer.addRule(name, rule + "{ this.command=\""+command+"\"; "
                + " this.arg=\""+arg+"\"; }");
        commands.put(command, handler);
    }
    
    protected void loadCommandRule(String name, String rule, 
            String command, String arg ) throws GrammarException, 
            IOException, JSGFGrammarParseException, JSGFGrammarException {
        recognizer.addRule(name, rule + "{ this.command=\""+command+"\"; "
                    + " this.arg=\""+arg+"\"; }");
    }
    
    public abstract void loadCommands() throws GrammarException;
    
    @Override
    public void run() {
        while (true) {
            Result result = recognizer.getRecognizer().recognize();
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
                }
            } catch (GrammarException ex) {
                Logger.getLogger(RASpeechRecognizer.class.getName()).log(
                        Level.SEVERE, null, ex);
                System.out.println("Result action failed:"+ex.getMessage());
            }
        }
    }
}
