package com.raina.NewsApp;

import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

class GetPictures {
    public static String getURL(String keyWord) throws Exception {
        keyWord = URLEncoder.encode(keyWord, "UTF-8");
        URL cs = new URL("http://image.baidu.com/search/index?tn=baiduimage&ps=1&ct=201326592&lm=-1&cl=2&nc=1&ie=utf-8&word="+keyWord);
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


