package com.example.david.dpsproject.AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.david.dpsproject.Adapters.MyPostAdapter;
import com.example.david.dpsproject.Class.Post;
import com.example.david.dpsproject.Class.Profile;
import com.example.david.dpsproject.R;
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
 * Created by david on 2016-11-23.
 */
public class ProfileTask extends AsyncTask<Void,Void,Void>{
    private String sub_book_post;
    private String profile_book_post;
    private View myView;
    private Activity mActivity;
    private ArrayList<Post> ptemp;
    private ListView listView;

    private FirebaseAuth authentication;
    private DatabaseReference dbReference;
    private FirebaseUser firebaseUser;

    private ProgressDialog pDialog;
    public ProfileTask(String sub_posts,String profile_post, View v,Activity activity,FirebaseAuth auth,DatabaseReference dbRef,FirebaseUser firebaseU){
        sub_book_post=sub_posts;
        profile_book_post=profile_post;
        myView=v;
        mActivity=activity;

        ptemp=new ArrayList<Post>();

        authentication=auth;
        dbReference=dbRef;
        firebaseUser=firebaseU;

    }
    public void ShowProgressDialog() { // progress
        if (pDialog == null) {
            pDialog = new ProgressDialog(mActivity);
            pDialog.setMessage("Loading Posts");
            pDialog.setIndeterminate(true);
        }
        pDialog.show();
    }
    public void HideProgressDialog() {
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

    @Override
    protected void onPreExecute() {
        ShowProgressDialog();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try{
            do {

                dbReference.child("Users").child(firebaseUser.getUid()).child(profile_book_post).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot s : dataSnapshot.getChildren()) {

                            for (DataSnapshot temp : s.getChildren()) {
                                dbReference.child("Sub").child(s.getKey()).child(sub_book_post).child(temp.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            Post post = dataSnapshot.getValue(Post.class);
                                            if (post != null) {
                                                post.setKey(dataSnapshot.getKey());
                                                ptemp.add(post);
                                            }

                                        } catch (DatabaseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                Thread.sleep(1000);
            }while(ptemp.size() == 0);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        HideProgressDialog();
        //Collections.shuffle(ptemp);
        listView = (ListView) myView.findViewById(R.id.profile_list_view);
        MyPostAdapter adapter = new MyPostAdapter(mActivity, ptemp);
        listView.setAdapter(adapter);
    }






}
