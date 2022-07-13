package com.hdr.sanmoku.bin;

import java.io.IOException;
import java.util.Iterator;

import com.hdr.sanmoku.Morpheme;
import com.hdr.sanmoku.Tagger;
import com.hdr.sanmoku.util.ReadLine;

public final class Sanmoku {
    public static void main(final String[] array) throws IOException {
        if (array.length != 0 && (array.length != 1 || !array[0].equals("-wakati"))) {
            System.err.println("Usage: java net.reduls.bin.Sanmoku [-wakati]");
            System.exit(1);
        }
        final boolean b = array.length == 1;
        final ReadLine readLine = new ReadLine(System.in);
        if (b) {
            for (String s = readLine.read(); s != null; s = readLine.read()) {
                final Iterator<String> iterator = Tagger.wakati(s).iterator();
                while (iterator.hasNext()) {
                    System.out.print(iterator.next() + " ");
                }
                System.out.println("");
            }
        }
        else {
            for (String s2 = readLine.read(); s2 != null; s2 = readLine.read()) {
                for (final Morpheme morpheme : Tagger.parse(s2)) {
                    System.out.println(morpheme.surface + "\t" + morpheme.feature);
                }
                System.out.println("EOS");
            }
        }
    }
}
