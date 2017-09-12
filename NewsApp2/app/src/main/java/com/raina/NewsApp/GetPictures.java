package com.raina.NewsApp;

import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

class GetPictures {
    public static String getURL(String keyWord) throws Exception {
        keyWord = URLEncoder.encode(keyWord, "UTF-8");
        URL cs = new URL("https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1505199245345_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word="+keyWord);
        BufferedReader in = new BufferedReader(new InputStreamReader(cs.openStream()));
        String inputLine;
        String wholePage = "";
        while ((inputLine = in.readLine()) != null)
            wholePage += inputLine;
        in.close();
        String pattern = "(?<=\"objURL\":\")(\\s|\\S)*?(?=\")";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(wholePage);
        if (m.find()) {
            return wholePage.substring(m.start(), m.end());
        }
        return "";        
    }
}


