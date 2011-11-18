/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech.commands;

import recipe.core.Recipe;
import recipe.interfaces.ISpeechCommandHandler;
import recipe.speech.RATextToSpeech;
import recipe.speech.RecognizerState;
import recipe.speech.result_handlers.AbstractResultHandler;

/**
 *
 * @author Michael
 */
public class TTSStateCommand implements ISpeechCommandHandler {
    Recipe r;
    
    
    public TTSStateCommand(Recipe r) {
        this.r = r;
    }
    
    @Override
    public void doCommand(String arg,AbstractResultHandler parent) {
        if (arg.equals("start")) {
            r.setStep(0);   
        }
        else if (arg.equals("end")) {
            r.setStep(r.Steps.size()-1);
        }
        else if (arg.equals("next")) {
            r.setStep(r.getStep()+1);
        }
        else if (arg.equals("prev")) {
            r.setStep(r.getStep()-1);
        }
        parent.executeListeners(RecognizerState.Speaking, r.currentStep());
        RATextToSpeech.speak(r.currentStep());
    }
}
