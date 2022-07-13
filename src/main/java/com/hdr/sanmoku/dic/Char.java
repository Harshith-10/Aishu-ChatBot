// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.dic;

import java.io.DataInputStream;

import com.hdr.sanmoku.util.Misc;

public final class Char
{
    private static final Category[] charCategorys;
    private static final byte[] charInfos;
    
    public static final Category category(final char c) {
        return Char.charCategorys[findNode(c) >> 16];
    }
    
    public static final boolean isCompatible(final char c, final char c2) {
        return (compatibleMask(c) & compatibleMask(c2)) != 0x0;
    }
    
    private static final int compatibleMask(final char c) {
        return findNode(c) & 0xFFFF;
    }
    
    public static final int findNode(final char c) {
        int n = 0;
        int n2 = Char.charInfos.length / 6;
        while (true) {
            final int n3 = n + (n2 - n) / 2;
            if (n2 - n == 1) {
                break;
            }
            if (c < nodeCode(n3)) {
                n2 = n3;
            }
            else {
                if (c < nodeCode(n3)) {
                    continue;
                }
                n = n3;
            }
        }
        return nodeValue(n);
    }
    
    public static final int nodeCode(final int n) {
        return (Char.charInfos[n * 6 + 0] & 0xFF) << 16 | (Char.charInfos[n * 6 + 1] & 0xFF) << 8 | (Char.charInfos[n * 6 + 2] & 0xFF) << 0;
    }
    
    public static final int nodeValue(final int n) {
        return (Char.charInfos[n * 6 + 3] & 0xFF) << 16 | (Char.charInfos[n * 6 + 4] & 0xFF) << 8 | (Char.charInfos[n * 6 + 5] & 0xFF) << 0;
    }
    
    static {
        final DataInputStream openDictionaryDataAsDIS = Misc.openDictionaryDataAsDIS("category.bin");
        final int int1 = Misc.readInt(openDictionaryDataAsDIS);
        charCategorys = new Category[int1];
        for (int i = 0; i < int1; ++i) {
            Char.charCategorys[i] = new Category(i, Misc.readByte(openDictionaryDataAsDIS) == 1, Misc.readByte(openDictionaryDataAsDIS) == 1, Misc.readByte(openDictionaryDataAsDIS));
        }
        Misc.close(openDictionaryDataAsDIS);
        charInfos = Misc.readBytesFromFile("code.bin", 6);
    }
    
    public static final class Category
    {
        public final int id;
        public final boolean invoke;
        public final boolean group;
        public final byte length;
        
        public Category(final int id, final boolean invoke, final boolean group, final byte length) {
            this.id = id;
            this.invoke = invoke;
            this.group = group;
            this.length = length;
        }
    }
}
