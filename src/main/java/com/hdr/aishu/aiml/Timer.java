// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

public class Timer
{
    private long startTimeMillis;
    
    public Timer() {
        this.start();
    }
    
    public void start() {
        this.startTimeMillis = System.currentTimeMillis();
    }
    
    public long elapsedTimeMillis() {
        return System.currentTimeMillis() - this.startTimeMillis + 1L;
    }
    
    public long elapsedRestartMs() {
        final long ms = System.currentTimeMillis() - this.startTimeMillis + 1L;
        this.start();
        return ms;
    }
    
    public float elapsedTimeSecs() {
        return this.elapsedTimeMillis() / 1000.0f;
    }
    
    public float elapsedTimeMins() {
        return this.elapsedTimeSecs() / 60.0f;
    }
}
