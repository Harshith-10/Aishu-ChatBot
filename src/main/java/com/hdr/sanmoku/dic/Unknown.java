// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.dic;

public final class Unknown
{
    private static final Char.Category space;
    
    public static void search(final String s, final int index, final WordDic.Callback callback) {
        final char char1 = s.charAt(index);
        final Char.Category category = Char.category(char1);
        if (!callback.isEmpty() && !category.invoke) {
            return;
        }
        final boolean b = category == Unknown.space;
        int min;
        int i;
        for (min = Math.min(s.length(), category.length + index), i = index; i < min; ++i) {
            WordDic.eachViterbiNode(callback, category.id, index, i - index + 1, b);
            if (i + 1 != min && !Char.isCompatible(char1, s.charAt(i + 1))) {
                return;
            }
        }
        if (category.group && i < s.length()) {
            while (i < s.length()) {
                if (!Char.isCompatible(char1, s.charAt(i))) {
                    WordDic.eachViterbiNode(callback, category.id, index, i - index, b);
                    return;
                }
                ++i;
            }
            WordDic.eachViterbiNode(callback, category.id, index, s.length() - index, b);
        }
    }
    
    static {
        space = Char.category(' ');
    }
}
