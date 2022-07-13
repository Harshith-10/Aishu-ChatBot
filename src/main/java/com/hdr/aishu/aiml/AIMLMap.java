package com.hdr.aishu.aiml;

import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.HashMap;

public class AIMLMap extends HashMap<String, String> {
    public String mapName;
    String host;
    String botid;
    boolean isExternal;
    
    public AIMLMap(final String name) {
        this.isExternal = false;
        this.mapName = name;
    }
    
    public String get(final String key) {
        if (this.mapName.equals(MagicStrings.map_successor)) {
            try {
                final int number = Integer.parseInt(key);
                return String.valueOf(number + 1);
            }
            catch (Exception ex) {
                return MagicStrings.unknown_map_value;
            }
        }
        if (this.mapName.equals(MagicStrings.map_predecessor)) {
            try {
                final int number = Integer.parseInt(key);
                return String.valueOf(number - 1);
            }
            catch (Exception ex) {
                return MagicStrings.unknown_map_value;
            }
        }
        String value;
        if (this.isExternal && MagicBooleans.enable_external_sets) {
            final String query = this.mapName.toUpperCase() + " " + key;
            final String response = Sraix.sraix(null, query, MagicStrings.unknown_map_value, null, this.host, this.botid, null, "0");
            System.out.println("External " + this.mapName + "(" + key + ")=" + response);
            value = response;
        }
        else {
            value = super.get(key);
        }
        if (value == null) {
            value = MagicStrings.unknown_map_value;
        }
        System.out.println("AIMLMap get " + key + "=" + value);
        return value;
    }
    
    @Override
    public String put(final String key, final String value) {
        return super.put(key, value);
    }
    
    public int readAIMLMapFromInputStream(final InputStream in, final Bot bot) {
        int cnt = 0;
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String strLine;
            while ((strLine = br.readLine()) != null && strLine.length() > 0) {
                final String[] splitLine = strLine.split(":");
                if (splitLine.length >= 2) {
                    ++cnt;
                    if (strLine.startsWith(MagicStrings.remote_map_key)) {
                        if (splitLine.length < 3) {
                            continue;
                        }
                        this.host = splitLine[1];
                        this.botid = splitLine[2];
                        this.isExternal = true;
                        System.out.println("Created external map at " + this.host + " " + this.botid);
                    }
                    else {
                        final String key = splitLine[0].toUpperCase();
                        final String value = splitLine[1];
                        this.put(key, value);
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return cnt;
    }
    
    public void readAIMLMap(final Bot bot) {
        System.out.println("Reading AIML Map " + MagicStrings.maps_path + "/" + this.mapName + ".txt");
        try {
            final File file = new File(MagicStrings.maps_path + "/" + this.mapName + ".txt");
            if (file.exists()) {
                final FileInputStream fstream = new FileInputStream(MagicStrings.maps_path + "/" + this.mapName + ".txt");
                this.readAIMLMapFromInputStream(fstream, bot);
                fstream.close();
            }
            else {
                System.out.println(MagicStrings.maps_path + "/" + this.mapName + ".txt not found");
            }
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
