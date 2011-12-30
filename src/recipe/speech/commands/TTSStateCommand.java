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
	public void doCommand(String arg, AbstractResultHandler parent) {
		if (arg.equals("start")) {
			r.setStep(0);
		} else if (arg.equals("end")) {
			r.setStep(r.Steps.size() - 1);
		} else if (arg.equals("next")) {
			r.setStep(r.getStep() + 1);
		} else if (arg.equals("prev")) {
			r.setStep(r.getStep() - 1);
		}
		RATextToSpeech.speak(r.currentStep());
	}
}
