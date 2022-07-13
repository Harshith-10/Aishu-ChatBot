// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.util.ArrayList;

public class Stars extends ArrayList<String>
{
    public String star(final int i) {
        if (i < this.size()) {
            return this.get(i);
        }
        return null;
    }
}
