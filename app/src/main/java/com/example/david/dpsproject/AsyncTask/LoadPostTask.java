package com.example.david.dpsproject.AsyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.david.dpsproject.Adapters.MyPostAdapter;
import com.example.david.dpsproject.Class.Post;
import com.example.david.dpsproject.Class.Users;
import com.example.david.dpsproject.R;
import com.example.david.dpsproject.navigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by david on 2016-11-30.
 */
public class LoadPostTask extends AsyncTask<Void,Void,Void> {

    private Activity mActivity;
    private Users user;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private ArrayList<String> category;
    private ArrayList<Post> posts;
    private View myView;
    private MyPostAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private long old_time_diff;
    private long time_diff;

    public LoadPostTask(Activity activity,Users u,DatabaseReference dbf,FirebaseAuth fba,FirebaseUser fbu,ArrayList<String> cat,View view,SwipeRefreshLayout l){
        mActivity=activity;
        user=u;
        databaseReference=dbf;
        firebaseAuth=fba;
        firebaseUser=fbu;
        category=cat;
        myView=view;
        refreshLayout=l;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        posts= new ArrayList<Post>();
        try {
            time_diff = (System.currentTimeMillis() / 1000);
            old_time_diff=time_diff;
            boolean first_iteration= false;
            posts.clear();
            do {
                user=((navigation)mActivity).getworkingUser();
                if(user!=null) {
                    ArrayList<String> SubCategory = user.getSubcategory();
                    int limit=15/SubCategory.size();
                    time_diff -=  86400;
                    if(!first_iteration) {
                        for (int i = 0; i < SubCategory.size(); i++) {
                            databaseReference.child("Sub").child(SubCategory.get(i)).child("posts").orderByChild("timestamp").startAt(time_diff).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                                            Post post = s.getValue(Post.class);
                                            post.setKey(s.getKey());
                                            posts.add(post);
                                        }
                                    } catch (DatabaseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    ((navigation)mActivity).HideProgressDialog();
                                    Toast.makeText(mActivity,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        first_iteration = true;
                        old_time_diff=time_diff;
                    }
                    else{
                        for (int i = 0; i < SubCategory.size(); i++) {
                            // Sub=SubCategory.get(i);
                            databaseReference.child("Sub").child(SubCategory.get(i)).child("posts").orderByChild("timestamp").startAt(time_diff).endAt(old_time_diff).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                                            Post post = s.getValue(Post.class);
                                            post.setKey(s.getKey());
                                            posts.add(post);
                                        }
                                    } catch (DatabaseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    ((navigation)mActivity).HideProgressDialog();
                                    Toast.makeText(mActivity,"Please Check Internet Connection",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        old_time_diff=time_diff;
                    }
                }
                Thread.sleep(1000);
            } while (posts.size()<10);

        }catch (InterruptedException e){
            e.printStackTrace();
        }

        return null;
    }
    protected void onPostExecute(Void aVoid) {
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.splashfadeoutleft);
        Collections.shuffle(posts);
        ListView listView = (ListView)myView.findViewById(R.id.postview);
        adapter = new MyPostAdapter(mActivity,posts);
        listView.startAnimation(animation);
        listView.setAdapter(adapter);
        if(refreshLayout!=null)refreshLayout.setRefreshing(false);
        ((navigation)mActivity).HideProgressDialog();
    }

}
