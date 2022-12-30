package com.github.catvod.net;

import com.github.catvod.crawler.Spider;
import com.github.catvod.spider.Init;
import com.google.net.cronet.okhttptransport.CronetInterceptor;

import org.chromium.net.CronetEngine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;
import okhttp3.OkHttpClient;

public class Cronet {

    private final OkHttpClient noRedirect;
    private final OkHttpClient client;

    private static class Loader {
        static volatile Cronet INSTANCE = new Cronet();
    }

    public static Cronet get() {
        return Loader.INSTANCE;
    }

    public Cronet() {
        client = getBuilder().build();
        noRedirect = client.newBuilder().followRedirects(false).followSslRedirects(false).build();
    }

    private OkHttpClient.Builder getBuilder() {
        return new OkHttpClient.Builder().dns(safeDns()).addInterceptor(CronetInterceptor.newBuilder(new CronetEngine.Builder(Init.context()).build()).build()).callTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).retryOnConnectionFailure(true).hostnameVerifier(SSLSocketFactoryCompat.hostnameVerifier).sslSocketFactory(new SSLSocketFactoryCompat(), SSLSocketFactoryCompat.trustAllCert);
    }

    private static OkHttpClient client() {
        return get().client;
    }

    private static OkHttpClient noRedirect() {
        return get().noRedirect;
    }

    public static Dns safeDns() {
        try {
            return (Dns) Spider.class.getMethod("safeDns").invoke(null);
        } catch (Exception e) {
            return Dns.SYSTEM;
        }
    }

    public static void stringNoRedirect(String url, Map<String, String> header, Map<String, List<String>> respHeader) {
        string(noRedirect(), OkHttp.GET, url, null, null, header, respHeader);
    }

    public static String string(OkHttpClient client, String method, String url, String tag, Map<String, String> params, Map<String, String> header, Map<String, List<String>> respHeader) {
        return new OkRequest(method, url, params, header, respHeader).tag(tag).execute(client);
    }

    public static String string(String url) {
        return string(url, null);
    }

    public static String string(String url, Map<String, String> header) {
        return string(url, header, null);
    }

    public static String string(String url, Map<String, String> header, Map<String, List<String>> respHeader) {
        return string(url, null, header, respHeader);
    }

    public static String string(String url, Map<String, String> params, Map<String, String> header, Map<String, List<String>> respHeader) {
        return string(url, null, params, header, respHeader);
    }

    public static String string(String url, String tag, Map<String, String> header) {
        return string(url, tag, null, header, null);
    }

    public static String string(String url, String tag, Map<String, String> params, Map<String, String> header, Map<String, List<String>> respHeader) {
        return string(client(), OkHttp.GET, url, tag, params, header, respHeader);
    }

    public static String post(String url, Map<String, String> params) {
        return post(url, params, null);
    }

    public static String post(String url, Map<String, String> params, Map<String, String> header) {
        return post(url, params, header, null);
    }

    public static String post(String url, Map<String, String> params, Map<String, String> header, Map<String, List<String>> respHeader) {
        return string(client(), OkHttp.POST, url, null, params, header, respHeader);
    }

    public static String postJson(String url, String json) {
        return postJson(url, json, null);
    }

    public static String postJson(String url, String json, Map<String, String> header) {
        return new OkRequest(OkHttp.POST, url, json, header).execute(client());
    }

    public static String getRedirectLocation(Map<String, List<String>> headers) {
        if (headers == null) return null;
        if (headers.containsKey("location")) return headers.get("location").get(0);
        if (headers.containsKey("Location")) return headers.get("Location").get(0);
        return null;
    }
}
