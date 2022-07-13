// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.dic;

import java.io.DataInputStream;

import com.hdr.sanmoku.util.Misc;

public final class Matrix
{
    private static final byte[] matrix;
    private static final int leftNum;
    private static final byte[] posid_map;
    private static final byte[] val;
    
    public static short linkCost(final short n, final short n2) {
        final int n3 = posid(n) * Matrix.leftNum + posid(n2);
        final int n4 = (int)(node(n3 / 4) >> n3 % 4 * 14) & 0x3FFF;
        return (short)(Matrix.val[n4 * 2] << 8 | (Matrix.val[n4 * 2 + 1] & 0xFF));
    }
    
    private static short posid(final short n) {
        return (short)(Matrix.posid_map[n * 2] << 8 | (Matrix.posid_map[n * 2 + 1] & 0xFF));
    }
    
    private static long node(final int n) {
        return (long)(Matrix.matrix[n * 7 + 0] & 0xFF) << 48 | (long)(Matrix.matrix[n * 7 + 1] & 0xFF) << 40 | (long)(Matrix.matrix[n * 7 + 2] & 0xFF) << 32 | (long)(Matrix.matrix[n * 7 + 3] & 0xFF) << 24 | (long)(Matrix.matrix[n * 7 + 4] & 0xFF) << 16 | (long)(Matrix.matrix[n * 7 + 5] & 0xFF) << 8 | (long)(Matrix.matrix[n * 7 + 6] & 0xFF);
    }
    
    static {
        posid_map = Misc.readBytesFromFile("posid-map.bin", 2);
        val = Misc.readBytesFromFile("matrix.map", 2);
        final DataInputStream openDictionaryDataAsDIS = Misc.openDictionaryDataAsDIS("matrix.bin");
        final int int1 = Misc.readInt(openDictionaryDataAsDIS);
        leftNum = Misc.readInt(openDictionaryDataAsDIS);
        matrix = new byte[int1 * 7];
        try {
            openDictionaryDataAsDIS.readFully(Matrix.matrix, 0, Matrix.matrix.length);
        }
        catch (Exception cause) {
            throw new RuntimeException(cause);
        }
        Misc.close(openDictionaryDataAsDIS);
    }
}
