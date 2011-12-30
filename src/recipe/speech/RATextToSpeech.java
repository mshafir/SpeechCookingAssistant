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

import recipe.interfaces.ISpeechEventListener;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

//import com.sun.speech.freetts.audio.JavaClipAudioPlayer;
/**
 * 
 * @author Michael
 */
public class RATextToSpeech {
	static VoiceManager voiceManager;
	static Voice voice;
	static ISpeechEventListener listener;
	static boolean speaking;

	public static boolean isSpeaking() {
		return speaking;
	}

	public static void initialize() {
		voiceManager = VoiceManager.getInstance();
		for (Voice v : voiceManager.getVoices()) {
			System.out.println(v.getName());
		}
		voice = voiceManager.getVoice("kevin16"); // switch to voiceName
		voice.allocate();
	}

	public static void speak(String text) {
		speaking = true;
		RASpeechRecognizer.getInstance().setSpeaking(text);
		if (voice == null) {
			initialize();
		}
		voice.speak(text);
		RASpeechRecognizer.getInstance().doneSpeaking();
		speaking = false;
	}
}
