/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech;

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
import recipe.MainWindow;

/**
 *
 * @author Michael
 */
public class RASpeechRecognizer {
    ConfigurationManager cm;
    Recognizer recognizer;
    BaseRecognizer jsapiRecognizer;
    Microphone microphone;
    JSGFGrammar jsgfGrammar;
    ResultListener listener;
    boolean started;
    
    public RASpeechRecognizer() 
            throws EngineException, GrammarException, IOException, 
            JSGFGrammarParseException, JSGFGrammarException {
        cm = new ConfigurationManager(MainWindow.class.getResource("jsgf.config.xml"));
        recognizer = (Recognizer) cm.lookup("recognizer");
        jsgfGrammar = (JSGFGrammar) cm.lookup("jsgfGrammar");
        microphone = (Microphone) cm.lookup("microphone");
        jsapiRecognizer = new BaseRecognizer(jsgfGrammar.getGrammarManager());
        jsapiRecognizer.allocate();
        //recognizer.allocate();
        
        System.out.print(" Loading recognizer ...");
        recognizer.allocate();
        System.out.println(" Ready");
        
        jsgfGrammar.loadJSGF("recipe/hello");
        
        started = false;
        //addRule(ruleGrammar, "t1", "test the system");
    }
    
    public RuleGrammar getRuleGrammar() {
        return new BaseRuleGrammar(jsapiRecognizer, 
                jsgfGrammar.getRuleGrammar());
    }
    
    public Recognizer getRecognizer() {
        return recognizer;
    }
    
    public void clearRules() {
        RuleGrammar ruleGrammar = new BaseRuleGrammar(jsapiRecognizer, jsgfGrammar.getRuleGrammar());
        for (String r : ruleGrammar.listRuleNames()) {
            ruleGrammar.deleteRule(r);
        }
    }
    
    public void addRule(String ruleName, String jsgf) throws GrammarException, 
                IOException, JSGFGrammarParseException, JSGFGrammarException {
        RuleGrammar ruleGrammar = new BaseRuleGrammar(jsapiRecognizer, jsgfGrammar.getRuleGrammar());
        Rule newRule = ruleGrammar.ruleForJSGF(jsgf);
        ruleGrammar.setRule(ruleName, newRule, true);
        ruleGrammar.setEnabled(ruleName, true);
        jsgfGrammar.commitChanges();
    }
     
    public void start(Thread rl) throws GrammarException {
        microphone.startRecording();
        rl.start();
        started = true;
    }
    
    public void stop() {
        if (started) {
            recognizer.removeResultListener(listener);
            microphone.stopRecording();
        }
    }
    
    static ActionTagsParser parser;
    
    static public ActionTagsParser getTagsParser() {
        if (parser == null) {
            parser = new ActionTagsParser();
        }
        return parser;
    }
}
