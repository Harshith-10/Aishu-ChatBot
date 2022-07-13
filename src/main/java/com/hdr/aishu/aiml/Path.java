// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.util.ArrayList;

public class Path extends ArrayList<String>
{
    public String word;
    public Path next;
    public int length;
    
    private Path() {
        this.next = null;
        this.word = null;
        this.length = 0;
    }
    
    public static Path sentenceToPath(String sentence) {
        sentence = sentence.trim();
        return arrayToPath(sentence.split(" "));
    }
    
    public static String pathToSentence(final Path path) {
        String result = "";
        for (Path p = path; p != null; p = p.next) {
            result = result + " " + path.word;
        }
        return result.trim();
    }
    
    private static Path arrayToPath(final String[] array) {
        Path tail = null;
        Path head = null;
        for (int i = array.length - 1; i >= 0; --i) {
            head = new Path();
            head.word = array[i];
            if ((head.next = tail) == null) {
                head.length = 1;
            }
            else {
                head.length = tail.length + 1;
            }
            tail = head;
        }
        return head;
    }
    
    private static Path arrayToPath(final String[] array, final int index) {
        if (index >= array.length) {
            return null;
        }
        final Path newPath = new Path();
        newPath.word = array[index];
        newPath.next = arrayToPath(array, index + 1);
        if (newPath.next == null) {
            newPath.length = 1;
        }
        else {
            newPath.length = newPath.next.length + 1;
        }
        return newPath;
    }
    
    public void print() {
        String result = "";
        for (Path p = this; p != null; p = p.next) {
            result = result + p.word + ",";
        }
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        System.out.println(result);
    }
}
