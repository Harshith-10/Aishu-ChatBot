// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.dic;

public final class WordDic
{
    public static void search(final String s, final int n, final Callback callback) {
        SurfaceId.eachCommonPrefix(s, n, callback);
    }
    
    public static void eachViterbiNode(final Callback callback, final int n, final int n2, final int n3, final boolean b) {
        for (final Morpheme.Entry entry : Morpheme.getMorphemes(n)) {
            callback.call(new ViterbiNode(n2, (short)n3, entry.cost, entry.posId, b, entry.morphemeId));
        }
    }
    
    public interface Callback
    {
        void call(final ViterbiNode p0);
        
        boolean isEmpty();
    }
}
