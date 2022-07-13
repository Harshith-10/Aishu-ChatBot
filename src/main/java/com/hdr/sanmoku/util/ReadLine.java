// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;

public class ReadLine
{
    private final BufferedReader br;
    
    public ReadLine(final InputStream in) throws IOException {
        this.br = new BufferedReader(new InputStreamReader(in));
    }
    
    public void close() {
        try {
            this.br.close();
        }
        catch (IOException ex) {}
    }
    
    public String read() throws IOException {
        return this.br.readLine();
    }
}
