package com.java.group41;

import java.io.*;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.content.Context;

import java.net.HttpURLConnection;
import java.net.URL;

class URLTest {

    static boolean exists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName)
                    .openConnection();
            con.setRequestMethod("HEAD");
            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) return true;
            if (code == 302 && !con.getHeaderField("Location").equals("https://baike.baidu.com/error.html")) return true;
            else return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

class News implements Serializable{
    String keyWords;
    String langType; // 新闻语言
    String newsClassTag; // 种类标记
    String newsAuthor; // 新闻作者
    String newsId; // 新闻id
    String[] newsPictures; // 相关图片URL
    String newsTime; // 新闻事件
    String newsTitle; // 标题
    String newsURL; // URL
    String[] newsVideo; // 相关视频URL
    String newsIntro; // 新闻简介
    String newsDetail;

    public static HashSet readNews = new HashSet();

    News() {
        keyWords = "";
        langType = "";
        newsClassTag = "";
        newsAuthor = "";
        newsId = "";
        newsPictures = new String[0];
        newsTime = "";
        newsTitle = "";
        newsURL = "";
        newsVideo = new String[0];
        newsIntro = "";
        newsDetail = "";
    }

    News(String langType, String newsClassTag, String newsAuthor, String newsId, String[] newsPictures, String newsTime, String newsTitle, String newsURL, String[] newsVideo, String newsIntro, String newsDetail) {
        this.langType = langType;
        this.newsClassTag = newsClassTag;
        this.newsAuthor = newsAuthor;
        this.newsId = newsId;
        this.newsPictures = newsPictures;
        this.newsTime = newsTime;
        this.newsTitle = newsTitle;
        this.newsURL = newsURL;
        this.newsVideo = newsVideo;
        this.newsIntro = newsIntro;
        this.newsDetail = newsDetail;
    }

    News(JSONObject Obj) throws Exception {
        keyWords = "";
        newsDetail = "";
        try {
            newsDetail = Obj.get("news_Content").toString();
        } catch (JSONException e) {
            newsDetail = "";
        }
        langType = Obj.get("lang_Type").toString();
        newsClassTag = Obj.get("newsClassTag").toString();
        newsAuthor = Obj.get("news_Author").toString();
        if (newsAuthor.equals("")) newsAuthor = "Unknown";
        newsId = Obj.get("news_ID").toString();
        String tmp  = Obj.get("news_Pictures").toString();
        if (tmp.equals("")) newsPictures = new String[0];
        else {
            if (tmp.indexOf(";") == -1) newsPictures = tmp.split(" ");
            else newsPictures = tmp.split(";");
        }
        newsTime = Obj.get("news_Time").toString();
        newsTitle = Obj.get("news_Title").toString();
        newsURL = Obj.get("news_URL").toString();
        tmp = Obj.get("news_Video").toString();
        if (!tmp.equals("No Match")) newsVideo = tmp.split(" ");
        newsIntro = Obj.get("news_Intro").toString();
        newsIntro = newsIntro.replaceAll(" ", "");
        newsIntro = newsIntro.replaceAll("　", "");
    }

    void show() {
        System.out.println(langType);
        System.out.println(newsClassTag);
        System.out.println(newsAuthor);
        System.out.println(newsId);
        System.out.println(newsPictures);
        System.out.println(newsTime);
        System.out.println(newsTitle);
        System.out.println(newsURL);
        System.out.println(newsVideo);
        System.out.println(newsIntro);
    }

    String getWholeNews() throws Exception {
        if (!newsDetail.equals("")) return newsDetail;
        readNews.add(newsId);
        JSONObject json = URLJSONReader.readJsonFromUrl("http://166.111.68.66:2042/news/action/query/detail?newsId="+newsId);
        String res = "";
        res = json.get("news_Content").toString();
        if (res.indexOf("\n") != -1) {
            res = res.replaceAll(" ", "");
            res = res.replaceAll("\n", "<br /><br />　　");
            if (res.charAt(0) != '　') res = "<br /><br />　　" + res;
            //res = res.replaceAll("　", "");
        } else {
            res = res.replaceAll(" ", "");
            if (res.charAt(0) != '　') res = "　　" + res;
            res = res.replaceAll("　　　　　　　　", "　　");
            res = res.replaceAll("　　　　　　", "　　");
            res = res.replaceAll("　　　　", "　　");
            res = res.replaceAll("　　", "<br /><br />　　");
        }
        JSONArray keywordsArray = new JSONArray(json.get("Keywords").toString());
        int len = 3 < keywordsArray.length() ? 3 : keywordsArray.length();
        for (int i = 0; i < len; i++) {
            JSONObject keyword = keywordsArray.getJSONObject(i);
            if (keyWords.equals("")) keyWords = keyword.get("word").toString();
            else keyWords += " " + keyword.get("word").toString();
        }
        String personsString = json.get("persons").toString();
        JSONArray personsArray = new JSONArray(personsString);
        for (int i = 0; i < personsArray.length(); i++) {
            JSONObject person = personsArray.getJSONObject(i);
            String name = person.get("word").toString();
            res = res.replaceAll(name, "<a href='https://baike.baidu.com/item/"+name+"'>"+name+"</a>");
        }
        String locationsString = json.get("locations").toString();
        JSONArray locationsArray = new JSONArray(locationsString);
        for (int i = 0; i < locationsArray.length(); i++) {
            JSONObject location = locationsArray.getJSONObject(i);
            String name = location.get("word").toString();
            res = res.replaceAll(name, "<a href='https://baike.baidu.com/item/"+name+"'>"+name+"</a>");
        }
        newsDetail = res;
        return res;
    }

