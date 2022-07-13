package com.hdr.aishu.aiml;

import javax.net.ssl.SSLSession;
import javax.net.ssl.HttpsURLConnection;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;

public class WhitelistHostnameVerifier implements HostnameVerifier {
    private Set whitelist;
    private HostnameVerifier defaultHostnameVerifier;
    
    WhitelistHostnameVerifier(final String... array) {
        this.whitelist = new HashSet();
        this.defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        for (int length = array.length, i = 0; i < length; ++i) {
            this.whitelist.add(array[i]);
        }
    }
    
    @Override
    public boolean verify(final String s, final SSLSession sslSession) {
        return true;
    }
}
