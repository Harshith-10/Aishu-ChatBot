// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

public class MagicStrings
{
    public static String programNameVersion;
    public static String comment;
    public static String aimlif_split_char;
    public static String default_bot;
    public static String default_language;
    public static String aimlif_split_char_name;
    public static String aimlif_file_suffix;
    public static String ab_sample_file;
    public static String pannous_api_key;
    public static String pannous_login;
    public static String sraix_failed;
    public static String sraix_no_hint;
    public static String sraix_event_hint;
    public static String sraix_pic_hint;
    public static String unknown_aiml_file;
    public static String deleted_aiml_file;
    public static String learnf_aiml_file;
    public static String null_aiml_file;
    public static String inappropriate_aiml_file;
    public static String profanity_aiml_file;
    public static String insult_aiml_file;
    public static String reductions_update_aiml_file;
    public static String predicates_aiml_file;
    public static String update_aiml_file;
    public static String personality_aiml_file;
    public static String sraix_aiml_file;
    public static String oob_aiml_file;
    public static String unfinished_aiml_file;
    public static String inappropriate_filter;
    public static String profanity_filter;
    public static String insult_filter;
    public static String deleted_template;
    public static String unfinished_template;
    public static String unknown_history_item;
    public static String default_bot_response;
    public static String error_bot_response;
    public static String schedule_error;
    public static String system_failed;
    public static String unknown_predicate_value;
    public static String unknown_property_value;
    public static String unknown_map_value;
    public static String unknown_customer_id;
    public static String unknown_bot_name;
    public static String default_that;
    public static String default_topic;
    public static String template_failed;
    public static String too_much_recursion;
    public static String too_much_looping;
    public static String blank_template;
    public static String null_input;
    public static String null_star;
    public static String set_member_string;
    public static String remote_map_key;
    public static String remote_set_key;
    public static String natural_number_set_name;
    public static String map_successor;
    public static String map_predecessor;
    public static String root_path;
    public static String bot_path;
    public static String bot_name_path;
    public static String aimlif_path;
    public static String aiml_path;
    public static String config_path;
    public static String log_path;
    public static String sets_path;
    public static String maps_path;
    
    static {
        MagicStrings.programNameVersion = "Program AB 0.0.4.2 beta -- AI Foundation Reference AIML 2.0 implementation";
        MagicStrings.comment = "removed some recursion from Path";
        MagicStrings.aimlif_split_char = ",";
        MagicStrings.default_bot = "super";
        MagicStrings.default_language = "EN";
        MagicStrings.aimlif_split_char_name = "\\#Comma";
        MagicStrings.aimlif_file_suffix = ".csv";
        MagicStrings.ab_sample_file = "sample.txt";
        MagicStrings.pannous_api_key = "guest";
        MagicStrings.pannous_login = "test-user";
        MagicStrings.sraix_failed = "SRAIXFAILED";
        MagicStrings.sraix_no_hint = "nohint";
        MagicStrings.sraix_event_hint = "event";
        MagicStrings.sraix_pic_hint = "pic";
        MagicStrings.unknown_aiml_file = "unknown_aiml_file.aiml";
        MagicStrings.deleted_aiml_file = "deleted.aiml";
        MagicStrings.learnf_aiml_file = "learnf.aiml";
        MagicStrings.null_aiml_file = "null.aiml";
        MagicStrings.inappropriate_aiml_file = "inappropriate.aiml";
        MagicStrings.profanity_aiml_file = "profanity.aiml";
        MagicStrings.insult_aiml_file = "insults.aiml";
        MagicStrings.reductions_update_aiml_file = "reductions_update.aiml";
        MagicStrings.predicates_aiml_file = "client_profile.aiml";
        MagicStrings.update_aiml_file = "update.aiml";
        MagicStrings.personality_aiml_file = "personality.aiml";
        MagicStrings.sraix_aiml_file = "sraix.aiml";
        MagicStrings.oob_aiml_file = "oob.aiml";
        MagicStrings.unfinished_aiml_file = "unfinished.aiml";
        MagicStrings.inappropriate_filter = "FILTER INAPPROPRIATE";
        MagicStrings.profanity_filter = "FILTER PROFANITY";
        MagicStrings.insult_filter = "FILTER INSULT";
        MagicStrings.deleted_template = "deleted";
        MagicStrings.unfinished_template = "unfinished";
        MagicStrings.unknown_history_item = "unknown";
        MagicStrings.default_bot_response = "I have no answer for that.";
        MagicStrings.error_bot_response = "Something is wrong with my brain.";
        MagicStrings.schedule_error = "I'm unable to schedule that event.";
        MagicStrings.system_failed = "Failed to execute system command.";
        MagicStrings.unknown_predicate_value = "unknown";
        MagicStrings.unknown_property_value = "unknown";
        MagicStrings.unknown_map_value = "unknown";
        MagicStrings.unknown_customer_id = "unknown";
        MagicStrings.unknown_bot_name = "unknown";
        MagicStrings.default_that = "unknown";
        MagicStrings.default_topic = "unknown";
        MagicStrings.template_failed = "Template failed.";
        MagicStrings.too_much_recursion = "Too much recursion in AIML";
        MagicStrings.too_much_looping = "Too much looping in AIML";
        MagicStrings.blank_template = "blank template";
        MagicStrings.null_input = "NORESP";
        MagicStrings.null_star = "nullstar";
        MagicStrings.set_member_string = "ISA";
        MagicStrings.remote_map_key = "external";
        MagicStrings.remote_set_key = "external";
        MagicStrings.natural_number_set_name = "number";
        MagicStrings.map_successor = "successor";
        MagicStrings.map_predecessor = "predecessor";
        MagicStrings.root_path = "c:/ab";
        MagicStrings.bot_path = MagicStrings.root_path + "/bots";
        MagicStrings.bot_name_path = MagicStrings.bot_path + "/super";
        MagicStrings.aimlif_path = MagicStrings.bot_path + "/aimlif";
        MagicStrings.aiml_path = MagicStrings.bot_path + "/aiml";
        MagicStrings.config_path = MagicStrings.bot_path + "/config";
        MagicStrings.log_path = MagicStrings.bot_path + "/log";
        MagicStrings.sets_path = MagicStrings.bot_path + "/sets";
        MagicStrings.maps_path = MagicStrings.bot_path + "/maps";
    }
}