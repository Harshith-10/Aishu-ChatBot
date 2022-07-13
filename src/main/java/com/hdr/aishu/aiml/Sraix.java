package com.hdr.aishu.aiml;

import java.util.regex.Matcher;
import org.json.JSONArray;
import java.util.regex.Pattern;
import org.json.JSONObject;

import com.hdr.aishu.aiml.utils.CalendarUtils;
import com.hdr.aishu.aiml.utils.NetworkUtils;

import java.util.HashMap;

public class Sraix {
    public static HashMap<String, String> custIdMap;
    private static String custid;
    
    public static final String wikipediaLogo = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Wikipedia_logo_v3.svg/1200px-Wikipedia_logo_v3.svg.png";
    
    public static String sraix(final Chat chatSession, final String input, final String defaultResponse, final String hint, final String host, final String botid, final String apiKey, final String limit) {
        String response;
        if (host != null && botid != null) {
            response = sraixPandorabots(input, chatSession, host, botid);
        }
        else {
            response = sraixPannous(input, hint, chatSession);
        }
        if (response.equals(MagicStrings.sraix_failed)) {
            if (chatSession != null && defaultResponse == null) {
                response = AIMLProcessor.respond(MagicStrings.sraix_failed, "nothing", "nothing", chatSession);
            }
            else if (defaultResponse != null) {
                response = defaultResponse;
            }
        }
        return response;
    }
    
    public static String sraixPandorabots(final String input, final Chat chatSession, final String host, final String botid) {
        final String responseContent = pandorabotsRequest(input, host, botid);
        if (responseContent == null) {
            return MagicStrings.sraix_failed;
        }
        return pandorabotsResponse(responseContent, chatSession, host, botid);
    }
    
