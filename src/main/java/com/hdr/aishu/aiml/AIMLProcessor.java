package com.hdr.aishu.aiml;

import com.hdr.aishu.aiml.utils.CalendarUtils;
import com.hdr.aishu.aiml.utils.DomUtils;
import com.hdr.aishu.aiml.utils.IOUtils;

import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import org.w3c.dom.Node;

public class AIMLProcessor {
    public static AIMLProcessorExtension extension;
    public static int sraiCount;
    public static int repeatCount;
    public static int trace_count;
    
    private static void categoryProcessor(final Node n, final ArrayList<Category> categories, String topic, final String aimlFile, final String language) {
        final NodeList children = n.getChildNodes();
        String pattern = "*";
        String that = "*";
        String template = "";
        for (int j = 0; j < children.getLength(); ++j) {
            final Node m = children.item(j);
            final String mName = m.getNodeName();
            if (!mName.equals("#text")) {
                if (mName.equals("pattern")) {
                    pattern = DomUtils.nodeToString(m);
                }
                else if (mName.equals("that")) {
                    that = DomUtils.nodeToString(m);
                }
                else if (mName.equals("topic")) {
                    topic = DomUtils.nodeToString(m);
                }
                else if (mName.equals("template")) {
                    template = DomUtils.nodeToString(m);
                }
                else {
                    System.out.println("categoryProcessor: unexpected " + mName);
                }
            }
        }
        pattern = trimTag(pattern, "pattern");
        that = trimTag(that, "that");
        topic = trimTag(topic, "topic");
        template = trimTag(template, "template");
        if (language.equals("JP") || language.equals("jp")) {
            final String morphPattern = JapaneseTokenizer.morphSentence(pattern);
            System.out.println("<pattern>" + pattern + "</pattern> --> <pattern>" + morphPattern + "</pattern>");
            pattern = morphPattern;
            final String morphThatPattern = JapaneseTokenizer.morphSentence(that);
            System.out.println("<that>" + that + "</that> --> <that>" + morphThatPattern + "</that>");
            that = morphThatPattern;
            final String morphTopicPattern = JapaneseTokenizer.morphSentence(topic);
            System.out.println("<topic>" + topic + "</topic> --> <topic>" + morphTopicPattern + "</topic>");
            topic = morphTopicPattern;
        }
        final Category c = new Category(0, pattern, that, topic, template, aimlFile);
        categories.add(c);
    }
    
    public static String trimTag(String s, final String tagName) {
        final String stag = "<" + tagName + ">";
        final String etag = "</" + tagName + ">";
        if (s.startsWith(stag) && s.endsWith(etag)) {
            s = s.substring(stag.length());
            s = s.substring(0, s.length() - etag.length());
        }
        return s.trim();
    }
    
