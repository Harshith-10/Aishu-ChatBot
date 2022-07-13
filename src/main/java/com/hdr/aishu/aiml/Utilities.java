// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import com.hdr.aishu.aiml.utils.CalendarUtils;

import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.HashSet;

public class Utilities
{
    public static String fixCSV(String line) {
        while (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1);
        }
        if (line.startsWith("\"")) {
            line = line.substring(1, line.length());
        }
        if (line.endsWith("\"")) {
            line = line.substring(0, line.length() - 1);
        }
        line = line.replaceAll("\"\"", "\"");
        return line;
    }
    
    public static String tagTrim(String xmlExpression, final String tagName) {
        final String stag = "<" + tagName + ">";
        final String etag = "</" + tagName + ">";
        if (xmlExpression.length() >= (stag + etag).length()) {
            xmlExpression = xmlExpression.substring(stag.length());
            xmlExpression = xmlExpression.substring(0, xmlExpression.length() - etag.length());
        }
        return xmlExpression;
    }
    
    public static HashSet<String> stringSet(final String... strings) {
        final HashSet<String> set = new HashSet<String>();
        for (final String s : strings) {
            set.add(s);
        }
        return set;
    }
    
    public static String getFileFromInputStream(final InputStream in) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String contents = "";
        try {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0) {
                    contents += "\n";
                }
                else {
                    contents = contents + strLine + "\n";
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return contents.trim();
    }
    
    public static String getFile(final String filename) {
        String contents = "";
        try {
            final File file = new File(filename);
            if (file.exists()) {
                final FileInputStream fstream = new FileInputStream(filename);
                contents = getFileFromInputStream(fstream);
                fstream.close();
            }
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return contents;
    }
    
    public static String getCopyrightFromInputStream(final InputStream in) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String copyright = "";
        try {
            String strLine;
            while ((strLine = br.readLine()) != null) {
                if (strLine.length() == 0) {
                    copyright += "\n";
                }
                else {
                    copyright = copyright + "<!-- " + strLine + " -->\n";
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return copyright;
    }
    
    public static String getCopyright(final Bot bot, final String AIMLFilename) {
        String copyright = "";
        final String year = CalendarUtils.year();
        final String date = CalendarUtils.date();
        try {
            copyright = getFile(MagicStrings.config_path + "/copyright.txt");
            final String[] splitCopyright = copyright.split("\n");
            copyright = "";
            for (int i = 0; i < splitCopyright.length; ++i) {
                copyright = copyright + "<!-- " + splitCopyright[i] + " -->\n";
            }
            copyright = copyright.replace("[url]", bot.properties.get("url"));
            copyright = copyright.replace("[date]", date);
            copyright = copyright.replace("[YYYY]", year);
            copyright = copyright.replace("[version]", bot.properties.get("version"));
            copyright = copyright.replace("[botname]", bot.name.toUpperCase());
            copyright = copyright.replace("[filename]", AIMLFilename);
            copyright = copyright.replace("[botmaster]", bot.properties.get("botmaster"));
            copyright = copyright.replace("[organization]", bot.properties.get("organization"));
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return copyright;
    }
    
    public static String getPannousAPIKey() {
        String apiKey = getFile(MagicStrings.config_path + "/pannous-apikey.txt");
        if (apiKey.equals("")) {
            apiKey = MagicStrings.pannous_api_key;
        }
        return apiKey;
    }
    
    public static String getPannousLogin() {
        String login = getFile(MagicStrings.config_path + "/pannous-login.txt");
        if (login.equals("")) {
            login = MagicStrings.pannous_login;
        }
        return login;
    }
    
    public static boolean isCharCJK(final char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS;
    }
}
