package com.example.david.dpsproject.AsyncTask;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.david.dpsproject.Class.Post;
import com.example.david.dpsproject.Fragments.VoteBarFrame;
import com.example.david.dpsproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by david on 2016-11-23.
 * currently not using this
 */
public class PostTask extends AsyncTask<Void,Void,Void> {
    private ProgressDialog pDialog;
    private Activity mActivity;
    private int yes;
    private int no;
    private FirebaseAuth authentication;
    private DatabaseReference dbReference;
    private FirebaseUser firebaseUser;
    private ArrayList<String> templist;
    private boolean stop;
    private Post post;

    public PostTask(){
        no=0;
        yes=0;
    }

    public PostTask(Activity activity,FirebaseAuth fbA,DatabaseReference dbR,FirebaseUser fbU,int y,int n,Post p){
        mActivity=activity;
        authentication=fbA;
        dbReference=dbR;
        firebaseUser=fbU;
        yes=y;
        no=n;
        post=p;
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
            stop=false;
            Thread.sleep(000);
            do{

                if(no!=0){
                    stop=true;
                }
                else if(yes!=0){
                    stop=true;
                }
            }while(!stop);
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        HideProgressDialog();
        templist= new ArrayList<String>();
        dbReference.child("Sub").child("Soccer").child("posts").child(post.getKey()).setValue(post);
        dbReference.child("Users").child(firebaseUser.getUid()).child("Viewed").child("Soccer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    templist.clear();
                    for(DataSnapshot s: dataSnapshot.getChildren()) {
                        String p = s.getValue(String.class);
                        templist.add(p);
                    }
                    templist.add(post.getKey());
                    dbReference.child("Users").child(firebaseUser.getUid()).child("Viewed").child("Soccer").setValue(templist);
                    Bundle bundle = new Bundle();
                    bundle.putInt("yes",post.getYes());
                    bundle.putInt("no",post.getNo());

                    VoteBarFrame voteBarFrame = new VoteBarFrame();
                    voteBarFrame.setArguments(bundle);
                    FragmentManager fragmentManager = mActivity.getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.replaceable_frame, voteBarFrame).commit();
                }catch (DatabaseException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
