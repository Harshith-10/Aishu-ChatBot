// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml.utils;

public class MemoryUtils
{
    public static long totalMemory() {
        return Runtime.getRuntime().totalMemory();
    }
    
    public static long maxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
    
    public static long freeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}
