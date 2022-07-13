// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessor
{
    public int normalCount;
    public int denormalCount;
    public int personCount;
    public int person2Count;
    public int genderCount;
    public String[] normalSubs;
    public Pattern[] normalPatterns;
    public String[] denormalSubs;
    public Pattern[] denormalPatterns;
    public String[] personSubs;
    public Pattern[] personPatterns;
    public String[] person2Subs;
    public Pattern[] person2Patterns;
    public String[] genderSubs;
    public Pattern[] genderPatterns;
    
    public PreProcessor(final Bot bot) {
        this.normalCount = 0;
        this.denormalCount = 0;
        this.personCount = 0;
        this.person2Count = 0;
        this.genderCount = 0;
        this.normalSubs = new String[MagicNumbers.max_substitutions];
        this.normalPatterns = new Pattern[MagicNumbers.max_substitutions];
        this.denormalSubs = new String[MagicNumbers.max_substitutions];
        this.denormalPatterns = new Pattern[MagicNumbers.max_substitutions];
        this.personSubs = new String[MagicNumbers.max_substitutions];
        this.personPatterns = new Pattern[MagicNumbers.max_substitutions];
        this.person2Subs = new String[MagicNumbers.max_substitutions];
        this.person2Patterns = new Pattern[MagicNumbers.max_substitutions];
        this.genderSubs = new String[MagicNumbers.max_substitutions];
        this.genderPatterns = new Pattern[MagicNumbers.max_substitutions];
        this.normalCount = this.readSubstitutions(MagicStrings.config_path + "/normal.txt", this.normalPatterns, this.normalSubs);
        this.denormalCount = this.readSubstitutions(MagicStrings.config_path + "/denormal.txt", this.denormalPatterns, this.denormalSubs);
        this.personCount = this.readSubstitutions(MagicStrings.config_path + "/person.txt", this.personPatterns, this.personSubs);
        this.person2Count = this.readSubstitutions(MagicStrings.config_path + "/person2.txt", this.person2Patterns, this.person2Subs);
        this.genderCount = this.readSubstitutions(MagicStrings.config_path + "/gender.txt", this.genderPatterns, this.genderSubs);
        System.out.println("Preprocessor: " + this.normalCount + " norms " + this.personCount + " persons " + this.person2Count + " person2 ");
    }
    
    public String normalize(final String request) {
        return this.substitute(request, this.normalPatterns, this.normalSubs, this.normalCount);
    }
    
    public String denormalize(final String request) {
        return this.substitute(request, this.denormalPatterns, this.denormalSubs, this.denormalCount);
    }
    
    public String person(final String input) {
        return this.substitute(input, this.personPatterns, this.personSubs, this.personCount);
    }
    
    public String person2(final String input) {
        return this.substitute(input, this.person2Patterns, this.person2Subs, this.person2Count);
    }
    
    public String gender(final String input) {
        return this.substitute(input, this.genderPatterns, this.genderSubs, this.genderCount);
    }
    
    String substitute(final String request, final Pattern[] patterns, final String[] subs, final int count) {
        String result = " " + request + " ";
        try {
            for (int i = 0; i < count; ++i) {
                final String replacement = subs[i];
                final Pattern p = patterns[i];
                final Matcher m = p.matcher(result);
                if (m.find()) {
                    result = m.replaceAll(replacement);
                }
            }
            while (result.contains("  ")) {
                result = result.replace("  ", " ");
            }
            result = result.trim();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result.trim();
    }
    
    public int readSubstitutionsFromInputStream(final InputStream in, final Pattern[] patterns, final String[] subs) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        int subCount = 0;
        try {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                final Pattern pattern = Pattern.compile("\"(.*?)\",\"(.*?)\"", 32);
                final Matcher matcher = pattern.matcher(strLine);
                if (matcher.find() && subCount < MagicNumbers.max_substitutions) {
                    subs[subCount] = matcher.group(2);
                    final String quotedPattern = Pattern.quote(matcher.group(1));
                    patterns[subCount] = Pattern.compile(quotedPattern, 2);
                    ++subCount;
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return subCount;
    }
    
    int readSubstitutions(final String filename, final Pattern[] patterns, final String[] subs) {
        int subCount = 0;
        try {
            final File file = new File(filename);
            if (file.exists()) {
                final FileInputStream fstream = new FileInputStream(filename);
                subCount = this.readSubstitutionsFromInputStream(fstream, patterns, subs);
                fstream.close();
            }
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return subCount;
    }
    
    public String[] sentenceSplit(String line) {
        line = line.replace("\u3002", ".");
        line = line.replace("\uff1f", "?");
        line = line.replace("\uff01", "!");
        final String[] result = line.split("[\\.!\\?]");
        for (int i = 0; i < result.length; ++i) {
            result[i] = result[i].trim();
        }
        return result;
    }
    
    public void normalizeFile(final String infile, final String outfile) {
        try {
            BufferedWriter bw = null;
            final FileInputStream fstream = new FileInputStream(infile);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            bw = new BufferedWriter(new FileWriter(outfile));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                strLine = this.normalize(strLine);
                bw.write(strLine);
                bw.newLine();
            }
            bw.close();
            br.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
