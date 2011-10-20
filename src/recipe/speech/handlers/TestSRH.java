/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech.handlers;

import recipe.speech.RASpeechRecognizer;
import recipe.speech.TTSCommandHandler;
import recipe.speech.SpeechResultHandler;
import javax.speech.recognition.GrammarException;
/**
 *
 * @author Michael
 */
public class TestSRH extends SpeechResultHandler {
    
    public TestSRH(RASpeechRecognizer rec) {
        super(rec);
    }
    
    @Override
    public void loadCommands() {
        try {
            super.loadCommandRule("r1", "<ingredientQuestion> sugar", "speak", "2 cups of sugar",
                    new TTSCommandHandler());

            super.loadCommandRule("r2", "<ingredientQuestion> flour", "speak", "3 cups of flour");

            super.loadCommandRule("r3", "<ingredientQuestion> vanilla extract", "speak", "1 teaspoon of vanilla extract");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
