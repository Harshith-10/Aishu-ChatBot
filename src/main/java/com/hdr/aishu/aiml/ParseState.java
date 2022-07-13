// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

public class ParseState
{
    public Nodemapper leaf;
    public String input;
    public String that;
    public String topic;
    public Chat chatSession;
    public int depth;
    public Predicates vars;
    
    public ParseState(final int depth, final Chat chatSession, final String input, final String that, final String topic, final Nodemapper leaf) {
        this.chatSession = chatSession;
        this.input = input;
        this.that = that;
        this.topic = topic;
        this.leaf = leaf;
        this.depth = depth;
        this.vars = new Predicates();
    }
}