    public static String pandorabotsRequest(final String input, final String host, final String botid) {
        try {
            Sraix.custid = "0";
            final String key = host + ":" + botid;
            if (Sraix.custIdMap.containsKey(key)) {
                Sraix.custid = Sraix.custIdMap.get(key);
            }
            final String spec = NetworkUtils.spec(host, botid, Sraix.custid, input);
            final String responseContent = NetworkUtils.responseContent(spec);
            return responseContent;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static String pandorabotsResponse(final String sraixResponse, final Chat chatSession, final String host, final String botid) {
        int n1 = sraixResponse.indexOf("<that>");
        int n2 = sraixResponse.indexOf("</that>");
        String botResponse = MagicStrings.sraix_failed;
        if (n2 > n1) {
            botResponse = sraixResponse.substring(n1 + "<that>".length(), n2);
        }
        n1 = sraixResponse.indexOf("custid=");
        if (n1 > 0) {
            Sraix.custid = sraixResponse.substring(n1 + "custid=\"".length(), sraixResponse.length());
            n2 = Sraix.custid.indexOf("\"");
            if (n2 > 0) {
                Sraix.custid = Sraix.custid.substring(0, n2);
            }
            else {
                Sraix.custid = "0";
            }
            final String key = host + ":" + botid;
            Sraix.custIdMap.put(key, Sraix.custid);
        }
        if (botResponse.endsWith(".")) {
            botResponse = botResponse.substring(0, botResponse.length() - 1);
        }
        return botResponse;
    }
    
    public static String sraixPannous(String input, String hint, final Chat chatSession) {
        try {
            if (hint == null) {
                hint = MagicStrings.sraix_no_hint;
            }
            input = " " + input + " ";
            input = input.replace(" point ", ".");
            input = input.replace(" rparen ", ")");
            input = input.replace(" lparen ", "(");
            input = input.replace(" slash ", "/");
            input = input.replace(" star ", "*");
            input = input.replace(" dash ", "-");
            input = input.trim();
            input = input.replace(" ", "+");
            final int offset = CalendarUtils.timeZoneOffset();
            String locationString = "";
            if (Chat.locationKnown) {
                locationString = "&location=" + Chat.latitude + "," + Chat.longitude;
            }
            final String url = "https://weannie.pannous.com/api?input=" + input + "&locale=en_US&timeZone=" + offset + locationString + "&login=" + MagicStrings.pannous_login + "&ip=" + NetworkUtils.localIPAddress() + "&botid=0&key=" + MagicStrings.pannous_api_key + "&exclude=Dialogues,ChatBot&out=json";
            if (MagicBooleans.trace_mode) {
                System.out.println("Sraix url='" + url + "'");
            }
            final String page = NetworkUtils.responseContent(url);
            if (MagicBooleans.trace_mode) {
                System.out.println("Sraix: " + page);
            }
            String text = "";
            String imgRef = "";
            if (page == null || page.length() == 0) {
                text = MagicStrings.sraix_failed;
            } else {
                final JSONArray outputJson = new JSONObject(page).getJSONArray("output");
                if (outputJson.length() == 0) {
                    text = MagicStrings.sraix_failed;
                } else {
                    final JSONObject firstHandler = outputJson.getJSONObject(0);
                    final JSONObject actions = firstHandler.getJSONObject("actions");
                    if (actions.has("reminder")) {
                        final Object obj = actions.get("reminder");
                        if (obj instanceof JSONObject) {
                            final JSONObject sObj = (JSONObject)obj;
                            String date = sObj.getString("date");
                            date = date.substring(0, "2012-10-24T14:32".length());
                            final String duration = sObj.getString("duration");
                            final Pattern datePattern = Pattern.compile("(.*)-(.*)-(.*)T(.*):(.*)");
                            final Matcher m = datePattern.matcher(date);
                            String year = "";
                            String month = "";
                            String day = "";
                            String hour = "";
                            String minute = "";
                            if (m.matches()) {
                                year = m.group(1);
                                month = String.valueOf(Integer.parseInt(m.group(2)) - 1);
                                day = m.group(3);
                                hour = m.group(4);
                                minute = m.group(5);
                                text = "<year>" + year + "</year>" + "<month>" + month + "</month>" + "<day>" + day + "</day>" + "<hour>" + hour + "</hour>" + "<minute>" + minute + "</minute>" + "<duration>" + duration + "</duration>";
                            } else {
                                text = MagicStrings.schedule_error;
                            }
                        }
                    } else if (actions.has("say") && !hint.equals(MagicStrings.sraix_pic_hint)) {
                        final Object obj = actions.get("say");
                        if (obj instanceof JSONObject) {
                            final JSONObject sObj = (JSONObject)obj;
                            text = sObj.getString("text");
                            if (sObj.has("moreText")) {
                                final JSONArray arr = sObj.getJSONArray("moreText");
                                for (int i = 0; i < arr.length(); ++i) {
                                    text = text + " " + arr.getString(i);
                                }
                            }
                        } else {
                            text = obj.toString();
                        }
                    }
                    if (actions.has("show") && !text.contains("Wolfram") && actions.getJSONObject("show").has("images")) {
                        final JSONArray arr2 = actions.getJSONObject("show").getJSONArray("images");
                        final int j = (int)(arr2.length() * Math.random());
                        imgRef = arr2.getString(j);
                        if (imgRef.startsWith("//")) {
                            imgRef = "http:" + imgRef;
                        }
                        imgRef = "<a href=\"" + imgRef + "\"><img src=\"" + imgRef + "\"/></a>";
                    }
                }
                if (hint.equals(MagicStrings.sraix_event_hint) && !text.startsWith("<year>")) {
                    return MagicStrings.sraix_failed;
                }
                if (text.equals(MagicStrings.sraix_failed)) {
                    return AIMLProcessor.respond(MagicStrings.sraix_failed, "nothing", "nothing", chatSession);
                }
                text = text.replace("&#39;", "'");
                text = text.replace("&apos;", "'");
                text = text.replaceAll("\\[(.*)\\]", "");
                final String[] sentences = text.split("\\. ");
                String clippedPage = sentences[0];
                for (int k = 1; k < sentences.length; ++k) {
                    if (clippedPage.length() < 500) {
                        clippedPage = clippedPage + ". " + sentences[k];
                    }
                }
                clippedPage = clippedPage + " " + imgRef;
                return clippedPage;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Sraix '" + input + "' failed");
        }
        return MagicStrings.sraix_failed;
    }
    
    static {
        Sraix.custIdMap = new HashMap<String, String>();
        Sraix.custid = "0";
    }
}
