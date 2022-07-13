// 
// Decompiled by Procyon v0.5.36
// 

package com.hdr;

import org.apache.http.annotation.Immutable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

@Immutable
public class NoopHostnameVerifier implements HostnameVerifier {
    public static final NoopHostnameVerifier INSTANCE;

    static {
        INSTANCE = new NoopHostnameVerifier();
    }

    @Override
    public boolean verify(final String s, final SSLSession sslSession) {
        return true;
    }

    @Override
    public final String toString() {
        return "NO_OP";
    }
}
