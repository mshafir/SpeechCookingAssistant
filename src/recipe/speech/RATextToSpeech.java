/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech;

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
    
    public static void load() { 
        voiceManager = VoiceManager.getInstance();
        for (Voice v : voiceManager.getVoices()) {
            System.out.println(v.getName());
        }
        voice = voiceManager.getVoice("kevin16"); //switch to voiceName
        voice.allocate();
    }
    
    public static void speak(String text) {
        
        voice.speak(text);

    }
}
