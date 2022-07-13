package com.hdr.aishu.aiml;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;

public class Graphmaster
{
    public Bot bot;
    public final Nodemapper root;
    public int matchCount;
    public int upgradeCnt;
    public HashSet<String> vocabulary;
    public String resultNote;
    public int categoryCnt;
    public static boolean enableShortCuts;
    public static boolean verbose;
    int leafCnt;
    int nodeCnt;
    long nodeSize;
    int singletonCnt;
    int shortCutCnt;
    int naryCnt;
    
    public Graphmaster(final Bot bot) {
        this.matchCount = 0;
        this.upgradeCnt = 0;
        this.resultNote = "";
        this.categoryCnt = 0;
        this.root = new Nodemapper();
        this.bot = bot;
        this.vocabulary = new HashSet<String>();
    }
    
    public static String inputThatTopic(final String input, final String that, final String topic) {
        return input.trim() + " <THAT> " + that.trim() + " <TOPIC> " + topic.trim();
    }
    
    public void addCategory(final Category category) {
        final Path p = Path.sentenceToPath(inputThatTopic(category.getPattern(), category.getThat(), category.getTopic()));
        this.addPath(p, category);
        ++this.categoryCnt;
    }
    
    boolean thatStarTopicStar(final Path path) {
        final String tail = Path.pathToSentence(path).trim();
        return tail.equals("<THAT> * <TOPIC> *");
    }
    
    void addSets(final String type, final Bot bot, final Nodemapper node) {
        final String typeName = Utilities.tagTrim(type, "SET").toLowerCase();
        if (bot.setMap.containsKey(typeName)) {
            if (node.sets == null) {
                node.sets = new ArrayList<String>();
            }
            node.sets.add(typeName);
        }
        else {
            System.out.println("AIML Set " + typeName + " not found.");
        }
    }
    
    void addPath(final Path path, final Category category) {
        this.addPath(this.root, path, category);
    }
    
    void addPath(final Nodemapper node, final Path path, final Category category) {
        if (path == null) {
            node.category = category;
            node.height = 0;
        }
        else if (Graphmaster.enableShortCuts && this.thatStarTopicStar(path)) {
            node.category = category;
            node.height = Math.min(4, node.height);
            node.shortCut = true;
        }
        else if (NodemapperOperator.containsKey(node, path.word)) {
            if (path.word.startsWith("<SET>")) {
                this.addSets(path.word, this.bot, node);
            }
            final Nodemapper nextNode = NodemapperOperator.get(node, path.word);
            this.addPath(nextNode, path.next, category);
            int offset = 1;
            if (path.word.equals("#") || path.word.equals("^")) {
                offset = 0;
            }
            node.height = Math.min(offset + nextNode.height, node.height);
        }
        else {
            final Nodemapper nextNode = new Nodemapper();
            if (path.word.startsWith("<SET>")) {
                this.addSets(path.word, this.bot, node);
            }
            if (node.key != null) {
                NodemapperOperator.upgrade(node);
                ++this.upgradeCnt;
            }
            NodemapperOperator.put(node, path.word, nextNode);
            this.addPath(nextNode, path.next, category);
            int offset = 1;
            if (path.word.equals("#") || path.word.equals("^")) {
                offset = 0;
            }
            node.height = Math.min(offset + nextNode.height, node.height);
        }
    }
    
    public boolean existsCategory(final Category c) {
        return this.findNode(c) != null;
    }
    
    public Nodemapper findNode(final Category c) {
        return this.findNode(c.getPattern(), c.getThat(), c.getTopic());
    }
    
    public Nodemapper findNode(final String input, final String that, final String topic) {
        final Nodemapper result = this.findNode(this.root, Path.sentenceToPath(inputThatTopic(input, that, topic)));
        if (Graphmaster.verbose) {
            System.out.println("findNode " + inputThatTopic(input, that, topic) + " " + result);
        }
        return result;
    }
    
