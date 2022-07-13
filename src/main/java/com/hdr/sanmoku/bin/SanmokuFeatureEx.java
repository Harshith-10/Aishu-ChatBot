// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.bin;

import java.io.IOException;

import com.hdr.sanmoku.FeatureEx;
import com.hdr.sanmoku.Morpheme;
import com.hdr.sanmoku.Tagger;
import com.hdr.sanmoku.util.ReadLine;

public final class SanmokuFeatureEx
{
    public static void main(final String[] array) throws IOException {
        if (array.length != 0) {
            System.err.println("Usage: java net.reduls.bin.SanmokuFeatureEx");
            System.exit(1);
        }
        final ReadLine readLine = new ReadLine(System.in);
        for (String s = readLine.read(); s != null; s = readLine.read()) {
            for (final Morpheme morpheme : Tagger.parse(s)) {
                final FeatureEx featureEx = new FeatureEx(morpheme);
                System.out.println(morpheme.surface + "\t" + morpheme.feature + "," + ((featureEx.baseform.length() == 0) ? "*" : featureEx.baseform) + "," + ((featureEx.reading.length() == 0) ? "*" : featureEx.reading) + "," + ((featureEx.pronunciation.length() == 0) ? "*" : featureEx.pronunciation));
            }
            System.out.println("EOS");
        }
    }
}
