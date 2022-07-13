// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.util.Comparator;

public class Category
{
    private String pattern;
    private String that;
    private String topic;
    private String template;
    private String filename;
    private int activationCnt;
    private int categoryNumber;
    public static int categoryCnt;
    private AIMLSet matches;
    public String validationMessage;
    public static Comparator<Category> ACTIVATION_COMPARATOR;
    public static Comparator<Category> PATTERN_COMPARATOR;
    public static Comparator<Category> CATEGORY_NUMBER_COMPARATOR;
    
    public AIMLSet getMatches() {
        if (this.matches != null) {
            return this.matches;
        }
        return new AIMLSet("No Matches");
    }
    
    public int getActivationCnt() {
        return this.activationCnt;
    }
    
    public int getCategoryNumber() {
        return this.categoryNumber;
    }
    
    public String getPattern() {
        if (this.pattern == null) {
            return "*";
        }
        return this.pattern;
    }
    
    public String getThat() {
        if (this.that == null) {
            return "*";
        }
        return this.that;
    }
    
    public String getTopic() {
        if (this.topic == null) {
            return "*";
        }
        return this.topic;
    }
    
    public String getTemplate() {
        if (this.template == null) {
            return "";
        }
        return this.template;
    }
    
    public String getFilename() {
        if (this.filename == null) {
            return MagicStrings.unknown_aiml_file;
        }
        return this.filename;
    }
    
    public void incrementActivationCnt() {
        ++this.activationCnt;
    }
    
    public void setActivationCnt(final int cnt) {
        this.activationCnt = cnt;
    }
    
    public void setFilename(final String filename) {
        this.filename = filename;
    }
    
    public void setTemplate(final String template) {
        this.template = template;
    }
    
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }
    
    public void setThat(final String that) {
        this.that = that;
    }
    
    public void setTopic(final String topic) {
        this.topic = topic;
    }
    
    public String inputThatTopic() {
        return Graphmaster.inputThatTopic(this.pattern, this.that, this.topic);
    }
    
    public void addMatch(final String input) {
        if (this.matches == null) {
            final String setName = this.inputThatTopic().replace("*", "STAR").replace("_", "UNDERSCORE").replace(" ", "-").replace("<THAT>", "THAT").replace("<TOPIC>", "TOPIC");
            this.matches = new AIMLSet(setName);
        }
        this.matches.add(input);
    }
    
    public static String templateToLine(final String template) {
        String result = template;
        result = result.replaceAll("(\r\n|\n\r|\r|\n)", "\\#Newline");
        result = result.replaceAll(MagicStrings.aimlif_split_char, MagicStrings.aimlif_split_char_name);
        return result;
    }
    
    private static String lineToTemplate(final String line) {
        String result = line.replaceAll("\\#Newline", "\n");
        result = result.replaceAll(MagicStrings.aimlif_split_char_name, MagicStrings.aimlif_split_char);
        return result;
    }
    
    public static Category IFToCategory(final String IF) {
        final String[] split = IF.split(MagicStrings.aimlif_split_char);
        return new Category(Integer.parseInt(split[0]), split[1], split[2], split[3], lineToTemplate(split[4]), split[5]);
    }
    
    public static String categoryToIF(final Category category) {
        final String c = MagicStrings.aimlif_split_char;
        return category.getActivationCnt() + c + category.getPattern() + c + category.getThat() + c + category.getTopic() + c + templateToLine(category.getTemplate()) + c + category.getFilename();
    }
    
    public static String categoryToAIML(final Category category) {
        String topicStart = "";
        String topicEnd = "";
        String thatStatement = "";
        String result = "";
        String pattern = category.getPattern();
        final String[] arr$;
        final String[] splitPattern = arr$ = pattern.split(" ");
        for (String w : arr$) {
            if (w.startsWith("<TYPE>")) {
                w = w.toLowerCase();
            }
            pattern = pattern + " " + w;
        }
        pattern = pattern.trim();
        if (pattern.contains("type")) {
            System.out.println("Rebuilt pattern " + pattern);
        }
        String NL = System.getProperty("line.separator");
        NL = "\n";
        try {
            if (!category.getTopic().equals("*")) {
                topicStart = "<topic name=\"" + category.getTopic() + "\">" + NL;
                topicEnd = "</topic>" + NL;
            }
            if (!category.getThat().equals("*")) {
                thatStatement = "<that>" + category.getThat() + "</that>";
            }
            result = topicStart + "<category><pattern>" + category.getPattern() + "</pattern>" + thatStatement + NL + "<template>" + category.getTemplate() + "</template>" + NL + "</category>" + topicEnd;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    public boolean validPatternForm(final String pattern) {
        if (pattern.length() < 1) {
            this.validationMessage += "Zero length. ";
            return false;
        }
        final String[] words = pattern.split(" ");
        for (int i = 0; i < words.length; ++i) {}
        return true;
    }
    
    public boolean validate() {
        this.validationMessage = "";
        if (!this.validPatternForm(this.pattern)) {
            this.validationMessage += "Badly formatted <pattern>";
            return false;
        }
        if (!this.validPatternForm(this.that)) {
            this.validationMessage += "Badly formatted <that>";
            return false;
        }
        if (!this.validPatternForm(this.topic)) {
            this.validationMessage += "Badly formatted <topic>";
            return false;
        }
        if (!AIMLProcessor.validTemplate(this.template)) {
            this.validationMessage += "Badly formatted <template>";
            return false;
        }
        if (!this.filename.endsWith(".aiml")) {
            this.validationMessage += "Filename suffix should be .aiml";
            return false;
        }
        return true;
    }
    
    public Category(final int activationCnt, String pattern, String that, String topic, String template, String filename) {
        this.validationMessage = "";
        if (MagicBooleans.fix_excel_csv) {
            pattern = Utilities.fixCSV(pattern);
            that = Utilities.fixCSV(that);
            topic = Utilities.fixCSV(topic);
            template = Utilities.fixCSV(template);
            filename = Utilities.fixCSV(filename);
        }
        this.pattern = pattern.trim().toUpperCase();
        this.that = that.trim().toUpperCase();
        this.topic = topic.trim().toUpperCase();
        this.template = template.replace("& ", " and ");
        this.filename = filename;
        this.activationCnt = activationCnt;
        this.matches = null;
        this.categoryNumber = Category.categoryCnt++;
    }
    
    public Category(final int activationCnt, final String patternThatTopic, final String template, final String filename) {
        this(activationCnt, patternThatTopic.substring(0, patternThatTopic.indexOf("<THAT>")), patternThatTopic.substring(patternThatTopic.indexOf("<THAT>") + "<THAT>".length(), patternThatTopic.indexOf("<TOPIC>")), patternThatTopic.substring(patternThatTopic.indexOf("<TOPIC>") + "<TOPIC>".length(), patternThatTopic.length()), template, filename);
    }
    
    static {
        Category.categoryCnt = 0;
        Category.ACTIVATION_COMPARATOR = new Comparator<Category>() {
            @Override
            public int compare(final Category c1, final Category c2) {
                return c2.getActivationCnt() - c1.getActivationCnt();
            }
        };
        Category.PATTERN_COMPARATOR = new Comparator<Category>() {
            @Override
            public int compare(final Category c1, final Category c2) {
                return String.CASE_INSENSITIVE_ORDER.compare(c1.inputThatTopic(), c2.inputThatTopic());
            }
        };
        Category.CATEGORY_NUMBER_COMPARATOR = new Comparator<Category>() {
            @Override
            public int compare(final Category c1, final Category c2) {
                return c1.getCategoryNumber() - c2.getCategoryNumber();
            }
        };
    }
}
