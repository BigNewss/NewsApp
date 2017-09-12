package com.raina.NewsApp;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;

class NewsSystem {

    //private static Context context;
    public static HashSet hs = new HashSet();

    ArrayList<News> res;
    News[] latestNewsList;
    News[] searchNewsList;
    News[][] categoryNewsList;
    int[] categoryPageNo;
    int latestPageNo;
    int searchPageNo;
    int pageSize;
    String[] keys;
    News[] markNewsList;

    void updateMarkNewsList(Context context) throws Exception {

        FileInputStream is;
        InputStreamReader ir;
        BufferedReader in;
        FileWriter fw;
        String s;
        String res = "";

        is = context.openFileInput("markList.txt");
        ir = new InputStreamReader(is);
        in = new BufferedReader(ir);

        s = in.readLine();
        in.close(); ir.close(); is.close();

        if (s != null) {
            String[] ss = s.split(" ");
            markNewsList = new News[ss.length];

            is = context.openFileInput("contentList.txt");
            ir = new InputStreamReader(is);
            in = new BufferedReader(ir);

            s = in.readLine();
            in.close(); ir.close(); is.close();

            JSONObject con = new JSONObject(s);
            for (int i = 0; i < ss.length; i++) {
                JSONObject obj = new JSONObject(con.get(ss[i]).toString());
                hs.add(obj.get("news_ID").toString());
                markNewsList[i] = new News(obj);
            }
        }

    }

    News[] getMarkNewsList() {
        return markNewsList;
    }

    NewsSystem() throws Exception {

        markNewsList = new News[0];
        pageSize = 20;
        latestNewsList = new News[0];
        latestPageNo = 0;
        categoryNewsList = new News[12][];
        for (int i = 0; i < 12; i++) categoryNewsList[i] = new News[0];
        categoryPageNo = new int[12];
        for (int i = 0; i < 12; i++) categoryPageNo[i] = 0;
        searchPageNo = 0;
        searchNewsList = new News[0];
    }

    void searchInit(String[] keys) {
        searchPageNo = 0;
        searchNewsList = new News[0];
        this.keys = keys;
    }

    void searchInit(String stringKeys) {
        searchPageNo = 0;
        searchNewsList = new News[0];
        keys = stringKeys.split(" ");
    }

    void searchNews() throws Exception {
        int currentCnt = searchNewsList.length;
        searchPageNo++;
        String keyString = "";
        if (keys.length > 0) {
            keyString = keys[0];
            for (int i = 1; i < keys.length; i++)
                keyString += ','+keys[i];
        }
        JSONObject json = URLJSONReader.readJsonFromUrl("http://166.111.68.66:2042/news/action/query/search?pageNo="+searchPageNo+"&pageSize="+pageSize+"&keyword="+keyString);
        JSONArray jsonArray = new JSONArray(json.get("list").toString());
        int delta = jsonArray.length();
        int newCnt = currentCnt + delta;
        News[] tmpNewsList = new News[newCnt];
        for (int i = 0; i < currentCnt; i++) tmpNewsList[i] = searchNewsList[i];
        for (int i = 0; i < delta; i++)
            tmpNewsList[currentCnt++] = new News(jsonArray.getJSONObject(i));
        searchNewsList = tmpNewsList;
    }

    ArrayList<News> getSearchNewsList() {
        res = new ArrayList<News>();
        for (int i = 0; i < searchNewsList.length; i++)
            res.add(searchNewsList[i]);
        return res;
    }

    void getCategoryNews(int type) throws Exception {
        int currentCnt = categoryNewsList[type].length;
        categoryPageNo[type]++;
        JSONObject json = URLJSONReader.readJsonFromUrl("http://166.111.68.66:2042/news/action/query/latest?pageNo="+categoryPageNo[type]+"&pageSize="+pageSize+"&category="+(type+1));
        JSONArray jsonArray = new JSONArray(json.get("list").toString());
        int delta = jsonArray.length();
        int newCnt = currentCnt + delta;
        News[] tmpNewsList = new News[newCnt];
        for (int i = 0; i < currentCnt; i++) tmpNewsList[i] = categoryNewsList[type][i];
        for (int i = 0; i < delta; i++)
            tmpNewsList[currentCnt++] = new News(jsonArray.getJSONObject(i));
        categoryNewsList[type] = tmpNewsList;
    }

    ArrayList<News> getCategoryNewsList(int type) throws Exception {
        res = new ArrayList<News>();
        for (int i = 0; i < categoryNewsList[type].length; i++)
            res.add(categoryNewsList[type][i]);
        return res;
    }

    void getLatestNews() throws Exception {
        int currentCnt = latestNewsList.length;
        latestPageNo++;
        JSONObject json = URLJSONReader.readJsonFromUrl("http://166.111.68.66:2042/news/action/query/latest?pageNo="+latestPageNo+"&pageSize="+pageSize);
        JSONArray jsonArray = new JSONArray(json.get("list").toString());
        int delta = jsonArray.length();
        int newCnt = currentCnt + delta;
        News[] tmpNewsList = new News[newCnt];
        for (int i = 0; i < currentCnt; i++) tmpNewsList[i] = latestNewsList[i];
        for (int i = 0; i < delta; i++)
            tmpNewsList[currentCnt++] = new News(jsonArray.getJSONObject(i));
        latestNewsList = tmpNewsList;
    }


    ArrayList<News> getLatestNewsList() {
        res = new ArrayList<News>();
        for (int i = 0; i < latestNewsList.length; i++)
            res.add(latestNewsList[i]);
        return res;
    }

}
