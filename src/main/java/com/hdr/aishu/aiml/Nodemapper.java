// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.util.ArrayList;
import java.util.HashMap;

public class Nodemapper
{
    public Category category;
    public int height;
    public StarBindings starBindings;
    public HashMap<String, Nodemapper> map;
    public String key;
    public Nodemapper value;
    public boolean shortCut;
    public ArrayList<String> sets;
    
    public Nodemapper() {
        this.category = null;
        this.height = MagicNumbers.max_graph_height;
        this.starBindings = null;
        this.map = null;
        this.key = null;
        this.value = null;
        this.shortCut = false;
    }
}
