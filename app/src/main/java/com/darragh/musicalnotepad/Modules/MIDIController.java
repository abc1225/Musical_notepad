package com.darragh.musicalnotepad.Modules;

import com.darragh.musicalnotepad.Objects.KeySignature;
import com.darragh.musicalnotepad.Objects.Song;

public class MIDIController {
    private static boolean skip=false;
    private static KeySignature keySignature;
    private static String keysignature;
    private static Song song;

    public static String convertABC(String _keysignature, String notes){
        keysignature=_keysignature;
        if(!keysignature.equals("C")){
            keySignature = new KeySignature(keysignature+"Major");
            char[] individualNotes = notes.toCharArray();
            String noteProgression = ""+individualNotes[0];
            for(int i=1; i<individualNotes.length; i++){
                if(i>0 && individualNotes[i-1]!='='){
                    noteProgression = noteProgression + sortAccidental(individualNotes[i]);
                } else {
                    noteProgression = noteProgression + individualNotes[i];
                }
            }
            return noteProgression;
        }
        return notes;
    }

    private static String sortAccidental(char note){
        for(String accidental: keySignature.getNotes(keysignature+"Major")){
            if(Character.toLowerCase(note)==accidental.toLowerCase().charAt(1)){
                switch(keySignature.getType()){
                    case "sharp":
                        return "^"+note;
                    case "flat":
                        return "_"+note;
                }
            }
        }
        return ""+note;
    }

}
