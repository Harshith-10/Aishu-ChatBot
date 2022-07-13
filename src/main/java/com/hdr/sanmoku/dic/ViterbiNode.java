// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.dic;

public final class ViterbiNode
{
    public int cost;
    public ViterbiNode prev;
    public final int start;
    private final int length_posId_isSpace;
    public final int morphemeId;
    
    public ViterbiNode(final int start, final short n, final short cost, final short n2, final boolean b, final int morphemeId) {
        this.prev = null;
        this.cost = cost;
        this.start = start;
        this.length_posId_isSpace = (n << 17) + (n2 << 1) + (b ? 1 : 0);
        this.morphemeId = morphemeId;
    }
    
    public short length() {
        return (short)(this.length_posId_isSpace >> 17);
    }
    
    public short posId() {
        return (short)(this.length_posId_isSpace >> 1 & 0xFFFF);
    }
    
    public boolean isSpace() {
        return (this.length_posId_isSpace & 0x1) == 0x1;
    }
    
    public static ViterbiNode makeBOSEOS() {
        return new ViterbiNode(0, (short)0, (short)0, (short)0, false, 0);
    }
}
