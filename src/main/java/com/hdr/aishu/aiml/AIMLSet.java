// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;

public class AIMLSet extends HashSet<String>
{
    public String setName;
    int maxLength;
    String host;
    String botid;
    boolean isExternal;
    
    public AIMLSet(final String name) {
        this.maxLength = 1;
        this.isExternal = false;
        this.setName = name.toLowerCase();
        if (this.setName.equals(MagicStrings.natural_number_set_name)) {
            this.maxLength = 1;
        }
    }
    
    public boolean contains(final String s) {
        if (this.isExternal && MagicBooleans.enable_external_sets) {
            final String[] split = s.split(" ");
            if (split.length > this.maxLength) {
                return false;
            }
            final String query = MagicStrings.set_member_string + this.setName.toUpperCase() + " " + s;
            final String response = Sraix.sraix(null, query, "false", null, this.host, this.botid, null, "0");
            System.out.println("External " + this.setName + " contains " + s + "? " + response);
            return response.equals("true");
        }
        else {
            if (this.setName.equals(MagicStrings.natural_number_set_name)) {
                final Pattern numberPattern = Pattern.compile("[0-9]+");
                final Matcher numberMatcher = numberPattern.matcher(s);
                final Boolean isanumber = numberMatcher.matches();
                return isanumber;
            }
            return super.contains(s);
        }
    }
    
    public void writeAIMLSet() {
        System.out.println("Writing AIML Set " + this.setName);
        try {
            final FileWriter fstream = new FileWriter(MagicStrings.sets_path + "/" + this.setName + ".txt");
            final BufferedWriter out = new BufferedWriter(fstream);
            for (final String p : this) {
                out.write(p.trim());
                out.newLine();
            }
            out.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public int readAIMLSetFromInputStream(final InputStream in, final Bot bot) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        int cnt = 0;
        try {
            String strLine;
            while ((strLine = br.readLine()) != null && strLine.length() > 0) {
                ++cnt;
                if (strLine.startsWith("external")) {
                    final String[] splitLine = strLine.split(":");
                    if (splitLine.length < 4) {
                        continue;
                    }
                    this.host = splitLine[1];
                    this.botid = splitLine[2];
                    this.maxLength = Integer.parseInt(splitLine[3]);
                    this.isExternal = true;
                    System.out.println("Created external set at " + this.host + " " + this.botid);
                }
                else {
                    strLine = strLine.toUpperCase().trim();
                    final String[] splitLine = strLine.split(" ");
                    final int length = splitLine.length;
                    if (length > this.maxLength) {
                        this.maxLength = length;
                    }
                    this.add(strLine.trim());
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return cnt;
    }
    
    public void readAIMLSet(final Bot bot) {
        System.out.println("Reading AIML Set " + MagicStrings.sets_path + "/" + this.setName + ".txt");
        try {
            final File file = new File(MagicStrings.sets_path + "/" + this.setName + ".txt");
            if (file.exists()) {
                final FileInputStream fstream = new FileInputStream(MagicStrings.sets_path + "/" + this.setName + ".txt");
                this.readAIMLSetFromInputStream(fstream, bot);
                fstream.close();
            }
            else {
                System.out.println(MagicStrings.sets_path + "/" + this.setName + ".txt not found");
            }
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
