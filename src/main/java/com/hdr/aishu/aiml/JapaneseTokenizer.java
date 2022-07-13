// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.util.HashSet;
import java.util.regex.Matcher;

import com.hdr.sanmoku.Morpheme;
import com.hdr.sanmoku.Tagger;
import java.util.Set;
import java.util.regex.Pattern;

public class JapaneseTokenizer
{
    static final Pattern tagPattern;
    static Set<Character.UnicodeBlock> japaneseUnicodeBlocks;
    
    public static String buildFragment(final String fragment) {
        String result = "";
        for (final Morpheme e : Tagger.parse(fragment)) {
            result = result + e.surface + " ";
        }
        return result.trim();
    }
    
    public static String morphSentence(String sentence) {
        if (!MagicBooleans.jp_morphological_analysis) {
            return sentence;
        }
        String result = "";
        final Matcher matcher = JapaneseTokenizer.tagPattern.matcher(sentence);
        while (matcher.find()) {
            final int i = matcher.start();
            final int j = matcher.end();
            String prefix;
            if (i > 0) {
                prefix = sentence.substring(0, i - 1);
            }
            else {
                prefix = "";
            }
            final String tag = sentence.substring(i, j);
            result = result + " " + buildFragment(prefix) + " " + tag;
            if (j < sentence.length()) {
                sentence = sentence.substring(j, sentence.length());
            }
            else {
                sentence = "";
            }
        }
        for (result = result + " " + buildFragment(sentence); result.contains("$ "); result = result.replace("$ ", "$")) {}
        while (result.contains("  ")) {
            result = result.replace("  ", " ");
        }
        return result.trim();
    }
    
    static {
        tagPattern = Pattern.compile("(<.*>.*</.*>)|(<.*/>)");
        JapaneseTokenizer.japaneseUnicodeBlocks = new HashSet<Character.UnicodeBlock>() {
            {
                this.add(Character.UnicodeBlock.HIRAGANA);
                this.add(Character.UnicodeBlock.KATAKANA);
                this.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
            }
        };
    }
}
