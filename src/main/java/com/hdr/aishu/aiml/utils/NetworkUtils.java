package com.hdr.aishu.aiml.utils;

import java.net.URLEncoder;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.impl.client.HttpClients;
import java.util.Enumeration;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class NetworkUtils
{
    public static String localIPAddress() {
        try {
            final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                final Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    final InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String str = inetAddress.getHostAddress().toString();
                        final int index = str.indexOf("%");
                        if (index > 0) {
                            str = str.substring(0, index);
                        }
                        System.out.println("--> localIPAddress = " + str);
                        return str;
                    }
                }
            }
        }
        catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "127.0.0.1";
    }
    
    public static String responseContent(final String str) throws Exception {
        final CloseableHttpClient build = HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(), new AllowAllHostnameVerifier())).build();
        final HttpGet httpGet = new HttpGet();
        httpGet.setURI(new URI(str));
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(build.execute(httpGet).getEntity().getContent()));
        final StringBuilder sb = new StringBuilder("");
        final String property = System.getProperty("line.separator");
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append(property);
        }
        bufferedReader.close();
        return sb.toString();
    }
    
    public static String spec(final String s, final String s2, final String s3, final String s4) {
        String s5;
        if (s3.equals("0")) {
            s5 = String.format("%s?botid=%s&input=%s", "http://" + s + "/pandora/talk-xml", s2, URLEncoder.encode(s4));
        }
        else {
            s5 = String.format("%s?botid=%s&custid=%s&input=%s", "http://" + s + "/pandora/talk-xml", s2, s3, URLEncoder.encode(s4));
        }
        return s5;
    }
}
