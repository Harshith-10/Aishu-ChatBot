package com.hdr.aishu.aiml;

import com.hdr.aishu.aiml.utils.IOUtils;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;

public class Aishu {
    public static boolean shuffle_mode;
    public static boolean sort_mode;
    public static boolean filter_atomic_mode;
    public static boolean filter_wild_mode;
    public static String logfile;
    public static AIMLSet passed;
    public static AIMLSet testSet;
    public static int runCompletedCnt;
    
    public static void productivity(final int runCompletedCnt, final Timer timer) {
        final float time = timer.elapsedTimeMins();
        System.out.println("Completed " + runCompletedCnt + " in " + time + " min. Productivity " + runCompletedCnt / time + " cat/min");
    }
    
    public static void saveCategory(final Bot bot, final String pattern, final String template, final String filename) {
        final String that = "*";
        final String topic = "*";
        final Category c = new Category(0, pattern, that, topic, template, filename);
        if (c.validate()) {
            bot.brain.addCategory(c);
            bot.writeAIMLIFFiles();
            ++Aishu.runCompletedCnt;
        }
        else {
            System.out.println("Invalid Category " + c.validationMessage);
        }
    }
    
    public static void deleteCategory(final Bot bot, final Category c) {
        c.setFilename(MagicStrings.deleted_aiml_file);
        c.setTemplate(MagicStrings.deleted_template);
        bot.deletedGraph.addCategory(c);
        bot.writeDeletedIFCategories();
    }
    
    public static void skipCategory(final Bot bot, final Category c) {
        c.setFilename(MagicStrings.unfinished_aiml_file);
        c.setTemplate(MagicStrings.unfinished_template);
        bot.unfinishedGraph.addCategory(c);
        System.out.println(bot.unfinishedGraph.getCategories().size() + " unfinished categories");
        bot.writeUnfinishedIFCategories();
    }
    
    public static void abwq(final Bot bot) {
        final Timer timer = new Timer();
        timer.start();
        bot.classifyInputs(Aishu.logfile);
        System.out.println(timer.elapsedTimeSecs() + " classifying inputs");
        bot.writeQuit();
    }
    
    public static void ab(final Bot bot) {
        final String logFile = Aishu.logfile;
        MagicBooleans.trace_mode = false;
        MagicBooleans.enable_external_sets = false;
        final Timer timer = new Timer();
        bot.brain.nodeStats();
        timer.start();
        System.out.println("Graphing inputs");
        bot.graphInputs(logFile);
        System.out.println(timer.elapsedTimeSecs() + " seconds Graphing inputs");
        timer.start();
        System.out.println("Finding Patterns");
        bot.findPatterns();
        System.out.println(bot.suggestedCategories.size() + " suggested categories");
        System.out.println(timer.elapsedTimeSecs() + " seconds finding patterns");
        timer.start();
        bot.patternGraph.nodeStats();
        System.out.println("Classifying Inputs");
        bot.classifyInputs(logFile);
        System.out.println(timer.elapsedTimeSecs() + " classifying inputs");
    }
    
    public static void terminalInteraction(final Bot bot) {
        Timer timer = new Timer();
        Aishu.sort_mode = !Aishu.shuffle_mode;
        Collections.sort(bot.suggestedCategories, Category.ACTIVATION_COMPARATOR);
        final ArrayList<Category> topSuggestCategories = new ArrayList<Category>();
        for (int i = 0; i < 10000 && i < bot.suggestedCategories.size(); ++i) {
            topSuggestCategories.add(bot.suggestedCategories.get(i));
        }
        bot.suggestedCategories = topSuggestCategories;
        if (Aishu.shuffle_mode) {
            Collections.shuffle(bot.suggestedCategories);
        }
        timer = new Timer();
        timer.start();
        Aishu.runCompletedCnt = 0;
        final ArrayList<Category> filteredAtomicCategories = new ArrayList<Category>();
        final ArrayList<Category> filteredWildCategories = new ArrayList<Category>();
        for (final Category c : bot.suggestedCategories) {
            if (!c.getPattern().contains("*")) {
                filteredAtomicCategories.add(c);
            }
            else {
                filteredWildCategories.add(c);
            }
        }
        ArrayList<Category> browserCategories;
        if (Aishu.filter_atomic_mode) {
            browserCategories = filteredAtomicCategories;
        }
        else if (Aishu.filter_wild_mode) {
            browserCategories = filteredWildCategories;
        }
        else {
            browserCategories = bot.suggestedCategories;
        }
        for (final Category c2 : browserCategories) {
            try {
                final ArrayList samples = new ArrayList((Collection<?>)c2.getMatches());
                Collections.shuffle(samples);
                for (int sampleSize = Math.min(MagicNumbers.displayed_input_sample_size, c2.getMatches().size()), j = 0; j < sampleSize; ++j) {
                    System.out.println("" + samples.get(j));
                }
                System.out.println("[" + c2.getActivationCnt() + "] " + c2.inputThatTopic());
                productivity(Aishu.runCompletedCnt, timer);
                final String textLine = "" + IOUtils.readInputTextLine();
                terminalInteractionStep(bot, "", textLine, c2);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Returning to Category Browser");
            }
        }
    }
    
