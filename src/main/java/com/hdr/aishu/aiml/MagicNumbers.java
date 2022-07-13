// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

public class MagicNumbers
{
    public static int node_activation_cnt;
    public static int node_size;
    public static int displayed_input_sample_size;
    public static int max_history;
    public static int max_stars;
    public static int max_graph_height;
    public static int max_substitutions;
    public static int max_recursion;
    public static int max_trace_length;
    public static int max_loops;
    public static int estimated_brain_size;
    public static int max_natural_number_digits;
    
    static {
        MagicNumbers.node_activation_cnt = 4;
        MagicNumbers.node_size = 4;
        MagicNumbers.displayed_input_sample_size = 6;
        MagicNumbers.max_history = 32;
        MagicNumbers.max_stars = 1000;
        MagicNumbers.max_graph_height = 100000;
        MagicNumbers.max_substitutions = 10000;
        MagicNumbers.max_recursion = 512;
        MagicNumbers.max_trace_length = 2048;
        MagicNumbers.max_loops = 10000;
        MagicNumbers.estimated_brain_size = 5000;
        MagicNumbers.max_natural_number_digits = 10000;
    }
}
