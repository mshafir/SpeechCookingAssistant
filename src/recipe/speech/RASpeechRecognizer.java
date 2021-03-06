/*   This file is part of SpeechCookingAssistant.
 *
 *   SpeechCookingAssistant is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, version 3
 *
 *   SpeechCookingAssistant is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with SpeechCookingAssistant.  If not, see <http://www.gnu.org/licenses/>.  
 * 
 *   Copyright 2011 Michael Shafir
 *   Michael.Shafir@gmail.com
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

import recipe.speech.result_handlers.AbstractResultHandler;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
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
	private boolean started;
	private AbstractResultHandler curHandler;

	// Singleton Code
	private static RASpeechRecognizer instance;

	public static void initialize() {
		try {
			instance = new RASpeechRecognizer();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public synchronized static RASpeechRecognizer getInstance() {
		if (instance == null) {
			initialize();
		}
		return instance;
	}

	public RASpeechRecognizer() throws EngineException, GrammarException,
			IOException, JSGFGrammarParseException, JSGFGrammarException {
		cm = new ConfigurationManager("speech.config.xml");
		recognizer = (Recognizer) cm.lookup("recognizer");
		jsgfGrammar = (JSGFGrammar) cm.lookup("jsgfGrammar");
		microphone = (Microphone) cm.lookup("microphone");
		jsapiRecognizer = new BaseRecognizer(jsgfGrammar.getGrammarManager());
		jsapiRecognizer.allocate();
		// recognizer.allocate();

		System.out.print(" Loading recognizer ...");
		recognizer.allocate();
		System.out.println(" Ready");

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

	public void loadGrammar() throws IOException, JSGFGrammarParseException,
			JSGFGrammarException {
		jsgfGrammar.loadJSGF("speech");
	}

	public void addRule(String ruleName, String jsgf) throws GrammarException,
			IOException, JSGFGrammarParseException, JSGFGrammarException {
		RuleGrammar ruleGrammar = new BaseRuleGrammar(jsapiRecognizer,
				jsgfGrammar.getRuleGrammar());
		Rule newRule = ruleGrammar.ruleForJSGF(jsgf);
		ruleGrammar.setRule(ruleName, newRule, true);
		ruleGrammar.setEnabled(ruleName, true);
		jsgfGrammar.commitChanges();
	}

	public void setHandler(AbstractResultHandler rh) {
		curHandler = rh;
	}

	public void start() throws GrammarException {
		if (!started) {
			microphone.startRecording();
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
				// recognizer.resetMonitors();
			} catch (Exception ex) {
				System.err.println("Could not properly close recognizer.");
			}
			started = false;
		}
	}

	public void help() {
		curHandler.help();
	}

	public void setSpeaking(String text) {
		curHandler.executeListeners(RecognizerState.Speaking, text);
	}

	public void doneSpeaking() {
		curHandler.executeListeners(RecognizerState.Ready, "");
	}

	static ActionTagsParser parser;

	static synchronized public ActionTagsParser getTagsParser() {
		if (parser == null) {
			parser = new ActionTagsParser();
		}
		return parser;
	}
}
