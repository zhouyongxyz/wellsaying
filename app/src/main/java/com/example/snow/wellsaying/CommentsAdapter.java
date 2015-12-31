package com.example.snow.wellsaying;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by zhouyong on 12/31/15.
 */
public class CommentsAdapter extends BaseAdapter {
    private final static String TAG = "CommentsAdapter";
    private Context mContext;
    private List<Map<String,Object>> mDataList;
    private LayoutInflater mLayoutInflater;
    public CommentsAdapter(Context context,List<Map<String,Object>> data) {
        mContext = context;
        mDataList = data;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentItem item = null;
        if(convertView == null) {
            item = new CommentItem();
            convertView = mLayoutInflater.inflate(R.layout.list_comment_item,null);
            item.title = (TextView)convertView.findViewById(R.id.title);
            item.content = (TextView)convertView.findViewById(R.id.content);
            convertView.setTag(item);
        } else {
            item = (CommentItem)convertView.getTag();
        }
        Log.d(TAG,"getView position = "+position);
        item.title.setText((String)mDataList.get(position).get("title"));
        item.content.setText((String)mDataList.get(position).get("content"));
        return convertView;
    }
}
