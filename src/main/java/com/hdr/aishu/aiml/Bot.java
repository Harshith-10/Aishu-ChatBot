package com.hdr.aishu.aiml;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Set;
import java.util.Collections;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;

public class Bot {
    public final Properties properties;
    public final PreProcessor preProcessor;
    public final Graphmaster brain;
    public final Graphmaster inputGraph;
    public final Graphmaster learnfGraph;
    public final Graphmaster patternGraph;
    public final Graphmaster deletedGraph;
    public Graphmaster unfinishedGraph;
    public ArrayList<Category> suggestedCategories;
    public String name;
    public HashMap<String, AIMLSet> setMap;
    public HashMap<String, AIMLMap> mapMap;
    static int leafPatternCnt;
    static int starPatternCnt;
    
    public void setAllPaths(final String root, final String name) {
        MagicStrings.bot_path = root;
        MagicStrings.bot_name_path = MagicStrings.bot_path + "/" + name;
        System.out.println("Name = " + name + " Path = " + MagicStrings.bot_name_path);
        MagicStrings.aiml_path = MagicStrings.bot_name_path + "/aiml";
        MagicStrings.aimlif_path = MagicStrings.bot_name_path + "/aimlif";
        MagicStrings.config_path = MagicStrings.bot_name_path + "/config";
        MagicStrings.log_path = MagicStrings.bot_name_path + "/logs";
        MagicStrings.sets_path = MagicStrings.bot_name_path + "/sets";
        MagicStrings.maps_path = MagicStrings.bot_name_path + "/maps";
        System.out.println(MagicStrings.root_path);
        System.out.println(MagicStrings.bot_path);
        System.out.println(MagicStrings.bot_name_path);
        System.out.println(MagicStrings.aiml_path);
        System.out.println(MagicStrings.aimlif_path);
        System.out.println(MagicStrings.config_path);
        System.out.println(MagicStrings.log_path);
        System.out.println(MagicStrings.sets_path);
        System.out.println(MagicStrings.maps_path);
    }
    
    public Bot() {
        this(MagicStrings.default_bot);
    }
    
    public Bot(final String name) {
        this(name, MagicStrings.root_path);
    }
    
    public Bot(final String name, final String path) {
        this(name, path, "auto");
    }
    
    public Bot(final String name, final String path, final String action) {
        this.properties = new Properties();
        this.name = MagicStrings.unknown_bot_name;
        this.setMap = new HashMap<String, AIMLSet>();
        this.mapMap = new HashMap<String, AIMLMap>();
        this.setAllPaths(path, this.name = name);
        this.brain = new Graphmaster(this);
        this.inputGraph = new Graphmaster(this);
        this.learnfGraph = new Graphmaster(this);
        this.deletedGraph = new Graphmaster(this);
        this.patternGraph = new Graphmaster(this);
        this.unfinishedGraph = new Graphmaster(this);
        this.suggestedCategories = new ArrayList<Category>();
        this.preProcessor = new PreProcessor(this);
        this.addProperties();
        this.addAIMLSets();
        this.addAIMLMaps();
        final AIMLSet number = new AIMLSet(MagicStrings.natural_number_set_name);
        this.setMap.put(MagicStrings.natural_number_set_name, number);
        final AIMLMap successor = new AIMLMap(MagicStrings.map_successor);
        this.mapMap.put(MagicStrings.map_successor, successor);
        final AIMLMap predecessor = new AIMLMap(MagicStrings.map_predecessor);
        this.mapMap.put(MagicStrings.map_predecessor, predecessor);
        final Date aimlDate = new Date(new File(MagicStrings.aiml_path).lastModified());
        final Date aimlIFDate = new Date(new File(MagicStrings.aimlif_path).lastModified());
        System.out.println("AIML modified " + aimlDate + " AIMLIF modified " + aimlIFDate);
        this.readDeletedIFCategories();
        this.readUnfinishedIFCategories();
        MagicStrings.pannous_api_key = Utilities.getPannousAPIKey();
        MagicStrings.pannous_login = Utilities.getPannousLogin();
        if (action.equals("aiml2csv")) {
            this.addCategoriesFromAIML();
        }
        else if (action.equals("csv2aiml")) {
            this.addCategoriesFromAIMLIF();
        }
        else if (aimlDate.after(aimlIFDate)) {
            System.out.println("AIML modified after AIMLIF");
            this.addCategoriesFromAIML();
            this.writeAIMLIFFiles();
        }
        else {
            this.addCategoriesFromAIMLIF();
            if (this.brain.getCategories().size() == 0) {
                System.out.println("No AIMLIF Files found.  Looking for AIML");
                this.addCategoriesFromAIML();
            }
        }
        System.out.println("--> Bot " + name + " " + this.brain.getCategories().size() + " completed " + this.deletedGraph.getCategories().size() + " deleted " + this.unfinishedGraph.getCategories().size() + " unfinished");
    }
    
