// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.dic;

import com.hdr.sanmoku.util.Misc;

public final class SurfaceId
{
    private static final int idOffset;
    private static final byte[] nodes;
    private static final byte[] exts;
    private static final byte[] char_to_chck;
    
    public static void eachCommonPrefix(final String s, final int n, final WordDic.Callback callback) {
        long node = getNode(0);
        int idOffset = SurfaceId.idOffset;
        final CodeStream codeStream = new CodeStream(s, n);
        while (true) {
            if (isTerminal(node)) {
                WordDic.eachViterbiNode(callback, idOffset++, n, codeStream.position() - n, false);
            }
            if (codeStream.isEos()) {
                return;
            }
            if (!checkEncodedChildren(codeStream, node)) {
                return;
            }
            final char read = read(codeStream);
            final long node2 = getNode(base(node) + read);
            if (chck(node2) != read) {
                return;
            }
            node = node2;
            idOffset += siblingTotal(node);
        }
    }
    
    private static char read(final CodeStream codeStream) {
        return (char)(SurfaceId.char_to_chck[codeStream.read()] & 0xFF);
    }
    
    private static boolean checkEncodedChildren(final CodeStream codeStream, final long n) {
        switch (type(n)) {
            case 0: {
                return checkEC(codeStream, n);
            }
            default: {
                return true;
            }
        }
    }
    
    private static boolean checkEC(final CodeStream codeStream, final long n) {
        final char c = (char)(n >> 27 & 0x7FL);
        return c == '\0' || (read(codeStream) == c && !codeStream.isEos());
    }
    
    private static char chck(final long n) {
        return (char)(n >> 20 & 0x7FL);
    }
    
    private static int base(final long n) {
        return (int)(n & 0x7FFFFL);
    }
    
    private static boolean isTerminal(final long n) {
        return (n >> 19 & 0x1L) == 0x1L;
    }
    
    private static int type(final long n) {
        if ((n >> 39 & 0x1L) == 0x1L) {
            return 2 + (int)(n >> 38 & 0x1L);
        }
        return 0;
    }
    
    private static int siblingTotal(final long n) {
        switch (type(n)) {
            case 0: {
                return (int)(n >> 34 & 0x1FL);
            }
            case 2: {
                return (int)(n >> 27 & 0x7FFL);
            }
            default: {
                final int n2 = (int)(n >> 27 & 0x7FFL);
                return (SurfaceId.exts[n2 * 4 + 0] & 0xFF) << 24 | (SurfaceId.exts[n2 * 4 + 1] & 0xFF) << 16 | (SurfaceId.exts[n2 * 4 + 2] & 0xFF) << 8 | (SurfaceId.exts[n2 * 4 + 3] & 0xFF) << 0;
            }
        }
    }
    
    private static long getNode(final int n) {
        return (long)(SurfaceId.nodes[n * 5 + 0] & 0xFF) << 32 | (long)(SurfaceId.nodes[n * 5 + 1] & 0xFF) << 24 | (long)(SurfaceId.nodes[n * 5 + 2] & 0xFF) << 16 | (long)(SurfaceId.nodes[n * 5 + 3] & 0xFF) << 8 | (long)(SurfaceId.nodes[n * 5 + 4] & 0xFF);
    }
    
    static {
        nodes = Misc.readBytesFromFile("surface-id.bin.node", 1);
        exts = Misc.readBytesFromFile("surface-id.bin.ext", 1);
        char_to_chck = Misc.readBytesFromFile("surface-id.bin.char", 256, 1);
        idOffset = Misc.readIntFromFile("category.bin");
    }
}
