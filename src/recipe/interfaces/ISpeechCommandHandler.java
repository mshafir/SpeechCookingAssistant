/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.interfaces;

import recipe.speech.result_handlers.AbstractResultHandler;

/**
 *
 * @author Michael
 */
public interface ISpeechCommandHandler {
    public void doCommand(String arg,AbstractResultHandler parent);
}
