package com.github.harshith;

import com.hdr.aishu.aiml.Bot;
import com.hdr.aishu.aiml.MagicBooleans;

public class AIMLWriter {
    public static void main(String[] args){
        try {
            MagicBooleans.trace_mode = false;
            Bot bot = new Bot("aishu", "./bots");
            bot.writeAIMLFiles();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}