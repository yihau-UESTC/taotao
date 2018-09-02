package com.taotao.scanner;

import java.util.concurrent.ConcurrentHashMap;

public class HtmlsHolder {
    private static ConcurrentHashMap<Long, String> htmls = new ConcurrentHashMap<>();

    public static void addHtml(long id, String name){
        String put = htmls.put(id, name);
    }

    public static void deleteHtml(long id){
        htmls.remove(id);
    }

    public static String getHtml(long id){
        return htmls.get(id);
    }
}
