package com.java.group41;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pilgrims on 13/09/2017.
 */

public class BlockAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<String> items = new ArrayList<String>();
    public BlockAdapter(Context context, List<String> arrayList) {
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
        String s = (String) getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.block_item_layout, parent, false);

            viewHolder.blockKey = (TextView) convertView.findViewById(R.id.block_key);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.blockKey.setText(s);
        return convertView;
    }

    public void onDateChange(List<String> items) {
        this.items = items;
        this.notifyDataSetChanged();
    }

    public class ViewHolder {
        TextView blockKey;
    }

}
