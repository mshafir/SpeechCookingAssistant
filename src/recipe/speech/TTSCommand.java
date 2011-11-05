/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech;

/**
 *
 * @author Michael
 */
public class TTSCommand implements SpeechCommandHandler {
    
    @Override
    public void doCommand(String arg,SpeechResultHandler parent) {
        parent.executeListeners(2, arg);
        RATextToSpeech.speak(arg);
    }
}