    boolean checkMark() {
        if (NewsSystem.hs.add(newsId) == true) {
            NewsSystem.hs.remove(newsId);
            return false;
        } else return true;
    }

    boolean hasRead() {
        if (readNews.add(newsId) == true) {
            readNews.remove(newsId);
            return false;
        }
        else return true;
    }

    void unsave(Context context) throws Exception {
        NewsSystem.hs.remove(newsId);

        FileInputStream is;
        InputStreamReader ir;
        BufferedReader in;
        FileOutputStream fw;
        String s;
        String res = "";

        is = context.openFileInput("markList.txt");
        ir = new InputStreamReader(is);
        in = new BufferedReader(ir);

        s = in.readLine();
        in.close(); ir.close(); is.close();

        if (s != null) {
            String[] ss = s.split(" ");
            boolean check = true;
            for (int i = 0; i < ss.length; i++) {
                if (ss[i].equals(newsId)) continue;
                if (check) {
                    res = ss[i];
                    check = false;
                } else res += " " + ss[i];
            }
        }

        fw = context.openFileOutput("markList.txt", Context.MODE_PRIVATE);
        fw.write(res.getBytes());
        fw.close();

    }

    void save(Context context) throws Exception {
        NewsSystem.hs.add(newsId);

        JSONObject savingNews = new JSONObject();

        FileInputStream is;
        InputStreamReader ir;
        BufferedReader in;
        FileOutputStream fw;
        String s;

        is = context.openFileInput("markList.txt");
        ir = new InputStreamReader(is);
        in = new BufferedReader(ir);

        s = in.readLine();
        in.close(); ir.close(); is.close();

        if (s != null) {
            String[] ss = s.split(" ");
            for (int i = 0; i < ss.length; i++)
                if (ss[i].equals(newsId))
                    return;
        }

        if (s == null) s = newsId;
        else s += " " + newsId;

        fw = context.openFileOutput("markList.txt", Context.MODE_PRIVATE);
        fw.write(s.getBytes());
        fw.close();

        is = context.openFileInput("savedList.txt");
        ir = new InputStreamReader(is);
        in = new BufferedReader(ir);

        s = in.readLine();
        in.close(); ir.close(); is.close();

        if (s != null) {
            String[] ss = s.split(" ");
            for (int i = 0; i < ss.length; i++)
                if (ss[i].equals(newsId))
                    return;
        }

        if (s == null) s = newsId;
        else s += " " + newsId;

        fw = context.openFileOutput("savedList.txt", Context.MODE_PRIVATE);
        fw.write(s.getBytes());
        fw.close();

        getWholeNews();
        savingNews.put("news_Content", newsDetail);
        savingNews.put("lang_Type", langType);
        savingNews.put("newsClassTag", newsClassTag);
        savingNews.put("news_Author", newsAuthor);
        savingNews.put("news_ID", newsId);
        String tmp = "";
        for (int i = 0; i < newsPictures.length; i++)
            tmp += newsPictures[i] + " ";
        savingNews.put("news_Pictures", tmp);
        savingNews.put("news_Time", newsTime);
        savingNews.put("news_Title", newsTitle);
        savingNews.put("news_URL", newsURL);
        tmp = "";
        for (int i = 0; i < newsPictures.length; i++)
            tmp += newsPictures[i] + " ";
        if (tmp.equals("")) savingNews.put("news_Video", "No Match");
        else savingNews.put("news_Video", tmp);
        savingNews.put("news_Intro", newsIntro);

        is = context.openFileInput("contentList.txt");
        ir = new InputStreamReader(is);
        in = new BufferedReader(ir);

        s = in.readLine();
        in.close(); ir.close(); is.close();

        JSONObject contentList = new JSONObject(s);
        contentList.put(newsId, savingNews);

        fw = context.openFileOutput("contentList.txt", Context.MODE_PRIVATE);
        fw.write(contentList.toString().getBytes());
        fw.close();

    }

    String getLangType() {
        return langType;
    }

    String getNewsClassTag() {
        return newsClassTag;
    }

    String getNewsAuthor() {
        return newsAuthor;
    }

    String getNewsId() {
        return newsId;
    }

    String[] getNewsPictures() {
        return newsPictures;
    }

    String getNewsTime() {
        return newsTime;
    }

    String getNewsTitle() {
        return newsTitle;
    }

    String getNewsURL() {
        return newsURL;
    }

    String[] getNewsVideo() {
        return newsVideo;
    }

    String getNewsIntro() {
        return newsIntro;
    }

    String getKeyWords() { return keyWords; }

}
