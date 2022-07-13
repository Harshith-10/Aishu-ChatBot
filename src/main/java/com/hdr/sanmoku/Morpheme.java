// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku;

public final class Morpheme
{
    public final String surface;
    public final String feature;
    public final int start;
    final int morphemeId;
    
    public Morpheme(final String surface, final String feature, final int start, final int morphemeId) {
        this.surface = surface;
        this.feature = feature;
        this.start = start;
        this.morphemeId = morphemeId;
    }
}
