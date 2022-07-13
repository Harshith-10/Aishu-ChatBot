// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.dic;

import java.io.BufferedReader;
import java.util.ArrayList;
import com.hdr.sanmoku.util.Misc;

public final class PartsOfSpeech
{
    private static final String[] posArray;
    
    public static final String get(final int n) {
        return PartsOfSpeech.posArray[n];
    }
    
    static {
        final BufferedReader openDictionaryDataAsBR = Misc.openDictionaryDataAsBR("pos.bin");
        final ArrayList<String> list = new ArrayList<String>();
        for (String e = Misc.readLine(openDictionaryDataAsBR); e != null; e = Misc.readLine(openDictionaryDataAsBR)) {
            list.add(e);
        }
        Misc.close(openDictionaryDataAsBR);
        posArray = new String[list.size()];
        for (int i = 0; i < PartsOfSpeech.posArray.length; ++i) {
            PartsOfSpeech.posArray[i] = list.get(i);
        }
    }
}
