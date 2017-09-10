package com.raina.NewsApp;

import java.util.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Raina on 03/09/2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    private int resourceId;
    public NewsAdapter(Context context, int textViewResourceId, News[] objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView newsTitle = (TextView)view.findViewById(R.id.news_title);
        TextView newsIntro = (TextView)view.findViewById(R.id.news_intro);
        //fruitImage.setImageResource(fruit.getImageId());
        String title, intro;
        try{
            title = news.getNewsTitle();
            intro = news.getNewsIntro();
        }catch(Exception e) {
            title = e.toString();
            intro = e.toString();
        }
        newsTitle.setText(title);
        newsIntro.setText(intro);
        return view;
    }
}

/*
public class NewsAdapter extends ArrayAdapter<News> {

    private int resourceId;
    public NewsAdapter(Context context, int textViewResourceId, List<News> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView newsTitle = (TextView)view.findViewById(R.id.news_title);
        TextView newsIntro = (TextView)view.findViewById(R.id.news_intro);
        //fruitImage.setImageResource(fruit.getImageId());
        newsTitle.setText(news.getTitle());
        newsIntro.setText(news.getIntro());
        return view;
    }
}
*/
