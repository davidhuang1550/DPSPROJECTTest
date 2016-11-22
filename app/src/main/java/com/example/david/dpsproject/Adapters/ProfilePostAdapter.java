package com.example.david.dpsproject.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.david.dpsproject.Class.Post;

import java.util.ArrayList;

/**
 * Created by david on 2016-11-16.
 */
public class ProfilePostAdapter extends BaseAdapter {
    private ArrayList<Post> posts;
    private Context context;

    ProfilePostAdapter(Context activity, ArrayList<Post> p){
        posts=p;
        context=activity;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
