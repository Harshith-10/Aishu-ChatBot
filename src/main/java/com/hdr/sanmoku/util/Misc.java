// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.sanmoku.util;

import java.io.DataInput;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

public final class Misc
{
    public static InputStream openDictionaryData(final String str) {
        return Misc.class.getResourceAsStream("/net/reduls/sanmoku/dicdata/" + str);
    }
    
    public static DataInputStream openDictionaryDataAsDIS(final String s) {
        return new DataInputStream(new BufferedInputStream(openDictionaryData(s), 80960));
    }
    
    public static BufferedReader openDictionaryDataAsBR(final String s) {
        try {
            return new BufferedReader(new InputStreamReader(openDictionaryData(s), "UTF-8"), 80960);
        }
        catch (IOException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    public static String readLine(final BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        }
        catch (IOException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    public static void close(final Closeable closeable) {
        try {
            closeable.close();
        }
        catch (IOException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    public static long readLong(final DataInput dataInput) {
        try {
            return dataInput.readLong();
        }
        catch (IOException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    public static int readInt(final DataInput dataInput) {
        try {
            return dataInput.readInt();
        }
        catch (IOException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    public static short readShort(final DataInput dataInput) {
        try {
            return dataInput.readShort();
        }
        catch (IOException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    public static byte readByte(final DataInput dataInput) {
        try {
            return dataInput.readByte();
        }
        catch (IOException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    public static char readChar(final DataInput dataInput) {
        try {
            return dataInput.readChar();
        }
        catch (IOException ex) {
            throw new AssertionError((Object)ex.getMessage());
        }
    }
    
    public static byte[] readBytesFromFile(final String s, final int n) {
        final DataInputStream openDictionaryDataAsDIS = openDictionaryDataAsDIS(s);
        final byte[] b = new byte[readInt(openDictionaryDataAsDIS) * n];
        try {
            openDictionaryDataAsDIS.readFully(b, 0, b.length);
        }
        catch (Exception cause) {
            throw new RuntimeException(cause);
        }
        return b;
    }
    
    public static byte[] readBytesFromFile(final String s, final int n, final int n2) {
        final DataInputStream openDictionaryDataAsDIS = openDictionaryDataAsDIS(s);
        final byte[] b = new byte[n * n2];
        try {
            openDictionaryDataAsDIS.readFully(b, 0, b.length);
        }
        catch (Exception cause) {
            throw new RuntimeException(cause);
        }
        return b;
    }
    
    public static int readIntFromFile(final String s) {
        final DataInputStream openDictionaryDataAsDIS = openDictionaryDataAsDIS(s);
        final int int1 = readInt(openDictionaryDataAsDIS);
        close(openDictionaryDataAsDIS);
        return int1;
    }
}