    public static void terminalInteractionStep(final Bot bot, final String request, String textLine, final Category c) {
        String template = null;
        if (textLine.contains("<pattern>") && textLine.contains("</pattern>")) {
            final int index = textLine.indexOf("<pattern>") + "<pattern>".length();
            final int jndex = textLine.indexOf("</pattern>");
            final int kndex = jndex + "</pattern>".length();
            if (index < jndex) {
                final String pattern = textLine.substring(index, jndex);
                c.setPattern(pattern);
                textLine = textLine.substring(kndex, textLine.length());
                System.out.println("Got pattern = " + pattern + " template = " + textLine);
            }
        }
        String botThinks = "";
        final String[] arr$;
        final String[] pronouns = arr$ = new String[] { "he", "she", "it", "we", "they" };
        for (final String p : arr$) {
            if (textLine.contains("<" + p + ">")) {
                textLine = textLine.replace("<" + p + ">", "");
                botThinks = "<think><set name=\"" + p + "\"><set name=\"topic\"><star/></set></set></think>";
            }
        }
        if (textLine.equals("q")) {
            System.exit(0);
        }
        else if (textLine.equals("wq")) {
            bot.writeQuit();
            System.exit(0);
        }
        else if (textLine.equals("skip") || textLine.equals("")) {
            skipCategory(bot, c);
        }
        else if (textLine.equals("s") || textLine.equals("pass")) {
            Aishu.passed.add(request);
            final AIMLSet difference = new AIMLSet("difference");
            difference.addAll(Aishu.testSet);
            difference.removeAll(Aishu.passed);
            difference.writeAIMLSet();
            Aishu.passed.writeAIMLSet();
        }
        else if (textLine.equals("d")) {
            deleteCategory(bot, c);
        }
        else if (textLine.equals("x")) {
            template = "<sraix>" + c.getPattern().replace("*", "<star/>") + "</sraix>";
            template += botThinks;
            saveCategory(bot, c.getPattern(), template, MagicStrings.sraix_aiml_file);
        }
        else if (textLine.equals("p")) {
            template = "<srai>" + MagicStrings.inappropriate_filter + "</srai>";
            template += botThinks;
            saveCategory(bot, c.getPattern(), template, MagicStrings.inappropriate_aiml_file);
        }
        else if (textLine.equals("f")) {
            template = "<srai>" + MagicStrings.profanity_filter + "</srai>";
            template += botThinks;
            saveCategory(bot, c.getPattern(), template, MagicStrings.profanity_aiml_file);
        }
        else if (textLine.equals("i")) {
            template = "<srai>" + MagicStrings.insult_filter + "</srai>";
            template += botThinks;
            saveCategory(bot, c.getPattern(), template, MagicStrings.insult_aiml_file);
        }
        else if (textLine.contains("<srai>") || textLine.contains("<sr/>")) {
            template = textLine;
            template += botThinks;
            saveCategory(bot, c.getPattern(), template, MagicStrings.reductions_update_aiml_file);
        }
        else if (textLine.contains("<oob>")) {
            template = textLine;
            template += botThinks;
            saveCategory(bot, c.getPattern(), template, MagicStrings.oob_aiml_file);
        }
        else if (textLine.contains("<set name") || botThinks.length() > 0) {
            template = textLine;
            template += botThinks;
            saveCategory(bot, c.getPattern(), template, MagicStrings.predicates_aiml_file);
        }
        else if (textLine.contains("<get name") && !textLine.contains("<get name=\"name")) {
            template = textLine;
            template += botThinks;
            saveCategory(bot, c.getPattern(), template, MagicStrings.predicates_aiml_file);
        }
        else {
            template = textLine;
            template += botThinks;
            saveCategory(bot, c.getPattern(), template, MagicStrings.personality_aiml_file);
        }
    }
    
    static {
        Aishu.shuffle_mode = false;
        Aishu.sort_mode = true;
        Aishu.filter_atomic_mode = true;
        Aishu.filter_wild_mode = false;
        Aishu.logfile = MagicStrings.root_path + "/data/" + MagicStrings.ab_sample_file;
        Aishu.passed = new AIMLSet("passed");
        Aishu.testSet = new AIMLSet("1000");
    }
}
