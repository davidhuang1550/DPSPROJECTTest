package com.example.david.dpsproject.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xlhuang3 on 11/8/2016.
 */
public class Searchpage extends Fragment {
    private Activity mActivity;
    private FirebaseAuth authentication;
    private View myView;
    private FirebaseUser firebaseUser;
    private SwipeRefreshLayout refreshLayout;
    private DatabaseReference dbReference;
    private ListView listView;
    private String Sub;
    private Users users;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
    }

    public void loadPost(String sub){
        dbReference.child("Sub").child(sub).child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Post> posts = new ArrayList<Post>();
                for(DataSnapshot s:dataSnapshot.getChildren()){
                    Post post = s.getValue(Post.class);
                    post.setKey(s.getKey());
                    posts.add(post);

                }
                if(posts.size()!=0){
                    Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.splashfadeoutleft);
                    MyPostAdapter adapter = new MyPostAdapter(mActivity,posts);
                    listView.startAnimation(animation);
                    listView.setAdapter(adapter);
                    if(refreshLayout!=null)refreshLayout.setRefreshing(false);

                }
                else{
                    Toast.makeText(mActivity,"Nothing was found",Toast.LENGTH_LONG).show();
                }
                ((navigation)mActivity).HideProgressDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setSubscribe(){
        users= ((navigation)mActivity).getworkingUser();
        boolean showsubscribe=false;
        if(users!=null){
            for(String s:users.getSubcategory()){
                if(s.equals(Sub)){
                    ((navigation)mActivity).showUnsubscribe();
                    showsubscribe=true;
                    break;
                }
            }
            if(!showsubscribe)((navigation)mActivity).showSubscribe();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.searchpage,container,false);
        authentication= FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        firebaseUser = authentication.getCurrentUser();
        listView = (ListView)myView.findViewById(R.id.listView);
        final Bundle b = getArguments();
        if(b!=null) {
            ((navigation) mActivity).ShowProgressDialog();
            ((navigation)mActivity).setSubCat(b.getString("Sub"));
            Sub = b.getString("Sub");
            mActivity.setTitle(Sub);
            final ProgressDialog mprogressDialog = ProgressDialog.show(mActivity, "Please wait","Loading Posts", true);
            new Thread(){
                @Override
                public void run() {
                    loadPost(b.getString("Sub"));

                    mActivity.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            setSubscribe();
                            mprogressDialog.dismiss();
                        }
                    });

                }
            }.start();


        }

        refreshLayout = (SwipeRefreshLayout)myView.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (listView != null){
                    final Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.splashfadeout);
                    listView.startAnimation(animation);
                    MyPostAdapter tempAdapter= (MyPostAdapter)listView.getAdapter();
                    tempAdapter.clearData();
                    tempAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(true);
                    loadPost(b.getString("Sub"));
                }
                else{
                    refreshLayout.setRefreshing(true);
                    loadPost(b.getString("Sub"));
                }


            }
        });

        return myView;
    }
    public void onDestroy() {
        ViewGroup container = (ViewGroup)mActivity.findViewById(R.id.content_frame);
        container.removeAllViews();
        super.onDestroy();
    }

}
