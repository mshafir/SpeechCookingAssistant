/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.interfaces;

import recipe.speech.RecognizerState;

/**
 *
 * @author Michael
 */
public interface ISpeechEventListener {
    public void handleEvent(RecognizerState state,String arg);
}
