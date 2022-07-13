package com.hdr.aishu.aiml;

import com.hdr.aishu.aiml.utils.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Chat {
    public Bot bot;
    public String customerId;
    public History<History> thatHistory;
    public History<String> requestHistory;
    public History<String> responseHistory;
    public History<String> inputHistory;
    public Predicates predicates;
    public static String matchTrace;
    public static boolean locationKnown;
    public static String longitude;
    public static String latitude;

    public Chat(final Bot bot) {
        this(bot, "0");
    }
    
    public Chat(final Bot bot, final String customerId) {
        this.customerId = MagicStrings.unknown_customer_id;
        this.thatHistory = new History<History>("that");
        this.requestHistory = new History<String>("request");
        this.responseHistory = new History<String>("response");
        this.inputHistory = new History<String>("input");
        this.predicates = new Predicates();
        this.customerId = customerId;
        this.bot = bot;
        final History<String> contextThatHistory = new History<String>();
        contextThatHistory.add(MagicStrings.default_that);
        this.thatHistory.add(contextThatHistory);
        this.addPredicates();
        this.predicates.put("topic", MagicStrings.default_topic);
    }
    
    void addPredicates() {
        try {
            this.predicates.getPredicateDefaults(MagicStrings.config_path + "/predicates.txt");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void chat() {
        BufferedWriter bw = null;
        final String logFilePath = MagicStrings.log_path + "/log_" + this.customerId + ".txt";
        try {
            File logFile = new File(logFilePath);
            if(!logFile.exists()) {
                logFile.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(logFile, true));
            String request = "SET PREDICATES";
            String response = this.multisentenceRespond(request);
            while (!request.equals("quit")) {
                System.out.print("Human: ");
                request = IOUtils.readInputTextLine();
                response = this.multisentenceRespond(request);
                System.out.println("Robot: " + response);
                bw.write("Human: " + request);
                bw.newLine();
                bw.write("Robot: " + response);
                bw.newLine();
                bw.flush();
            }
            bw.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    String respond(final String input, String that, final String topic, final History contextThatHistory) {
        this.inputHistory.add(input);
        final String response = AIMLProcessor.respond(input, that, topic, this);
        String normResponse = this.bot.preProcessor.normalize(response);
        normResponse = JapaneseTokenizer.morphSentence(normResponse);
        final String[] sentences = this.bot.preProcessor.sentenceSplit(normResponse);
        for (int i = 0; i < sentences.length; ++i) {
            that = sentences[i];
            if (that.trim().equals("")) {
                that = MagicStrings.default_that;
            }
            contextThatHistory.add(that);
        }
        return response.trim() + "  ";
    }
    
    String respond(final String input, final History<String> contextThatHistory) {
        final History hist = this.thatHistory.get(0);
        String that;
        if (hist == null) {
            that = MagicStrings.default_that;
        }
        else {
            that = hist.getString(0);
        }
        return this.respond(input, that, this.predicates.get("topic"), contextThatHistory);
    }

    public String multisentenceRespond(final String request) {
        String response = "";
        Chat.matchTrace = "";

        try {
            String norm = this.bot.preProcessor.normalize(request);
            norm = JapaneseTokenizer.morphSentence(norm);
            if (MagicBooleans.trace_mode) {
                System.out.println("normalized = " + norm);
            }
            final String[] sentences = this.bot.preProcessor.sentenceSplit(norm);
            final History<String> contextThatHistory = new History<String>("contextThat");
            for (int i = 0; i < sentences.length; ++i) {
                AIMLProcessor.trace_count = 0;
                final String reply = this.respond(sentences[i], contextThatHistory);
                response = response + " " + reply;
            }
            this.requestHistory.add(request);
            this.responseHistory.add(response);
            this.thatHistory.add(contextThatHistory);
        } catch (Exception ex) {
            ex.printStackTrace();
            return MagicStrings.error_bot_response;
        }
        this.bot.writeLearnfIFCategories();
        return response.trim();
    }
    
    public static void setMatchTrace(final String newMatchTrace) {
        Chat.matchTrace = newMatchTrace;
    }
    
    static {
        Chat.matchTrace = "";
        Chat.locationKnown = false;
    }
}
