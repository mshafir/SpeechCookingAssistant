/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech;

import recipe.core.Recipe;

/**
 *
 * @author Michael
 */
public class TTSStateCommand implements SpeechCommandHandler {
    Recipe r;
    
    
    public TTSStateCommand(Recipe r) {
        this.r = r;
    }
    
    @Override
    public void doCommand(String arg) {
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
        RATextToSpeech.speak(r.currentStep());
    }
}
