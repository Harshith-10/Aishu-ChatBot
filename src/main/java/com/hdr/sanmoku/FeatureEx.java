// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku;

import com.hdr.sanmoku.util.Misc;
import java.io.UnsupportedEncodingException;

public final class FeatureEx
{
    public final String baseform;
    public final String reading;
    public final String pronunciation;
    private static final byte[] info;
    private static final byte[] data;
    
    public FeatureEx(final Morpheme morpheme) {
        final long info = info(morpheme.morphemeId);
        this.baseform = baseform(info, morpheme);
        final String reading_pronunciation = reading_pronunciation(info);
        final int index = reading_pronunciation.indexOf(",");
        if (index == -1) {
            final String s = reading_pronunciation;
            this.pronunciation = s;
            this.reading = s;
        }
        else {
            this.reading = reading_pronunciation.substring(0, index);
            this.pronunciation = reading_pronunciation.substring(index + 1);
        }
    }
    
    private static String baseform(final long n, final Morpheme morpheme) {
        final int baseformOffset = baseformOffset(n);
        if (baseformOffset == 131071) {
            return morpheme.surface;
        }
        return text(baseformOffset, baseformLength(n));
    }
    
    private static String reading_pronunciation(final long n) {
        return text(rpOffset(n), rpLength(n));
    }
    
    private static long info(final int n) {
        return (long)(FeatureEx.info[n * 6 + 0] & 0xFF) << 40 | (long)(FeatureEx.info[n * 6 + 1] & 0xFF) << 32 | (long)(FeatureEx.info[n * 6 + 2] & 0xFF) << 24 | (long)(FeatureEx.info[n * 6 + 3] & 0xFF) << 16 | (long)(FeatureEx.info[n * 6 + 4] & 0xFF) << 8 | (long)(FeatureEx.info[n * 6 + 5] & 0xFF);
    }
    
    private static int baseformOffset(final long n) {
        return (int)(n & 0x1FFFFL);
    }
    
    private static int baseformLength(final long n) {
        return (int)(n >> 38 & 0xFL);
    }
    
    private static int rpOffset(final long n) {
        return (int)(n >> 17 & 0x1FFFFFL);
    }
    
    private static int rpLength(final long n) {
        return (int)(n >> 42 & 0x3FL);
    }
    
    private static String text(final int n, final int n2) {
        try {
            return new String(FeatureEx.data, n * 2, n2 * 2, "UTF-16BE");
        }
        catch (UnsupportedEncodingException cause) {
            throw new RuntimeException(cause);
        }
    }
    
    static {
        info = Misc.readBytesFromFile("feature.info.bin", 6);
        data = Misc.readBytesFromFile("feature.text.bin", 2);
    }
}