    void addMoreCategories(final String file, final ArrayList<Category> moreCategories) {
        if (file.contains(MagicStrings.deleted_aiml_file)) {
            for (final Category c : moreCategories) {
                this.deletedGraph.addCategory(c);
            }
        }
        else if (file.contains(MagicStrings.unfinished_aiml_file)) {
            for (final Category c : moreCategories) {
                if (this.brain.findNode(c) == null) {
                    this.unfinishedGraph.addCategory(c);
                }
                else {
                    System.out.println("unfinished " + c.inputThatTopic() + " found in brain");
                }
            }
        }
        else if (file.contains(MagicStrings.learnf_aiml_file)) {
            System.out.println("Reading Learnf file");
            for (final Category c : moreCategories) {
                this.brain.addCategory(c);
                this.learnfGraph.addCategory(c);
                this.patternGraph.addCategory(c);
            }
        }
        else {
            for (final Category c : moreCategories) {
                this.brain.addCategory(c);
                this.patternGraph.addCategory(c);
            }
        }
    }
    
    void addCategoriesFromAIML() {
        final Timer timer = new Timer();
        timer.start();
        try {
            final File folder = new File(MagicStrings.aiml_path);
            if (folder.exists()) {
                final File[] listOfFiles = folder.listFiles();
                System.out.println("Loading AIML files from " + MagicStrings.aiml_path);
                for (final File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        final String file = listOfFile.getName();
                        if (file.endsWith(".aiml") || file.endsWith(".AIML")) {
                            System.out.println(file);
                            try {
                                final ArrayList<Category> moreCategories = AIMLProcessor.AIMLToCategories(MagicStrings.aiml_path, file);
                                this.addMoreCategories(file, moreCategories);
                            }
                            catch (Exception iex) {
                                System.out.println("Problem loading " + file);
                                iex.printStackTrace();
                            }
                        }
                    }
                }
            }
            else {
                System.out.println("addCategories: " + MagicStrings.aiml_path + " does not exist.");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Loaded " + this.brain.getCategories().size() + " categories in " + timer.elapsedTimeSecs() + " sec");
    }
    
    void addCategoriesFromAIMLIF() {
        final Timer timer = new Timer();
        timer.start();
        try {
            final File folder = new File(MagicStrings.aimlif_path);
            if (folder.exists()) {
                final File[] listOfFiles = folder.listFiles();
                System.out.println("Loading AIML files from " + MagicStrings.aimlif_path);
                for (final File listOfFile : listOfFiles) {
                    Label_0199: {
                        if (listOfFile.isFile()) {
                            final String file = listOfFile.getName();
                            if (!file.endsWith(MagicStrings.aimlif_file_suffix)) {
                                if (!file.endsWith(MagicStrings.aimlif_file_suffix.toUpperCase())) {
                                    break Label_0199;
                                }
                            }
                            try {
                                final ArrayList<Category> moreCategories = this.readIFCategories(MagicStrings.aimlif_path + "/" + file);
                                this.addMoreCategories(file, moreCategories);
                            }
                            catch (Exception iex) {
                                System.out.println("Problem loading " + file);
                                iex.printStackTrace();
                            }
                        }
                    }
                }
            }
            else {
                System.out.println("addCategories: " + MagicStrings.aimlif_path + " does not exist.");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Loaded " + this.brain.getCategories().size() + " categories in " + timer.elapsedTimeSecs() + " sec");
    }
    
    public void readDeletedIFCategories() {
        this.readCertainIFCategories(this.deletedGraph, MagicStrings.deleted_aiml_file);
    }
    
    public void readUnfinishedIFCategories() {
        this.readCertainIFCategories(this.unfinishedGraph, MagicStrings.unfinished_aiml_file);
    }
    
    public void updateUnfinishedCategories() {
        final ArrayList<Category> unfinished = this.unfinishedGraph.getCategories();
        this.unfinishedGraph = new Graphmaster(this);
        for (final Category c : unfinished) {
            if (!this.brain.existsCategory(c)) {
                this.unfinishedGraph.addCategory(c);
            }
        }
    }
    
    public void writeQuit() {
        this.writeAIMLIFFiles();
        System.out.println("Wrote AIMLIF Files");
        this.writeAIMLFiles();
        System.out.println("Wrote AIML Files");
        this.writeDeletedIFCategories();
        this.updateUnfinishedCategories();
        this.writeUnfinishedIFCategories();
    }
    
    public void readCertainIFCategories(final Graphmaster graph, final String fileName) {
        final File file = new File(MagicStrings.aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
        if (file.exists()) {
            try {
                final ArrayList<Category> deletedCategories = this.readIFCategories(MagicStrings.aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
                for (final Category d : deletedCategories) {
                    graph.addCategory(d);
                }
                System.out.println("readCertainIFCategories " + graph.getCategories().size() + " categories from " + fileName + MagicStrings.aimlif_file_suffix);
            }
            catch (Exception iex) {
                System.out.println("Problem loading " + fileName);
                iex.printStackTrace();
            }
        }
        else {
            System.out.println("No " + MagicStrings.deleted_aiml_file + MagicStrings.aimlif_file_suffix + " file found");
        }
    }
    
    public void writeCertainIFCategories(final Graphmaster graph, final String file) {
        if (MagicBooleans.trace_mode) {
            System.out.println("writeCertainIFCaegories " + file + " size= " + graph.getCategories().size());
        }
        this.writeIFCategories(graph.getCategories(), file + MagicStrings.aimlif_file_suffix);
        final File dir = new File(MagicStrings.aimlif_path);
        dir.setLastModified(new Date().getTime());
    }
    
    public void writeDeletedIFCategories() {
        this.writeCertainIFCategories(this.deletedGraph, MagicStrings.deleted_aiml_file);
    }
    
    public void writeLearnfIFCategories() {
        this.writeCertainIFCategories(this.learnfGraph, MagicStrings.learnf_aiml_file);
    }
    
    public void writeUnfinishedIFCategories() {
        this.writeCertainIFCategories(this.unfinishedGraph, MagicStrings.unfinished_aiml_file);
    }
    
    public void writeIFCategories(final ArrayList<Category> cats, final String filename) {
        BufferedWriter bw = null;
        final File existsPath = new File(MagicStrings.aimlif_path);
        if (existsPath.exists()) {
            try {
                bw = new BufferedWriter(new FileWriter(MagicStrings.aimlif_path + "/" + filename));
                for (final Category category : cats) {
                    bw.write(Category.categoryToIF(category));
                    bw.newLine();
                }
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
            finally {
                try {
                    if (bw != null) {
                        bw.flush();
                        bw.close();
                    }
                }
                catch (IOException ex3) {
                    ex3.printStackTrace();
                }
            }
        }
    }
    
    public void writeAIMLIFFiles() {
        System.out.println("writeAIMLIFFiles");
        final HashMap<String, BufferedWriter> fileMap = new HashMap<String, BufferedWriter>();
        if (this.deletedGraph.getCategories().size() > 0) {
            this.writeDeletedIFCategories();
        }
        final ArrayList<Category> brainCategories = this.brain.getCategories();
        Collections.sort(brainCategories, Category.CATEGORY_NUMBER_COMPARATOR);
        for (final Category c : brainCategories) {
            try {
                final String fileName = c.getFilename();
                BufferedWriter bw;
                if (fileMap.containsKey(fileName)) {
                    bw = fileMap.get(fileName);
                }
                else {
                    bw = new BufferedWriter(new FileWriter(MagicStrings.aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix));
                    fileMap.put(fileName, bw);
                }
                bw.write(Category.categoryToIF(c));
                bw.newLine();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        final Set set = fileMap.keySet();
        for (final Object aSet : set) {
            final BufferedWriter bw2 = fileMap.get(aSet);
            try {
                if (bw2 == null) {
                    continue;
                }
                bw2.flush();
                bw2.close();
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        final File dir = new File(MagicStrings.aimlif_path);
        dir.setLastModified(new Date().getTime());
    }
    
    public void writeAIMLFiles() {
        final HashMap<String, BufferedWriter> fileMap = new HashMap<String, BufferedWriter>();
        Category b = new Category(0, "BUILD", "*", "*", new Date().toString(), "update.aiml");
        this.brain.addCategory(b);
        b = new Category(0, "DELEVLOPMENT ENVIRONMENT", "*", "*", MagicStrings.programNameVersion, "update.aiml");
        this.brain.addCategory(b);
        final ArrayList<Category> brainCategories = this.brain.getCategories();
        Collections.sort(brainCategories, Category.CATEGORY_NUMBER_COMPARATOR);
        for (final Category c : brainCategories) {
            if (!c.getFilename().equals(MagicStrings.null_aiml_file)) {
                try {
                    final String fileName = c.getFilename();
                    BufferedWriter bw;
                    if (fileMap.containsKey(fileName)) {
                        bw = fileMap.get(fileName);
                    }
                    else {
                        final String copyright = Utilities.getCopyright(this, fileName);
                        bw = new BufferedWriter(new FileWriter(MagicStrings.aiml_path + "/" + fileName));
                        fileMap.put(fileName, bw);
                        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<aiml>\n");
                        bw.write(copyright);
                    }
                    bw.write(Category.categoryToAIML(c) + "\n");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        final Set set = fileMap.keySet();
        for (final Object aSet : set) {
            final BufferedWriter bw2 = fileMap.get(aSet);
            try {
                if (bw2 == null) {
                    continue;
                }
                bw2.write("</aiml>\n");
                bw2.flush();
                bw2.close();
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        final File dir = new File(MagicStrings.aiml_path);
        dir.setLastModified(new Date().getTime());
    }
    
    void addProperties() {
        try {
            this.properties.getProperties(MagicStrings.config_path + "/properties.txt");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void findPatterns() {
        this.findPatterns(this.inputGraph.root, "");
        System.out.println(Bot.leafPatternCnt + " Leaf Patterns " + Bot.starPatternCnt + " Star Patterns");
    }
    
    void findPatterns(final Nodemapper node, final String partialPatternThatTopic) {
        if (NodemapperOperator.isLeaf(node) && node.category.getActivationCnt() > MagicNumbers.node_activation_cnt) {
            ++Bot.leafPatternCnt;
            try {
                String categoryPatternThatTopic = "";
                if (node.shortCut) {
                    categoryPatternThatTopic = partialPatternThatTopic + " <THAT> * <TOPIC> *";
                }
                else {
                    categoryPatternThatTopic = partialPatternThatTopic;
                }
                final Category c = new Category(0, categoryPatternThatTopic, MagicStrings.blank_template, MagicStrings.unknown_aiml_file);
                if (!this.brain.existsCategory(c) && !this.deletedGraph.existsCategory(c) && !this.unfinishedGraph.existsCategory(c)) {
                    this.patternGraph.addCategory(c);
                    this.suggestedCategories.add(c);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (NodemapperOperator.size(node) > MagicNumbers.node_size) {
            ++Bot.starPatternCnt;
            try {
                final Category c2 = new Category(0, partialPatternThatTopic + " * <THAT> * <TOPIC> *", MagicStrings.blank_template, MagicStrings.unknown_aiml_file);
                if (!this.brain.existsCategory(c2) && !this.deletedGraph.existsCategory(c2) && !this.unfinishedGraph.existsCategory(c2)) {
                    this.patternGraph.addCategory(c2);
                    this.suggestedCategories.add(c2);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (final String key : NodemapperOperator.keySet(node)) {
            final Nodemapper value = NodemapperOperator.get(node, key);
            this.findPatterns(value, partialPatternThatTopic + " " + key);
        }
    }
    
    public void classifyInputs(final String filename) {
        try {
            final FileInputStream fstream = new FileInputStream(filename);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            int count = 0;
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.startsWith("Human: ")) {
                    strLine = strLine.substring("Human: ".length(), strLine.length());
                }
                final Nodemapper match = this.patternGraph.match(strLine, "unknown", "unknown");
                match.category.incrementActivationCnt();
                ++count;
            }
            br.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public void graphInputs(final String filename) {
        try {
            final FileInputStream fstream = new FileInputStream(filename);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                final Category c = new Category(0, strLine, "*", "*", "nothing", MagicStrings.unknown_aiml_file);
                final Nodemapper node = this.inputGraph.findNode(c);
                if (node == null) {
                    this.inputGraph.addCategory(c);
                    c.incrementActivationCnt();
                }
                else {
                    node.category.incrementActivationCnt();
                }
            }
            br.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public ArrayList<Category> readIFCategories(final String filename) {
        final ArrayList<Category> categories = new ArrayList<Category>();
        try {
            final FileInputStream fstream = new FileInputStream(filename);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                try {
                    final Category c = Category.IFToCategory(strLine);
                    categories.add(c);
                }
                catch (Exception ex) {
                    System.out.println("Invalid AIMLIF in " + filename + " line " + strLine);
                }
            }
            br.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return categories;
    }
    
    public void shadowChecker() {
        this.shadowChecker(this.brain.root);
    }
    
    void shadowChecker(final Nodemapper node) {
        if (NodemapperOperator.isLeaf(node)) {
            final String input = node.category.getPattern().replace("*", "XXX").replace("_", "XXX");
            final String that = node.category.getThat().replace("*", "XXX").replace("_", "XXX");
            final String topic = node.category.getTopic().replace("*", "XXX").replace("_", "XXX");
            final Nodemapper match = this.brain.match(input, that, topic);
            if (match != node) {
                System.out.println("" + Graphmaster.inputThatTopic(input, that, topic));
                System.out.println("MATCHED:     " + match.category.inputThatTopic());
                System.out.println("SHOULD MATCH:" + node.category.inputThatTopic());
            }
        }
        else {
            for (final String key : NodemapperOperator.keySet(node)) {
                this.shadowChecker(NodemapperOperator.get(node, key));
            }
        }
    }
    
    void addAIMLSets() {
        final Timer timer = new Timer();
        timer.start();
        try {
            final File folder = new File(MagicStrings.sets_path);
            if (folder.exists()) {
                final File[] listOfFiles = folder.listFiles();
                System.out.println("Loading AIML Sets files from " + MagicStrings.sets_path);
                for (final File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        final String file = listOfFile.getName();
                        if (file.endsWith(".txt") || file.endsWith(".TXT")) {
                            System.out.println(file);
                            final String setName = file.substring(0, file.length() - ".txt".length());
                            System.out.println("Read AIML Set " + setName);
                            final AIMLSet aimlSet = new AIMLSet(setName);
                            aimlSet.readAIMLSet(this);
                            this.setMap.put(setName, aimlSet);
                        }
                    }
                }
            }
            else {
                System.out.println("addAIMLSets: " + MagicStrings.sets_path + " does not exist.");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    void addAIMLMaps() {
        final Timer timer = new Timer();
        timer.start();
        try {
            final File folder = new File(MagicStrings.maps_path);
            if (folder.exists()) {
                final File[] listOfFiles = folder.listFiles();
                System.out.println("Loading AIML Map files from " + MagicStrings.maps_path);
                for (final File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        final String file = listOfFile.getName();
                        if (file.endsWith(".txt") || file.endsWith(".TXT")) {
                            System.out.println(file);
                            final String mapName = file.substring(0, file.length() - ".txt".length());
                            System.out.println("Read AIML Map " + mapName);
                            final AIMLMap aimlMap = new AIMLMap(mapName);
                            aimlMap.readAIMLMap(this);
                            this.mapMap.put(mapName, aimlMap);
                        }
                    }
                }
            }
            else {
                System.out.println("addCategories: " + MagicStrings.aiml_path + " does not exist.");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static {
        Bot.leafPatternCnt = 0;
        Bot.starPatternCnt = 0;
    }
}
