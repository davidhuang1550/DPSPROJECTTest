package com.example.david.dpsproject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by david on 2016-11-03.
 */
public class MyPostAdapter extends BaseAdapter {
    private ArrayList<Post> posts;
    private Context context;
    MyPostAdapter(Activity Activity, ArrayList<Post> p){
        posts=p;
        context=Activity;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View row;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.postlist,null);
        TextView tView = (TextView)row.findViewById(R.id.PostT);
        TextView posterId = (TextView)row.findViewById(R.id.posterId);
        tView.setText(posts.get(i).getTitle());
        posterId.setText(posts.get(i).getPosterId());
        row.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();

                FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
                Fragment myFragment = fragmentManager.findFragmentByTag("search");



                if(myFragment==null){
                    postview pV = new postview();
                    bundle.putSerializable("Post_Object", (Serializable) posts.get(i));
                    bundle.putString("UID",posts.get(i).getPosterId());
                    pV.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.content_frame, pV).commit();
               }
            }
        });
        return row;

    }
}
