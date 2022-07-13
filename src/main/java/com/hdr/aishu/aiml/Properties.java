// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.HashMap;

public class Properties extends HashMap<String, String>
{
    public String get(final String key) {
        final String result = super.get(key);
        if (result == null) {
            return MagicStrings.unknown_property_value;
        }
        return result;
    }
    
    public void getPropertiesFromInputStream(final InputStream in) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.contains(":")) {
                    final String property = strLine.substring(0, strLine.indexOf(":"));
                    final String value = strLine.substring(strLine.indexOf(":") + 1);
                    this.put(property, value);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void getProperties(final String filename) {
        System.out.println("Get Properties: " + filename);
        try {
            final File file = new File(filename);
            if (file.exists()) {
                System.out.println("Exists: " + filename);
                final FileInputStream fstream = new FileInputStream(filename);
                this.getPropertiesFromInputStream(fstream);
                fstream.close();
            }
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
