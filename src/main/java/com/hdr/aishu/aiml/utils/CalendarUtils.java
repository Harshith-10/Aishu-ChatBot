// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr.aishu.aiml.utils;

import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarUtils
{
    public static int timeZoneOffset() {
        final Calendar cal = Calendar.getInstance();
        final int offset = (cal.get(15) + cal.get(16)) / 60000;
        return offset;
    }
    
    public static String year() {
        final Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(1));
    }
    
    public static String date() {
        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(1);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMMMMMM dd, yyyy");
        dateFormat.setCalendar(cal);
        return dateFormat.format(cal.getTime());
    }
    
    public static String date(String jformat, String locale, String timezone) {
        if (jformat == null) {
            jformat = "EEE MMM dd HH:mm:ss zzz yyyy";
        }
        if (locale == null) {
            locale = Locale.US.getISO3Country();
        }
        if (timezone == null) {
            timezone = TimeZone.getDefault().getDisplayName();
        }
        String dateAsString = new Date().toString();
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(jformat);
            dateAsString = simpleDateFormat.format(new Date());
        }
        catch (Exception ex) {
            System.out.println("CalendarUtils.date Bad date: Format = " + jformat + " Locale = " + locale + " Timezone = " + timezone);
        }
        System.out.println("CalendarUtils.date: " + dateAsString);
        return dateAsString;
    }
}
