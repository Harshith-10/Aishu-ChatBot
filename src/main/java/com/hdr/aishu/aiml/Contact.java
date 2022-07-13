// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml;

import java.util.regex.Matcher;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.HashMap;

public class Contact
{
    public static int contactCount;
    public static HashMap<String, Contact> idContactMap;
    public static HashMap<String, String> nameIdMap;
    public String contactId;
    public String displayName;
    public String birthday;
    public HashMap<String, String> phones;
    public HashMap<String, String> emails;
    
    public static String multipleIds(final String contactName) {
        String patternString;
        for (patternString = " (" + contactName.toUpperCase() + ") "; patternString.contains(" "); patternString = patternString.replace(" ", "(.*)")) {}
        final Pattern pattern = Pattern.compile(patternString);
        final Set<String> keys = Contact.nameIdMap.keySet();
        String result = "";
        int idCount = 0;
        for (final String key : keys) {
            final Matcher m = pattern.matcher(key);
            if (m.find()) {
                result = result + Contact.nameIdMap.get(key.toUpperCase()) + " ";
                ++idCount;
            }
        }
        if (idCount <= 1) {
            result = "false";
        }
        return result.trim();
    }
    
    public static String contactId(final String contactName) {
        String patternString;
        for (patternString = " " + contactName.toUpperCase() + " "; patternString.contains(" "); patternString = patternString.replace(" ", ".*")) {}
        final Pattern pattern = Pattern.compile(patternString);
        final Set<String> keys = Contact.nameIdMap.keySet();
        String result = "unknown";
        for (final String key : keys) {
            final Matcher m = pattern.matcher(key);
            if (m.find()) {
                result = Contact.nameIdMap.get(key.toUpperCase()) + " ";
            }
        }
        return result.trim();
    }
    
    public static String displayName(final String id) {
        final Contact c = Contact.idContactMap.get(id.toUpperCase());
        String result = "unknown";
        if (c != null) {
            result = c.displayName;
        }
        return result;
    }
    
    public static String dialNumber(final String type, final String id) {
        String result = "unknown";
        final Contact c = Contact.idContactMap.get(id.toUpperCase());
        if (c != null) {
            final String dialNumber = c.phones.get(type.toUpperCase());
            if (dialNumber != null) {
                result = dialNumber;
            }
        }
        return result;
    }
    
    public static String emailAddress(final String type, final String id) {
        String result = "unknown";
        final Contact c = Contact.idContactMap.get(id.toUpperCase());
        if (c != null) {
            final String emailAddress = c.emails.get(type.toUpperCase());
            if (emailAddress != null) {
                result = emailAddress;
            }
        }
        return result;
    }
    
    public static String birthday(final String id) {
        final Contact c = Contact.idContactMap.get(id.toUpperCase());
        if (c == null) {
            return "unknown";
        }
        return c.birthday;
    }
    
    public Contact(final String displayName, final String phoneType, final String dialNumber, final String emailType, final String emailAddress, final String birthday) {
        this.contactId = "ID" + Contact.contactCount;
        ++Contact.contactCount;
        this.phones = new HashMap<String, String>();
        this.emails = new HashMap<String, String>();
        Contact.idContactMap.put(this.contactId.toUpperCase(), this);
        this.addPhone(phoneType, dialNumber);
        this.addEmail(emailType, emailAddress);
        this.addName(displayName);
        this.addBirthday(birthday);
    }
    
    public void addPhone(final String type, final String dialNumber) {
        this.phones.put(type.toUpperCase(), dialNumber);
    }
    
    public void addEmail(final String type, final String emailAddress) {
        this.emails.put(type.toUpperCase(), emailAddress);
    }
    
    public void addName(final String name) {
        this.displayName = name;
        Contact.nameIdMap.put(this.displayName.toUpperCase(), this.contactId);
    }
    
    public void addBirthday(final String birthday) {
        this.birthday = birthday;
    }
    
    static {
        Contact.contactCount = 0;
        Contact.idContactMap = new HashMap<String, Contact>();
        Contact.nameIdMap = new HashMap<String, String>();
    }
}
