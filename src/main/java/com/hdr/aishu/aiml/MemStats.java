// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import com.hdr.aishu.aiml.utils.MemoryUtils;

public class MemStats
{
    public static long prevHeapSize;
    
    public static void memStats() {
        final long heapSize = MemoryUtils.totalMemory();
        final long heapMaxSize = MemoryUtils.maxMemory();
        final long heapFreeSize = MemoryUtils.freeMemory();
        final long diff = heapSize - MemStats.prevHeapSize;
        MemStats.prevHeapSize = heapSize;
        System.out.println("Heap " + heapSize + " MaxSize " + heapMaxSize + " Free " + heapFreeSize + " Diff " + diff);
    }
    
    static {
        MemStats.prevHeapSize = 0L;
    }
}
