/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech.commands;

import recipe.interfaces.ISpeechCommandHandler;
import recipe.speech.RATextToSpeech;
import recipe.speech.RecognizerState;
import recipe.speech.result_handlers.AbstractResultHandler;

/**
 *
 * @author Michael
 */
public class HelpCommand implements ISpeechCommandHandler {
    
    @Override
    public void doCommand(String arg,AbstractResultHandler parent) {
        parent.help();
    }
}
