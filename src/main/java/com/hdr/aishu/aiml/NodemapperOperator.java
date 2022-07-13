// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class NodemapperOperator
{
    public static int size(final Nodemapper node) {
        final HashSet set = new HashSet();
        if (node.shortCut) {
            set.add("<THAT>");
        }
        if (node.key != null) {
            set.add(node.key);
        }
        if (node.map != null) {
            set.addAll(node.map.keySet());
        }
        return set.size();
    }
    
    public static void put(final Nodemapper node, final String key, final Nodemapper value) {
        if (node.map != null) {
            node.map.put(key, value);
        }
        else {
            node.key = key;
            node.value = value;
        }
    }
    
    public static Nodemapper get(final Nodemapper node, final String key) {
        if (node.map != null) {
            return node.map.get(key);
        }
        if (key.equals(node.key)) {
            return node.value;
        }
        return null;
    }
    
    public static boolean containsKey(final Nodemapper node, final String key) {
        if (node.map != null) {
            return node.map.containsKey(key);
        }
        return key.equals(node.key);
    }
    
    public static void printKeys(final Nodemapper node) {
        final Set set = keySet(node);
        final Iterator iter = set.iterator();
        while (iter.hasNext()) {
            System.out.println("" + iter.next());
        }
    }
    
    public static Set<String> keySet(final Nodemapper node) {
        if (node.map != null) {
            return node.map.keySet();
        }
        final Set set = new HashSet();
        if (node.key != null) {
            set.add(node.key);
        }
        return (Set<String>)set;
    }
    
    public static boolean isLeaf(final Nodemapper node) {
        return node.category != null;
    }
    
    public static void upgrade(final Nodemapper node) {
        (node.map = new HashMap<String, Nodemapper>()).put(node.key, node.value);
        node.key = null;
        node.value = null;
    }
}
