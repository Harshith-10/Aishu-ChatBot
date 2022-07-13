package com.github.harshith;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;

public class ResponseCompiler {
    private String[] cmds = new String[]{"%cat", "%dog"};
    private String mediaRoot = "C:\\Users\\91798\\IdeaProjects\\Aishu\\bots\\aishu\\pictures\\";
    private ArrayList<String> cmdArray = new ArrayList<>();
    private ArrayList<String> cats = new ArrayList<>();
    private ArrayList<String> dogs = new ArrayList<>();
    private File theChosenOne;
    
    public ResponseCompiler(){
        try {
            cmdArray.addAll(Arrays.asList(cmds));
            for (int i = 1; i < 5; i++) {
                cats.add(mediaRoot + "animals\\cats\\cat" + String.valueOf(i) + ".jpg");
                dogs.add(mediaRoot + "animals\\dogs\\dog" + String.valueOf(i) + ".jpg");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public File process(String msg){
        if (msg.length()>3) {
            final String cmd = msg.substring(msg.length() - 4);
            if(cmdArray.indexOf(cmd) > -1){
                return getFile(cmd);
            }
        }
        return null;
    }
    
    public File getFile(String cmd){
        int index = getRandom(0, 3);
        switch(cmd){
            case "%cat":
                try {
                    theChosenOne = new File(cats.get(index));
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            case "%dog":
                try {
                    theChosenOne = new File(dogs.get(index));
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                try {
                    theChosenOne = new File("C:\\Users\\91798\\IdeaProjects\\Aishu\\bots\\aishu\\pictures\\unavailable.jpeg");
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return theChosenOne;
    }
    
    public int getRandom(int min, int max){
        return (int) ((Math.random() * (max - min)) + min);
    }
}