    public static ArrayList<Category> AIMLToCategories(final String directory, final String aimlFile) {
        try {
            final ArrayList categories = new ArrayList();
            final Node root = DomUtils.parseFile(directory + "/" + aimlFile);
            String language = MagicStrings.default_language;
            if (root.hasAttributes()) {
                final NamedNodeMap XMLAttributes = root.getAttributes();
                for (int i = 0; i < XMLAttributes.getLength(); ++i) {
                    if (XMLAttributes.item(i).getNodeName().equals("language")) {
                        language = XMLAttributes.item(i).getNodeValue();
                    }
                }
            }
            final NodeList nodelist = root.getChildNodes();
            for (int i = 0; i < nodelist.getLength(); ++i) {
                final Node n = nodelist.item(i);
                if (n.getNodeName().equals("category")) {
                    categoryProcessor(n, categories, "*", aimlFile, language);
                }
                else if (n.getNodeName().equals("topic")) {
                    final String topic = n.getAttributes().getNamedItem("name").getTextContent();
                    final NodeList children = n.getChildNodes();
                    for (int j = 0; j < children.getLength(); ++j) {
                        final Node m = children.item(j);
                        if (m.getNodeName().equals("category")) {
                            categoryProcessor(m, categories, topic, aimlFile, language);
                        }
                    }
                }
            }
            return (ArrayList<Category>)categories;
        }
        catch (Exception ex) {
            System.out.println("AIMLToCategories: " + ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    public static int checkForRepeat(final String input, final Chat chatSession) {
        if (input.equals(chatSession.inputHistory.get(1))) {
            return 1;
        }
        return 0;
    }
    
    public static String respond(final String input, final String that, final String topic, final Chat chatSession) {
        return respond(input, that, topic, chatSession, 0);
    }
    
    public static String respond(String input, final String that, final String topic, final Chat chatSession, final int srCnt) {
        if (input == null || input.length() == 0) {
            input = MagicStrings.null_input;
        }
        AIMLProcessor.sraiCount = srCnt;
        String response = MagicStrings.default_bot_response;
        try {
            final Nodemapper leaf = chatSession.bot.brain.match(input, that, topic);
            if (leaf == null) {
                return response;
            }
            final ParseState ps = new ParseState(0, chatSession, input, that, topic, leaf);
            response = evalTemplate(leaf.category.getTemplate(), ps);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }
    
    private static String capitalizeString(final String string) {
        final char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; ++i) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            }
            else if (Character.isWhitespace(chars[i])) {
                found = false;
            }
        }
        return String.valueOf(chars);
    }
    
    private static String explode(final String input) {
        String result = "";
        for (int i = 0; i < input.length(); ++i) {
            result = result + " " + input.charAt(i);
        }
        return result.trim();
    }
    
    public static String evalTagContent(final Node node, final ParseState ps, final Set<String> ignoreAttributes) {
        String result = "";
        try {
            final NodeList childList = node.getChildNodes();
            for (int i = 0; i < childList.getLength(); ++i) {
                final Node child = childList.item(i);
                if (ignoreAttributes == null || !ignoreAttributes.contains(child.getNodeName())) {
                    result += recursEval(child, ps);
                }
            }
        }
        catch (Exception ex) {
            System.out.println("Something went wrong with evalTagContent");
            ex.printStackTrace();
        }
        return result;
    }
    
    public static String genericXML(final Node node, final ParseState ps) {
        final String result = evalTagContent(node, ps, null);
        return unevaluatedXML(result, node, ps);
    }
    
    private static String unevaluatedXML(final String result, final Node node, final ParseState ps) {
        final String nodeName = node.getNodeName();
        String attributes = "";
        if (node.hasAttributes()) {
            final NamedNodeMap XMLAttributes = node.getAttributes();
            for (int i = 0; i < XMLAttributes.getLength(); ++i) {
                attributes = attributes + " " + XMLAttributes.item(i).getNodeName() + "=\"" + XMLAttributes.item(i).getNodeValue() + "\"";
            }
        }
        if (result.equals("")) {
            return "<" + nodeName + attributes + "/>";
        }
        return "<" + nodeName + attributes + ">" + result + "</" + nodeName + ">";
    }
    
    private static String srai(final Node node, final ParseState ps) {
        ++AIMLProcessor.sraiCount;
        if (AIMLProcessor.sraiCount > MagicNumbers.max_recursion) {
            return MagicStrings.too_much_recursion;
        }
        String response = MagicStrings.default_bot_response;
        try {
            String result = evalTagContent(node, ps, null);
            result = result.trim();
            result = result.replaceAll("(\r\n|\n\r|\r|\n)", " ");
            result = ps.chatSession.bot.preProcessor.normalize(result);
            final String topic = ps.chatSession.predicates.get("topic");
            if (MagicBooleans.trace_mode) {
                System.out.println(AIMLProcessor.trace_count + ". <srai>" + result + "</srai> from " + ps.leaf.category.inputThatTopic() + " topic=" + topic + ") ");
                ++AIMLProcessor.trace_count;
            }
            final Nodemapper leaf = ps.chatSession.bot.brain.match(result, ps.that, topic);
            if (leaf == null) {
                return response;
            }
            response = evalTemplate(leaf.category.getTemplate(), new ParseState(ps.depth + 1, ps.chatSession, ps.input, ps.that, topic, leaf));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return response.trim();
    }
    
    private static String getAttributeOrTagValue(final Node node, final ParseState ps, final String attributeName) {
        String result = "";
        final Node m = node.getAttributes().getNamedItem(attributeName);
        if (m == null) {
            final NodeList childList = node.getChildNodes();
            result = null;
            for (int i = 0; i < childList.getLength(); ++i) {
                final Node child = childList.item(i);
                if (child.getNodeName().equals(attributeName)) {
                    result = evalTagContent(child, ps, null);
                }
            }
        }
        else {
            result = m.getNodeValue();
        }
        return result;
    }
    
    private static String sraix(final Node node, final ParseState ps) {
        final HashSet<String> attributeNames = Utilities.stringSet("botid", "host");
        final String host = getAttributeOrTagValue(node, ps, "host");
        final String botid = getAttributeOrTagValue(node, ps, "botid");
        final String hint = getAttributeOrTagValue(node, ps, "hint");
        final String limit = getAttributeOrTagValue(node, ps, "limit");
        final String defaultResponse = getAttributeOrTagValue(node, ps, "default");
        final String result = evalTagContent(node, ps, attributeNames);
        return Sraix.sraix(ps.chatSession, result, defaultResponse, hint, host, botid, null, limit);
    }
    
    private static String map(final Node node, final ParseState ps) {
        String result = MagicStrings.unknown_map_value;
        final HashSet<String> attributeNames = Utilities.stringSet("name");
        final String mapName = getAttributeOrTagValue(node, ps, "name");
        final String contents = evalTagContent(node, ps, attributeNames);
        if (mapName == null) {
            result = "<map>" + contents + "</map>";
        }
        else {
            final AIMLMap map = ps.chatSession.bot.mapMap.get(mapName);
            if (map != null) {
                result = map.get(contents.toUpperCase());
            }
            if (result == null) {
                result = MagicStrings.unknown_map_value;
            }
            result = result.trim();
        }
        return result;
    }
    
    private static String set(final Node node, final ParseState ps) {
        final HashSet<String> attributeNames = Utilities.stringSet("name", "var");
        final String predicateName = getAttributeOrTagValue(node, ps, "name");
        final String varName = getAttributeOrTagValue(node, ps, "var");
        String value = evalTagContent(node, ps, attributeNames).trim();
        value = value.replaceAll("(\r\n|\n\r|\r|\n)", " ");
        if (predicateName != null) {
            ps.chatSession.predicates.put(predicateName, value);
        }
        if (varName != null) {
            ps.vars.put(varName, value);
        }
        return value;
    }
    
    private static String get(final Node node, final ParseState ps) {
        String result = MagicStrings.unknown_predicate_value;
        final String predicateName = getAttributeOrTagValue(node, ps, "name");
        final String varName = getAttributeOrTagValue(node, ps, "var");
        if (predicateName != null) {
            result = ps.chatSession.predicates.get(predicateName).trim();
        }
        else if (varName != null) {
            result = ps.vars.get(varName).trim();
        }
        return result;
    }
    
    private static String bot(final Node node, final ParseState ps) {
        String result = MagicStrings.unknown_property_value;
        final String propertyName = getAttributeOrTagValue(node, ps, "name");
        if (propertyName != null) {
            result = ps.chatSession.bot.properties.get(propertyName).trim();
        }
        return result;
    }
    
    private static String date(final Node node, final ParseState ps) {
        final String jformat = getAttributeOrTagValue(node, ps, "jformat");
        final String locale = getAttributeOrTagValue(node, ps, "locale");
        final String timezone = getAttributeOrTagValue(node, ps, "timezone");
        final String dateAsString = CalendarUtils.date(jformat, locale, timezone);
        return dateAsString;
    }
    
    private static String interval(final Node node, final ParseState ps) {
        final HashSet<String> attributeNames = Utilities.stringSet("style", "jformat", "from", "to");
        String style = getAttributeOrTagValue(node, ps, "style");
        String jformat = getAttributeOrTagValue(node, ps, "jformat");
        String from = getAttributeOrTagValue(node, ps, "from");
        String to = getAttributeOrTagValue(node, ps, "to");
        if (style == null) {
            style = "years";
        }
        if (jformat == null) {
            jformat = "MMMMMMMMM dd, yyyy";
        }
        if (from == null) {
            from = "January 1, 1970";
        }
        if (to == null) {
            to = CalendarUtils.date(jformat, null, null);
        }
        String result = "unknown";
        if (style.equals("years")) {
            result = "" + Interval.getYearsBetween(from, to, jformat);
        }
        if (style.equals("months")) {
            result = "" + Interval.getMonthsBetween(from, to, jformat);
        }
        if (style.equals("days")) {
            result = "" + Interval.getDaysBetween(from, to, jformat);
        }
        if (style.equals("hours")) {
            result = "" + Interval.getHoursBetween(from, to, jformat);
        }
        return result;
    }
    
    private static int getIndexValue(final Node node, final ParseState ps) {
        int index = 0;
        final String value = getAttributeOrTagValue(node, ps, "index");
        if (value != null) {
            try {
                index = Integer.parseInt(value) - 1;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return index;
    }
    
    private static String inputStar(final Node node, final ParseState ps) {
        final int index = getIndexValue(node, ps);
        if (ps.leaf.starBindings.inputStars.star(index) == null) {
            return "";
        }
        return ps.leaf.starBindings.inputStars.star(index).trim();
    }
    
    private static String thatStar(final Node node, final ParseState ps) {
        final int index = getIndexValue(node, ps);
        if (ps.leaf.starBindings.thatStars.star(index) == null) {
            return "";
        }
        return ps.leaf.starBindings.thatStars.star(index).trim();
    }
    
    private static String topicStar(final Node node, final ParseState ps) {
        final int index = getIndexValue(node, ps);
        if (ps.leaf.starBindings.topicStars.star(index) == null) {
            return "";
        }
        return ps.leaf.starBindings.topicStars.star(index).trim();
    }
    
    private static String id(final Node node, final ParseState ps) {
        return ps.chatSession.customerId;
    }
    
    private static String size(final Node node, final ParseState ps) {
        final int size = ps.chatSession.bot.brain.getCategories().size();
        return String.valueOf(size);
    }
    
    private static String vocabulary(final Node node, final ParseState ps) {
        final int size = ps.chatSession.bot.brain.getVocabulary().size();
        return String.valueOf(size);
    }
    
    private static String program(final Node node, final ParseState ps) {
        return MagicStrings.programNameVersion;
    }
    
    private static String that(final Node node, final ParseState ps) {
        int index = 0;
        int jndex = 0;
        final String value = getAttributeOrTagValue(node, ps, "index");
        if (value != null) {
            try {
                final String pair = value;
                final String[] spair = pair.split(",");
                index = Integer.parseInt(spair[0]) - 1;
                jndex = Integer.parseInt(spair[1]) - 1;
                System.out.println("That index=" + index + "," + jndex);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        String that = MagicStrings.unknown_history_item;
        final History hist = ps.chatSession.thatHistory.get(index);
        if (hist != null) {
            that = hist.get(jndex).toString();
        }
        return that.trim();
    }
    
    private static String input(final Node node, final ParseState ps) {
        final int index = getIndexValue(node, ps);
        return ps.chatSession.inputHistory.getString(index);
    }
    
    private static String request(final Node node, final ParseState ps) {
        final int index = getIndexValue(node, ps);
        return ps.chatSession.requestHistory.getString(index).trim();
    }
    
    private static String response(final Node node, final ParseState ps) {
        final int index = getIndexValue(node, ps);
        return ps.chatSession.responseHistory.getString(index).trim();
    }
    
    private static String system(final Node node, final ParseState ps) {
        final HashSet<String> attributeNames = Utilities.stringSet("timeout");
        final String evaluatedContents = evalTagContent(node, ps, attributeNames);
        final String result = IOUtils.system(evaluatedContents, MagicStrings.system_failed);
        return result;
    }
    
    private static String think(final Node node, final ParseState ps) {
        evalTagContent(node, ps, null);
        return "";
    }
    
    private static String explode(final Node node, final ParseState ps) {
        final String result = evalTagContent(node, ps, null);
        return explode(result);
    }
    
    private static String normalize(final Node node, final ParseState ps) {
        final String result = evalTagContent(node, ps, null);
        return ps.chatSession.bot.preProcessor.normalize(result);
    }
    
    private static String denormalize(final Node node, final ParseState ps) {
        final String result = evalTagContent(node, ps, null);
        return ps.chatSession.bot.preProcessor.denormalize(result);
    }
    
    private static String uppercase(final Node node, final ParseState ps) {
        final String result = evalTagContent(node, ps, null);
        return result.toUpperCase();
    }
    
    private static String lowercase(final Node node, final ParseState ps) {
        final String result = evalTagContent(node, ps, null);
        return result.toLowerCase();
    }
    
    private static String formal(final Node node, final ParseState ps) {
        final String result = evalTagContent(node, ps, null);
        return capitalizeString(result);
    }
    
    private static String sentence(final Node node, final ParseState ps) {
        final String result = evalTagContent(node, ps, null);
        if (result.length() > 1) {
            return result.substring(0, 1).toUpperCase() + result.substring(1, result.length());
        }
        return "";
    }
    
    private static String person(final Node node, final ParseState ps) {
        String result;
        if (node.hasChildNodes()) {
            result = evalTagContent(node, ps, null);
        }
        else {
            result = ps.leaf.starBindings.inputStars.star(0);
        }
        result = " " + result + " ";
        result = ps.chatSession.bot.preProcessor.person(result);
        return result.trim();
    }
    
    private static String person2(final Node node, final ParseState ps) {
        String result;
        if (node.hasChildNodes()) {
            result = evalTagContent(node, ps, null);
        }
        else {
            result = ps.leaf.starBindings.inputStars.star(0);
        }
        result = " " + result + " ";
        result = ps.chatSession.bot.preProcessor.person2(result);
        return result.trim();
    }
    
    private static String gender(final Node node, final ParseState ps) {
        String result = evalTagContent(node, ps, null);
        result = " " + result + " ";
        result = ps.chatSession.bot.preProcessor.gender(result);
        return result.trim();
    }
    
    private static String random(final Node node, final ParseState ps) {
        final NodeList childList = node.getChildNodes();
        final ArrayList<Node> liList = new ArrayList<Node>();
        for (int i = 0; i < childList.getLength(); ++i) {
            if (childList.item(i).getNodeName().equals("li")) {
                liList.add(childList.item(i));
            }
        }
        return evalTagContent(liList.get((int)(Math.random() * liList.size())), ps, null);
    }
    
    private static String unevaluatedAIML(final Node node, final ParseState ps) {
        final String result = learnEvalTagContent(node, ps);
        return unevaluatedXML(result, node, ps);
    }
    
    private static String recursLearn(final Node node, final ParseState ps) {
        final String nodeName = node.getNodeName();
        if (nodeName.equals("#text")) {
            return node.getNodeValue();
        }
        if (nodeName.equals("eval")) {
            return evalTagContent(node, ps, null);
        }
        return unevaluatedAIML(node, ps);
    }
    
    private static String learnEvalTagContent(final Node node, final ParseState ps) {
        String result = "";
        final NodeList childList = node.getChildNodes();
        for (int i = 0; i < childList.getLength(); ++i) {
            final Node child = childList.item(i);
            result += recursLearn(child, ps);
        }
        return result;
    }
    
    private static String learn(final Node node, final ParseState ps) {
        final NodeList childList = node.getChildNodes();
        String pattern = "";
        String that = "*";
        String template = "";
        for (int i = 0; i < childList.getLength(); ++i) {
            if (childList.item(i).getNodeName().equals("category")) {
                final NodeList grandChildList = childList.item(i).getChildNodes();
                for (int j = 0; j < grandChildList.getLength(); ++j) {
                    if (grandChildList.item(j).getNodeName().equals("pattern")) {
                        pattern = recursLearn(grandChildList.item(j), ps);
                    }
                    else if (grandChildList.item(j).getNodeName().equals("that")) {
                        that = recursLearn(grandChildList.item(j), ps);
                    }
                    else if (grandChildList.item(j).getNodeName().equals("template")) {
                        template = recursLearn(grandChildList.item(j), ps);
                    }
                }
                pattern = pattern.substring("<pattern>".length(), pattern.length() - "</pattern>".length());
                if (template.length() >= "<template></template>".length()) {
                    template = template.substring("<template>".length(), template.length() - "</template>".length());
                }
                if (that.length() >= "<that></that>".length()) {
                    that = that.substring("<that>".length(), that.length() - "</that>".length());
                }
                pattern = pattern.toUpperCase();
                that = that.toUpperCase();
                if (MagicBooleans.trace_mode) {
                    System.out.println("Learn Pattern = " + pattern);
                    System.out.println("Learn That = " + that);
                    System.out.println("Learn Template = " + template);
                }
                Category c;
                if (node.getNodeName().equals("learn")) {
                    c = new Category(0, pattern, that, "*", template, MagicStrings.null_aiml_file);
                }
                else {
                    c = new Category(0, pattern, that, "*", template, MagicStrings.learnf_aiml_file);
                    ps.chatSession.bot.learnfGraph.addCategory(c);
                }
                ps.chatSession.bot.brain.addCategory(c);
            }
        }
        return "";
    }
    
    private static String loopCondition(final Node node, final ParseState ps) {
        boolean loop = true;
        String result = "";
        final int loopCnt = 0;
        while (loop && loopCnt < MagicNumbers.max_loops) {
            String loopResult = condition(node, ps);
            if (loopResult.trim().equals(MagicStrings.too_much_recursion)) {
                return MagicStrings.too_much_recursion;
            }
            if (loopResult.contains("<loop/>")) {
                loopResult = loopResult.replace("<loop/>", "");
                loop = true;
            }
            else {
                loop = false;
            }
            result += loopResult;
        }
        if (loopCnt >= MagicNumbers.max_loops) {
            result = MagicStrings.too_much_looping;
        }
        return result;
    }
    
    private static String condition(final Node node, final ParseState ps) {
        final String result = "";
        final NodeList childList = node.getChildNodes();
        final ArrayList<Node> liList = new ArrayList<Node>();
        String predicate = null;
        String varName = null;
        String value = null;
        final HashSet<String> attributeNames = Utilities.stringSet("name", "var", "value");
        predicate = getAttributeOrTagValue(node, ps, "name");
        varName = getAttributeOrTagValue(node, ps, "var");
        for (int i = 0; i < childList.getLength(); ++i) {
            if (childList.item(i).getNodeName().equals("li")) {
                liList.add(childList.item(i));
            }
        }
        if (liList.size() == 0 && (value = getAttributeOrTagValue(node, ps, "value")) != null && predicate != null && ps.chatSession.predicates.get(predicate).equals(value)) {
            return evalTagContent(node, ps, attributeNames);
        }
        if (liList.size() == 0 && (value = getAttributeOrTagValue(node, ps, "value")) != null && varName != null && ps.vars.get(varName).equals(value)) {
            return evalTagContent(node, ps, attributeNames);
        }
        for (int i = 0; i < liList.size() && result.equals(""); ++i) {
            final Node n = liList.get(i);
            String liPredicate = predicate;
            String liVarName = varName;
            if (liPredicate == null) {
                liPredicate = getAttributeOrTagValue(n, ps, "name");
            }
            if (liVarName == null) {
                liVarName = getAttributeOrTagValue(n, ps, "var");
            }
            value = getAttributeOrTagValue(n, ps, "value");
            if (value == null) {
                return evalTagContent(n, ps, attributeNames);
            }
            if (liPredicate != null && value != null && (ps.chatSession.predicates.get(liPredicate).equals(value) || (ps.chatSession.predicates.containsKey(liPredicate) && value.equals("*")))) {
                return evalTagContent(n, ps, attributeNames);
            }
            if (liVarName != null && value != null && (ps.vars.get(liVarName).equals(value) || (ps.vars.containsKey(liPredicate) && value.equals("*")))) {
                return evalTagContent(n, ps, attributeNames);
            }
        }
        return "";
    }
    
    public static boolean evalTagForLoop(final Node node) {
        final NodeList childList = node.getChildNodes();
        for (int i = 0; i < childList.getLength(); ++i) {
            if (childList.item(i).getNodeName().equals("loop")) {
                return true;
            }
        }
        return false;
    }
    
    private static String recursEval(final Node node, final ParseState ps) {
        try {
            final String nodeName = node.getNodeName();
            if (nodeName.equals("#text")) {
                return node.getNodeValue();
            }
            if (nodeName.equals("#comment")) {
                return "";
            }
            if (nodeName.equals("template")) {
                return evalTagContent(node, ps, null);
            }
            if (nodeName.equals("random")) {
                return random(node, ps);
            }
            if (nodeName.equals("condition")) {
                return loopCondition(node, ps);
            }
            if (nodeName.equals("srai")) {
                return srai(node, ps);
            }
            if (nodeName.equals("sr")) {
                return respond(ps.leaf.starBindings.inputStars.star(0), ps.that, ps.topic, ps.chatSession, AIMLProcessor.sraiCount);
            }
            if (nodeName.equals("sraix")) {
                return sraix(node, ps);
            }
            if (nodeName.equals("set")) {
                return set(node, ps);
            }
            if (nodeName.equals("get")) {
                return get(node, ps);
            }
            if (nodeName.equals("map")) {
                return map(node, ps);
            }
            if (nodeName.equals("bot")) {
                return bot(node, ps);
            }
            if (nodeName.equals("id")) {
                return id(node, ps);
            }
            if (nodeName.equals("size")) {
                return size(node, ps);
            }
            if (nodeName.equals("vocabulary")) {
                return vocabulary(node, ps);
            }
            if (nodeName.equals("program")) {
                return program(node, ps);
            }
            if (nodeName.equals("date")) {
                return date(node, ps);
            }
            if (nodeName.equals("interval")) {
                return interval(node, ps);
            }
            if (nodeName.equals("think")) {
                return think(node, ps);
            }
            if (nodeName.equals("system")) {
                return system(node, ps);
            }
            if (nodeName.equals("explode")) {
                return explode(node, ps);
            }
            if (nodeName.equals("normalize")) {
                return normalize(node, ps);
            }
            if (nodeName.equals("denormalize")) {
                return denormalize(node, ps);
            }
            if (nodeName.equals("uppercase")) {
                return uppercase(node, ps);
            }
            if (nodeName.equals("lowercase")) {
                return lowercase(node, ps);
            }
            if (nodeName.equals("formal")) {
                return formal(node, ps);
            }
            if (nodeName.equals("sentence")) {
                return sentence(node, ps);
            }
            if (nodeName.equals("person")) {
                return person(node, ps);
            }
            if (nodeName.equals("person2")) {
                return person2(node, ps);
            }
            if (nodeName.equals("gender")) {
                return gender(node, ps);
            }
            if (nodeName.equals("star")) {
                return inputStar(node, ps);
            }
            if (nodeName.equals("thatstar")) {
                return thatStar(node, ps);
            }
            if (nodeName.equals("topicstar")) {
                return topicStar(node, ps);
            }
            if (nodeName.equals("that")) {
                return that(node, ps);
            }
            if (nodeName.equals("input")) {
                return input(node, ps);
            }
            if (nodeName.equals("request")) {
                return request(node, ps);
            }
            if (nodeName.equals("response")) {
                return response(node, ps);
            }
            if (nodeName.equals("learn") || nodeName.equals("learnf")) {
                return learn(node, ps);
            }
            if (AIMLProcessor.extension != null && AIMLProcessor.extension.extensionTagSet().contains(nodeName)) {
                return AIMLProcessor.extension.recursEval(node, ps);
            }
            return genericXML(node, ps);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    private static String evalTemplate(String template, final ParseState ps) {
        String response = MagicStrings.template_failed;
        try {
            template = "<template>" + template + "</template>";
            final Node root = DomUtils.parseString(template);
            response = recursEval(root, ps);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    
    public static boolean validTemplate(String template) {
        try {
            template = "<template>" + template + "</template>";
            DomUtils.parseString(template);
            return true;
        }
        catch (Exception e) {
            System.out.println("Invalid Template " + template);
            return false;
        }
    }
    
    static {
        AIMLProcessor.sraiCount = 0;
        AIMLProcessor.repeatCount = 0;
        AIMLProcessor.trace_count = 0;
    }
}