    Nodemapper findNode(final Nodemapper node, final Path path) {
        if (path == null && node != null) {
            if (Graphmaster.verbose) {
                System.out.println("findNode: path is null, returning node " + node.category.inputThatTopic());
            }
            return node;
        }
        if (Path.pathToSentence(path).trim().equals("<THAT> * <TOPIC> *") && node.shortCut && path.word.equals("<THAT>")) {
            if (Graphmaster.verbose) {
                System.out.println("findNode: shortcut, returning " + node.category.inputThatTopic());
            }
            return node;
        }
        if (NodemapperOperator.containsKey(node, path.word)) {
            if (Graphmaster.verbose) {
                System.out.println("findNode: node contains " + path.word);
            }
            final Nodemapper nextNode = NodemapperOperator.get(node, path.word.toUpperCase());
            return this.findNode(nextNode, path.next);
        }
        if (Graphmaster.verbose) {
            System.out.println("findNode: returning null");
        }
        return null;
    }
    
    public final Nodemapper match(final String input, final String that, final String topic) {
        Nodemapper n = null;
        try {
            final String inputThatTopic = inputThatTopic(input, that, topic);
            final Path p = Path.sentenceToPath(inputThatTopic);
            n = this.match(p, inputThatTopic);
            if (MagicBooleans.trace_mode) {
                if (n != null) {
                    System.out.println("Matched: " + n.category.inputThatTopic() + " " + n.category.getFilename());
                }
                else {
                    System.out.println("No match.");
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            n = null;
        }
        if (MagicBooleans.trace_mode && Chat.matchTrace.length() < MagicNumbers.max_trace_length && n != null) {
            Chat.setMatchTrace(Chat.matchTrace + n.category.inputThatTopic() + "\n");
        }
        return n;
    }
    
    final Nodemapper match(final Path path, final String inputThatTopic) {
        try {
            final String[] inputStars = new String[MagicNumbers.max_stars];
            final String[] thatStars = new String[MagicNumbers.max_stars];
            final String[] topicStars = new String[MagicNumbers.max_stars];
            final String starState = "inputStar";
            final String matchTrace = "";
            final Nodemapper n = this.match(path, this.root, inputThatTopic, starState, 0, inputStars, thatStars, topicStars, matchTrace);
            if (n != null) {
                final StarBindings sb = new StarBindings();
                for (int i = 0; inputStars[i] != null && i < MagicNumbers.max_stars; ++i) {
                    sb.inputStars.add(inputStars[i]);
                }
                for (int i = 0; thatStars[i] != null && i < MagicNumbers.max_stars; ++i) {
                    sb.thatStars.add(thatStars[i]);
                }
                for (int i = 0; topicStars[i] != null && i < MagicNumbers.max_stars; ++i) {
                    sb.topicStars.add(topicStars[i]);
                }
                n.starBindings = sb;
            }
            if (n != null) {
                n.category.addMatch(inputThatTopic);
            }
            return n;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    final Nodemapper match(final Path path, final Nodemapper node, final String inputThatTopic, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, final String matchTrace) {
        ++this.matchCount;
        Nodemapper matchedNode;
        if ((matchedNode = this.nullMatch(path, node, matchTrace)) != null) {
            return matchedNode;
        }
        if (path.length < node.height) {
            return null;
        }
        if ((matchedNode = this.dollarMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
            return matchedNode;
        }
        if ((matchedNode = this.sharpMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
            return matchedNode;
        }
        if ((matchedNode = this.underMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
            return matchedNode;
        }
        if ((matchedNode = this.wordMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
            return matchedNode;
        }
        if ((matchedNode = this.setMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
            return matchedNode;
        }
        if ((matchedNode = this.shortCutMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
            return matchedNode;
        }
        if ((matchedNode = this.caretMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
            return matchedNode;
        }
        if ((matchedNode = this.starMatch(path, node, inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
            return matchedNode;
        }
        return null;
    }
    
    void fail(final String mode, final String trace) {
    }
    
    final Nodemapper nullMatch(final Path path, final Nodemapper node, final String matchTrace) {
        if (path == null && node != null && NodemapperOperator.isLeaf(node) && node.category != null) {
            return node;
        }
        this.fail("null", matchTrace);
        return null;
    }
    
    final Nodemapper shortCutMatch(final Path path, final Nodemapper node, final String inputThatTopic, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, final String matchTrace) {
        if (node != null && node.shortCut && path.word.equals("<THAT>") && node.category != null) {
            final String tail = Path.pathToSentence(path).trim();
            final String that = tail.substring(tail.indexOf("<THAT>") + "<THAT>".length(), tail.indexOf("<TOPIC>")).trim();
            final String topic = tail.substring(tail.indexOf("<TOPIC>") + "<TOPIC>".length(), tail.length()).trim();
            thatStars[0] = that;
            topicStars[0] = topic;
            return node;
        }
        this.fail("shortCut", matchTrace);
        return null;
    }
    
    final Nodemapper wordMatch(final Path path, final Nodemapper node, final String inputThatTopic, String starState, int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, String matchTrace) {
        try {
            final String uword = path.word.toUpperCase();
            if (uword.equals("<THAT>")) {
                starIndex = 0;
                starState = "thatStar";
            }
            else if (uword.equals("<TOPIC>")) {
                starIndex = 0;
                starState = "topicStar";
            }
            matchTrace = matchTrace + "[" + uword + "," + uword + "]";
            final Nodemapper matchedNode;
            if (path != null && NodemapperOperator.containsKey(node, uword) && (matchedNode = this.match(path.next, NodemapperOperator.get(node, uword), inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
                return matchedNode;
            }
            this.fail("word", matchTrace);
            return null;
        }
        catch (Exception ex) {
            System.out.println("wordMatch: " + Path.pathToSentence(path) + ": " + ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    final Nodemapper dollarMatch(final Path path, final Nodemapper node, final String inputThatTopic, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, final String matchTrace) {
        final String uword = "$" + path.word.toUpperCase();
        final Nodemapper matchedNode;
        if (path != null && NodemapperOperator.containsKey(node, uword) && (matchedNode = this.match(path.next, NodemapperOperator.get(node, uword), inputThatTopic, starState, starIndex, inputStars, thatStars, topicStars, matchTrace)) != null) {
            return matchedNode;
        }
        this.fail("dollar", matchTrace);
        return null;
    }
    
    final Nodemapper starMatch(final Path path, final Nodemapper node, final String input, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, final String matchTrace) {
        return this.wildMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "*", matchTrace);
    }
    
    final Nodemapper underMatch(final Path path, final Nodemapper node, final String input, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, final String matchTrace) {
        return this.wildMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "_", matchTrace);
    }
    
    final Nodemapper caretMatch(final Path path, final Nodemapper node, final String input, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, final String matchTrace) {
        final Nodemapper matchedNode = this.zeroMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "^", matchTrace);
        if (matchedNode != null) {
            return matchedNode;
        }
        return this.wildMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "^", matchTrace);
    }
    
    final Nodemapper sharpMatch(final Path path, final Nodemapper node, final String input, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, final String matchTrace) {
        final Nodemapper matchedNode = this.zeroMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "#", matchTrace);
        if (matchedNode != null) {
            return matchedNode;
        }
        return this.wildMatch(path, node, input, starState, starIndex, inputStars, thatStars, topicStars, "#", matchTrace);
    }
    
    final Nodemapper zeroMatch(final Path path, final Nodemapper node, final String input, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, final String wildcard, String matchTrace) {
        matchTrace = matchTrace + "[" + wildcard + ",]";
        if (path != null && NodemapperOperator.containsKey(node, wildcard)) {
            this.setStars(this.bot.properties.get(MagicStrings.null_star), starIndex, starState, inputStars, thatStars, topicStars);
            final Nodemapper nextNode = NodemapperOperator.get(node, wildcard);
            return this.match(path, nextNode, input, starState, starIndex + 1, inputStars, thatStars, topicStars, matchTrace);
        }
        this.fail("zero " + wildcard, matchTrace);
        return null;
    }
    
    final Nodemapper wildMatch(Path path, final Nodemapper node, final String input, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, final String wildcard, String matchTrace) {
        if (path.word.equals("<THAT>") || path.word.equals("<TOPIC>")) {
            this.fail("wild1 " + wildcard, matchTrace);
            return null;
        }
        try {
            if (path != null && NodemapperOperator.containsKey(node, wildcard)) {
                matchTrace = matchTrace + "[" + wildcard + "," + path.word + "]";
                String currentWord = path.word;
                String starWords = currentWord + " ";
                final Path pathStart = path.next;
                final Nodemapper nextNode = NodemapperOperator.get(node, wildcard);
                if (NodemapperOperator.isLeaf(nextNode) && !nextNode.shortCut) {
                    final Nodemapper matchedNode = nextNode;
                    starWords = Path.pathToSentence(path);
                    this.setStars(starWords, starIndex, starState, inputStars, thatStars, topicStars);
                    return matchedNode;
                }
                Nodemapper matchedNode;
                for (path = pathStart; path != null && !currentWord.equals("<THAT>") && !currentWord.equals("<TOPIC>"); currentWord = path.word, starWords = starWords + currentWord + " ", path = path.next) {
                    matchTrace = matchTrace + "[" + wildcard + "," + path.word + "]";
                    if ((matchedNode = this.match(path, nextNode, input, starState, starIndex + 1, inputStars, thatStars, topicStars, matchTrace)) != null) {
                        this.setStars(starWords, starIndex, starState, inputStars, thatStars, topicStars);
                        return matchedNode;
                    }
                }
                this.fail("wild2 " + wildcard, matchTrace);
                return null;
            }
        }
        catch (Exception ex) {
            System.out.println("wildMatch: " + Path.pathToSentence(path) + ": " + ex);
        }
        this.fail("wild3 " + wildcard, matchTrace);
        return null;
    }
    
    final Nodemapper setMatch(final Path path, final Nodemapper node, final String input, final String starState, final int starIndex, final String[] inputStars, final String[] thatStars, final String[] topicStars, String matchTrace) {
        if (node.sets == null || path.word.equals("<THAT>") || path.word.equals("<TOPIC>")) {
            return null;
        }
        for (final String setName : node.sets) {
            final Nodemapper nextNode = NodemapperOperator.get(node, "<SET>" + setName.toUpperCase() + "</SET>");
            final AIMLSet aimlSet = this.bot.setMap.get(setName);
            String currentWord = path.word;
            String starWords = currentWord + " ";
            int length = 1;
            matchTrace = matchTrace + "[<set>" + setName + "</set>," + path.word + "]";
            for (Path qath = path.next; qath != null && !currentWord.equals("<THAT>") && !currentWord.equals("<TOPIC>") && length <= aimlSet.maxLength; ++length, currentWord = qath.word, starWords = starWords + currentWord + " ", qath = qath.next) {
                final String phrase = this.bot.preProcessor.normalize(starWords.trim()).toUpperCase();
                final Nodemapper matchedNode;
                if (aimlSet.contains(phrase) && (matchedNode = this.match(qath, nextNode, input, starState, starIndex + 1, inputStars, thatStars, topicStars, matchTrace)) != null) {
                    this.setStars(starWords, starIndex, starState, inputStars, thatStars, topicStars);
                    return matchedNode;
                }
            }
        }
        this.fail("set", matchTrace);
        return null;
    }
    
    public void setStars(String starWords, final int starIndex, final String starState, final String[] inputStars, final String[] thatStars, final String[] topicStars) {
        if (starIndex < MagicNumbers.max_stars) {
            starWords = starWords.trim();
            if (starState.equals("inputStar")) {
                inputStars[starIndex] = starWords;
            }
            else if (starState.equals("thatStar")) {
                thatStars[starIndex] = starWords;
            }
            else if (starState.equals("topicStar")) {
                topicStars[starIndex] = starWords;
            }
        }
    }
    
    public void printgraph() {
        this.printgraph(this.root, "");
    }
    
    void printgraph(final Nodemapper node, final String partial) {
        if (node == null) {
            System.out.println("Null graph");
        }
        else {
            String template = "";
            if (NodemapperOperator.isLeaf(node) || node.shortCut) {
                template = Category.templateToLine(node.category.getTemplate());
                template = template.substring(0, Math.min(16, template.length()));
                if (node.shortCut) {
                    System.out.println(partial + "(" + NodemapperOperator.size(node) + "[" + node.key + "," + node.value + "])--<THAT>-->X(1)--*-->X(1)--<TOPIC>-->X(1)--*-->" + template + "...");
                }
                else {
                    System.out.println(partial + "(" + NodemapperOperator.size(node) + "[" + node.key + "," + node.value + "]) " + template + "...");
                }
            }
            for (final String key : NodemapperOperator.keySet(node)) {
                this.printgraph(NodemapperOperator.get(node, key), partial + "(" + NodemapperOperator.size(node) + "[" + node.height + "])--" + key + "-->");
            }
        }
    }
    
    public ArrayList<Category> getCategories() {
        final ArrayList<Category> categories = new ArrayList<Category>();
        this.getCategories(this.root, categories);
        return categories;
    }
    
    void getCategories(final Nodemapper node, final ArrayList<Category> categories) {
        if (node == null) {
            return;
        }
        if ((NodemapperOperator.isLeaf(node) || node.shortCut) && node.category != null) {
            categories.add(node.category);
        }
        for (final String key : NodemapperOperator.keySet(node)) {
            this.getCategories(NodemapperOperator.get(node, key), categories);
        }
    }
    
    public void nodeStats() {
        this.leafCnt = 0;
        this.nodeCnt = 0;
        this.nodeSize = 0L;
        this.singletonCnt = 0;
        this.shortCutCnt = 0;
        this.naryCnt = 0;
        this.nodeStatsGraph(this.root);
        this.resultNote = this.nodeCnt + " nodes " + this.singletonCnt + " singletons " + this.leafCnt + " leaves " + this.shortCutCnt + " shortcuts " + this.naryCnt + " n-ary " + this.nodeSize + " branches " + this.nodeSize / (float)this.nodeCnt + " average branching ";
        System.out.println(this.resultNote);
    }
    
    public void nodeStatsGraph(final Nodemapper node) {
        if (node != null) {
            ++this.nodeCnt;
            this.nodeSize += NodemapperOperator.size(node);
            if (NodemapperOperator.size(node) == 1) {
                ++this.singletonCnt;
            }
            if (NodemapperOperator.isLeaf(node) && !node.shortCut) {
                ++this.leafCnt;
            }
            if (NodemapperOperator.size(node) > 1) {
                ++this.naryCnt;
            }
            if (node.shortCut) {
                ++this.shortCutCnt;
            }
            for (final String key : NodemapperOperator.keySet(node)) {
                this.nodeStatsGraph(NodemapperOperator.get(node, key));
            }
        }
    }
    
    public HashSet<String> getVocabulary() {
        this.vocabulary = new HashSet<String>();
        this.getBrainVocabulary(this.root);
        for (final String set : this.bot.setMap.keySet()) {
            this.vocabulary.addAll((Collection<? extends String>) this.bot.setMap.get(set));
        }
        return this.vocabulary;
    }
    
    public void getBrainVocabulary(final Nodemapper node) {
        if (node != null) {
            for (final String key : NodemapperOperator.keySet(node)) {
                this.vocabulary.add(key);
                this.getBrainVocabulary(NodemapperOperator.get(node, key));
            }
        }
    }
    
    static {
        Graphmaster.enableShortCuts = false;
        Graphmaster.verbose = false;
    }
}
