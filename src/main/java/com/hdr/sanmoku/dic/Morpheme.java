// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.dic;

import com.hdr.sanmoku.util.Misc;
import java.util.Iterator;

public final class Morpheme
{
    private static final byte[] morps;
    private static final byte[] morpMap;
    private static final byte[] leafs;
    private static final byte[] leafAccCounts;
    private static final int nextBase;
    
    public static Iterable<Entry> getMorphemes(final int n) {
        return new Iterable<Entry>() {
            @Override
            public Iterator<Entry> iterator() {
                return new MorphemeIterator(n);
            }
        };
    }
    
    private static final int nextNode(final int n) {
        final long leaf = getLeaf(n);
        if (!hasNext(leaf, n)) {
            return -1;
        }
        return nextNode(leaf, n);
    }
    
    private static final boolean hasNext(final long n, final int n2) {
        return (n & 1L << n2 % 64) != 0x0L;
    }
    
    private static final int nextNode(final long n, final int n2) {
        final int n3 = n2 / 64;
        return Morpheme.nextBase + ((Morpheme.leafAccCounts[n3 * 2 + 0] & 0xFF) << 8 | (Morpheme.leafAccCounts[n3 * 2 + 1] & 0xFF)) + Long.bitCount(n & (1L << n2 % 64) - 1L);
    }
    
    private static final long getLeaf(final int n) {
        final int n2 = n / 64;
        return (long)Morpheme.leafs[n2 * 8 + 0] << 56 | (long)(Morpheme.leafs[n2 * 8 + 1] & 0xFF) << 48 | (long)(Morpheme.leafs[n2 * 8 + 2] & 0xFF) << 40 | (long)(Morpheme.leafs[n2 * 8 + 3] & 0xFF) << 32 | (long)(Morpheme.leafs[n2 * 8 + 4] & 0xFF) << 24 | (long)(Morpheme.leafs[n2 * 8 + 5] & 0xFF) << 16 | (long)(Morpheme.leafs[n2 * 8 + 6] & 0xFF) << 8 | (long)(Morpheme.leafs[n2 * 8 + 7] & 0xFF);
    }
    
    static {
        morps = Misc.readBytesFromFile("morp.info.bin", 2);
        morpMap = Misc.readBytesFromFile("morp.info.map", 4);
        leafs = Misc.readBytesFromFile("morp.leaf.bin", 8);
        leafAccCounts = Misc.readBytesFromFile("morp.leaf.cnt.bin", 2);
        nextBase = Misc.readIntFromFile("morp.base.bin");
    }
    
    static class Entry
    {
        public final short posId;
        public final short cost;
        public final int morphemeId;
        
        private Entry(final int morphemeId) {
            final int n = (Morpheme.morps[morphemeId * 2 + 0] & 0xFF) << 8 | (Morpheme.morps[morphemeId * 2 + 1] & 0xFF);
            this.posId = (short)((short)(Morpheme.morpMap[n * 4 + 0] << 8) | (short)(Morpheme.morpMap[n * 4 + 1] & 0xFF));
            this.cost = (short)((short)(Morpheme.morpMap[n * 4 + 2] << 8) | (short)(Morpheme.morpMap[n * 4 + 3] & 0xFF));
            this.morphemeId = morphemeId;
        }
    }
    
    static class MorphemeIterator implements Iterator<Entry>
    {
        private int node;
        
        public MorphemeIterator(final int node) {
            this.node = node;
        }
        
        @Override
        public boolean hasNext() {
            return this.node != -1;
        }
        
        @Override
        public Entry next() {
            final Entry entry = new Entry(this.node);
            this.node = nextNode(this.node);
            return entry;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
