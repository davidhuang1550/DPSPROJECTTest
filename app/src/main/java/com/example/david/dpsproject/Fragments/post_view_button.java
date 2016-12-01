package com.example.david.dpsproject.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.david.dpsproject.AsyncTask.PostTask;
import com.example.david.dpsproject.AsyncTask.ProfileTask;
import com.example.david.dpsproject.Class.Post;
import com.example.david.dpsproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by david on 2016-11-23.
 */
public class post_view_button extends Fragment implements View.OnClickListener{
    private Activity mActivity;
    private View myView;
    private Button yes;
    private Button No;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private Post post;
    ProgressDialog pDialog;
    private ArrayList<String> templist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
    }
    public void HideProgressDialog() {
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.post_button_frame, container, false);

        Bundle bundle = getArguments();
        post = (Post)bundle.getSerializable("Post_Object");

        yes = (Button)myView.findViewById(R.id.yes);
        No = (Button)myView.findViewById(R.id.no);
        yes.setOnClickListener(this);
        No.setOnClickListener(this);

        yes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP) {
                    yes.setAlpha(1);
                }else if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    yes.setAlpha(new Float(.5));
                }


                return false;
            }
        });
        No.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP) {
                    No.setAlpha(1);
                }else if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    No.setAlpha(new Float(.5));
                }

                return false;
            }
        });

        return myView;
    }

    @Override
    public void onClick(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = firebaseAuth.getCurrentUser();
        FragmentManager fragmentManager = mActivity.getFragmentManager();

        Handler handler = new Handler();
        switch (view.getId()) {
            case R.id.yes:
                post.IncYes();
                databaseReference.child("Sub").child("Soccer").child("posts").child(post.getKey()).setValue(post);

                break;
            case R.id.no:
                post.IncNo();
                databaseReference.child("Sub").child("Soccer").child("posts").child(post.getKey()).setValue(post);

        }
        templist= new ArrayList<String>();
        databaseReference.child("Sub").child("Soccer").child("posts").child(post.getKey()).setValue(post);
        databaseReference.child("Users").child(firebaseUser.getUid()).child("Viewed").child("Soccer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    templist.clear();
                    for(DataSnapshot s: dataSnapshot.getChildren()) {
                        String p = s.getValue(String.class);
                        templist.add(p);
                    }
                    templist.add(post.getKey());
                    databaseReference.child("Users").child(firebaseUser.getUid()).child("Viewed").child("Soccer").setValue(templist);
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
