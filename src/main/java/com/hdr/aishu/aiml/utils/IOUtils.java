// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml.utils;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class IOUtils
{
    public static String readInputTextLine() {
        final BufferedReader lineOfText = new BufferedReader(new InputStreamReader(System.in));
        String textLine = null;
        try {
            textLine = lineOfText.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return textLine;
    }
    
    public static String system(final String evaluatedContents, final String failedString) {
        final Runtime rt = Runtime.getRuntime();
        System.out.println("System " + evaluatedContents);
        try {
            final Process p = rt.exec(evaluatedContents);
            final InputStream istrm = p.getInputStream();
            final InputStreamReader istrmrdr = new InputStreamReader(istrm);
            final BufferedReader buffrdr = new BufferedReader(istrmrdr);
            String result = "";
            String data = "";
            while ((data = buffrdr.readLine()) != null) {
                result = result + data + "\n";
            }
            System.out.println("Result = " + result);
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return failedString;
        }
    }
}
