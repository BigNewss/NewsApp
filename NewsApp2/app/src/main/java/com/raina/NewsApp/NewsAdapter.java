package com.raina.NewsApp;

import java.util.*;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Raina on 03/09/2017.
 */
public class NewsAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private Context context;
    private List<News> items = new ArrayList<News>();

    public NewsAdapter(Context context, List<News> arrayList) {
        this.context = context;
        this.items = arrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News news = (News) getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.news_item_layout, parent, false);


            viewHolder.newsIntro = (TextView) convertView.findViewById(R.id.news_intro);
            viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(news.hasRead())
            viewHolder.newsTitle.setTextColor(convertView.getResources().getColor(R.color.colorSeen));
        else
            viewHolder.newsTitle.setTextColor(convertView.getResources().getColor(R.color.title_color));

        viewHolder.newsIntro.setText(items.get(position).getNewsIntro());
        viewHolder.newsTitle.setText(items.get(position).getNewsTitle());

        //viewHolder.newsTitle.setTypeface(MainActivity.titleTypeface);
        //viewHolder.newsIntro.setTypeface(MainActivity.introTypeface);
        return convertView;
    }


    public void onDateChange(List<News> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    public class ViewHolder {

        TextView newsIntro;
        TextView newsTitle;
    }

}
//public class NewsAdapter extends ArrayAdapter<News> {
//
//    private int resourceId;
//    public NewsAdapter(Context context, int textViewResourceId, z) {
//        super(context, textViewResourceId, objects);
//        resourceId = textViewResourceId;
//    }
//
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        News news = getItem(position);
//        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
//        TextView newsTitle = (TextView)view.findViewById(R.id.news_title);
//        TextView newsIntro = (TextView)view.findViewById(R.id.news_intro);
//        //fruitImage.setImageResource(fruit.getImageId());
//        String title, intro;
//        try{
//            title = news.getNewsTitle();
//            intro = news.getNewsIntro();
//        }catch(Exception e) {
//            title = e.toString();
//            intro = e.toString();
//        }
//        newsTitle.setText(title);
//        newsIntro.setText(intro);
//        return view;
//    }
//}

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