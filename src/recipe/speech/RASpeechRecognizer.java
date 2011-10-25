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
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.io.IOException;

import javax.speech.EngineException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.tools.tags.ActionTagsParser;

/**
 *
 * @author Michael
 */
public class RASpeechRecognizer {
    private ConfigurationManager cm;
    private Recognizer recognizer;
    private BaseRecognizer jsapiRecognizer;
    private Microphone microphone;
    private JSGFGrammar jsgfGrammar;
    private RuleGrammar ruleGrammar;
    private boolean started;
    private SpeechResultHandler curHandler;
    
    //Singleton Code
    private static RASpeechRecognizer instance;
    public static void initialize() {
        try {
            instance = new RASpeechRecognizer();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    public synchronized static RASpeechRecognizer getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }
    
    
    public RASpeechRecognizer() 
            throws EngineException, GrammarException, IOException, 
            JSGFGrammarParseException, JSGFGrammarException {
        cm = new ConfigurationManager("jsgf.config.xml");
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
    }
    
    public RuleGrammar getRuleGrammar() {
        return new BaseRuleGrammar(jsapiRecognizer, 
                jsgfGrammar.getRuleGrammar());
    }
    
    public Recognizer getRecognizer() {
        return recognizer;
    }
    
    public void clearMic() {
        microphone.clear();
    }
    
    public void clearRules() throws IOException, JSGFGrammarParseException, JSGFGrammarException {
        for (String r : jsgfGrammar.getRuleGrammar().getRuleNames()) {
            jsgfGrammar.getRuleGrammar().deleteRule(r);
        }
        jsgfGrammar.getGrammarManager().grammars().clear();
        jsgfGrammar.loadJSGF("recipe/hello");
        jsgfGrammar.commitChanges();
    }
    
    public void addRule(String ruleName, String jsgf) throws GrammarException, 
                IOException, JSGFGrammarParseException, JSGFGrammarException {
        ruleGrammar = new BaseRuleGrammar(jsapiRecognizer, jsgfGrammar.getRuleGrammar());
        Rule newRule = ruleGrammar.ruleForJSGF(jsgf);
        ruleGrammar.setRule(ruleName, newRule, true);
        ruleGrammar.setEnabled(ruleName, true);
        jsgfGrammar.commitChanges();
    }
     
    public void start(SpeechResultHandler rh) throws GrammarException {
        if (!started) {
            microphone.startRecording();
            curHandler = rh;
            curHandler.start();
            started = true;
        }
    }
    
    public void stop() {
        if (started) {
            clearMic();
            microphone.stopRecording();
            curHandler.stopRecognition();
            try {
                curHandler.join();
                recognizer.resetMonitors();
                clearRules();
            } catch (Exception ex) {
                System.err.println("Could not properly close recognizer.");
            }
            started = false;
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
