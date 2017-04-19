package com.darragh.musicalnotepad;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

class PitchDetector {
    public Thread t;
    private AudioDispatcher dispatcher;
    private static ArrayList<String> list,note;
    private static ArrayList<Integer> note_length;

    void recordAudio(){
        list = new ArrayList<>();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent audioEvent) {
                final float pitchInHz = result.getPitch();
                System.out.println(hz_to_note(pitchInHz,1));
                list.add(hz_to_note(pitchInHz,1));
            }
        };

        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(p);
        t = new Thread(dispatcher, "Audio Dispatcher");
        t.start();
    }

    private static void concatenate(){
        ArrayList<String> updatedNotes = new ArrayList<>();
        ArrayList<Integer> updatedLengths = new ArrayList<>();
        int temp_length;
        for(int i=0; i<note.size()-1; i++){
            if(note.get(i).equals(note.get(i+1))) {
                updatedNotes.add(note.get(i));
                updatedLengths.add(note_length.get(i) + note_length.get(i + 1));
                i++;
            } else {
                updatedNotes.add(note.get(i));
                updatedLengths.add(note_length.get(i));
            }
        }
        note=new ArrayList<>(updatedNotes);
        note_length=new ArrayList<>(updatedLengths);
    }

    private void processAudioInput(){
        note = new ArrayList<>();
        note_length = new ArrayList<>();
        String currentNote="";
        int currentLength=0;
        int array_Size= list.size();
        for(int i=0; i<array_Size; i++){
            currentLength++;
            if(i==0){
                currentNote=list.get(i);
            } else if(!currentNote.equals(list.get(i))) {
                System.out.println("Note change! " + list.get(i) + " - "+  currentLength);
                if(currentLength>2){
                    note.add(currentNote);
                    note_length.add(currentLength);
                }
                currentLength=0;
                currentNote = list.get(i);
            }
            else if(i==(array_Size-1)){
                note.add(currentNote);
                note_length.add(currentLength);
            }
        }
    }

    String stopRecording(TextView outputDisplay,KeySignature keySignature){
        dispatcher.stop();
        outputDisplay.setVisibility(View.VISIBLE);
        processAudioInput();

//        System.out.println("----- PRECONCATENATE -----");
//        for(int i=0; i<note.size(); i++){
//            System.out.println(note.get(i) + " - " + note_length.get(i));
//        }
        concatenate();
//        System.out.println("----- POSTCONCATENATE -----");
//
//        for(int i=0; i<note.size(); i++){
//            System.out.println(note.get(i) + " - " + note_length.get(i));
//        }
        return kMeans.kMeanController(fillCluster(note,note_length),keySignature);

    }

    public static ArrayList<ClusterNode> fillCluster(ArrayList<String> note, ArrayList<Integer> note_length){
        ArrayList<ClusterNode> clusterNodeArrayList = new ArrayList<ClusterNode>();
        System.out.println("ClusterNode ArrayList<>");
        for(int i=0; i<note.size(); i++){
            clusterNodeArrayList.add(new ClusterNode(i,note_length.get(i),note.get(i)));
        }
        return clusterNodeArrayList;
    }



    //YOU MIGHT NEED TO DO SOMETHING ABOUT THESE HARDCODED VALUES.
    public String hz_to_note(float frequency,int octave){
        if(frequency>538.808f && frequency<=1077.616f){
            float temp_frequency;
            for(float i=2.0f; i<10.0f; i++){
                temp_frequency = frequency/i;
                if(temp_frequency>=261.626f && temp_frequency<=538.808f){
                    octave = (int)i;
                    frequency = frequency/i;
                    break;
                }
            }
        }
        else if(frequency>=1077.616f){
            float temp_frequency;
            for(float i=4.0f; i<10.0f; i++){
                temp_frequency = frequency/i;
                if(temp_frequency>=254.284f && temp_frequency<=538.808f){
                    octave = (int)(i-1.0f);
                    frequency = frequency/i;
                    break;
                }
            }
        }
        if(frequency>=254.284f && frequency<=269.4045f){
            return ("C" + octave);
        } else if(frequency>269.4045f && frequency<=285.424f){
            return ("_D" + octave);
        } else if(frequency>285.424f && frequency<=302.396f){
            return ("D" + octave);
        } else if(frequency>302.396f && frequency<=320.3775f){
            return ("_E" + octave);
        } else if(frequency>320.3775f && frequency<=339.428f){
            return ("E" + octave);
        } else if(frequency>339.428f && frequency<=359.611f){
            return ("F" + octave);
        } else if(frequency>359.611f && frequency<=380.9945f){
            return ("_G" + octave);
        } else if(frequency>380.9945f && frequency<=403.65f){
            return ("G" + octave);
        } else if(frequency>403.65f && frequency<=427.6525f){
            return ("_A" + octave);
        } else if(frequency>427.6525f && frequency<=453.082f){
            return ("A" + octave);
        } else if(frequency>453.082f && frequency<=480.0236f){
            return ("_B" + octave);
        } else if(frequency>480.0236f && frequency<=508.567f){
            return ("B" + octave);
        } else if(frequency>508.567f && frequency<=538.808f){
            return ("C" + (octave+1));
        } else
            return "SPACE";
    }
}