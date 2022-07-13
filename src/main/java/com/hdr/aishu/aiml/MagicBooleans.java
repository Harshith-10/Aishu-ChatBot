// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

public class MagicBooleans
{
    public static boolean trace_mode;
    public static boolean enable_external_sets;
    public static boolean enable_external_maps;
    public static boolean jp_morphological_analysis;
    public static boolean fix_excel_csv;
    
    static {
        MagicBooleans.trace_mode = true;
        MagicBooleans.enable_external_sets = true;
        MagicBooleans.enable_external_maps = true;
        MagicBooleans.jp_morphological_analysis = false;
        MagicBooleans.fix_excel_csv = true;
    }
}
