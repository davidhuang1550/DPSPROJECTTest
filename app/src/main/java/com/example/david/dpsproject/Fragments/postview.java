package com.example.david.dpsproject.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by david on 2016-11-03.
 */
public class postview extends Fragment  {
    private TextView Title;
    private TextView Description;
    private View myView;
    private Activity mActivity;
    Post post;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_postview,container,false);

        firebaseAuth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser=firebaseAuth.getCurrentUser();

        bundle = getArguments();
        post = (Post)bundle.getSerializable("Post_Object");

        Title= (TextView)myView.findViewById(R.id.PostTitle);
        Description= (TextView)myView.findViewById(R.id.PostDesc);
        Title.setText( post.getTitle());
        Description.setText(post.getDescription());

        databaseReference.child("Users").child(firebaseUser.getUid()).child("Viewed").child("Soccer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    FragmentManager fragmentManager = mActivity.getFragmentManager();
                    boolean go_post_view_button=true;
                    for(DataSnapshot s: dataSnapshot.getChildren()) {
                        String p = s.getValue(String.class);
                        if(p.equals(post.getKey())){
                            go_post_view_button=false;
                        }
                    }
                    if(go_post_view_button){
                        post_view_button pv= new post_view_button();
                        pv.setArguments(bundle);
                        fragmentManager.beginTransaction().add(R.id.replaceable_frame, pv).commit();
                    }
                    else{
                        VoteBarFrame voteBarFrame = new VoteBarFrame();
                        bundle.putInt("yes",post.getYes());
                        bundle.putInt("no",post.getNo());
                        voteBarFrame.setArguments(bundle);
                        fragmentManager.beginTransaction().add(R.id.replaceable_frame, voteBarFrame).commit();
                    }

                }catch(DatabaseException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mActivity,databaseError.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        return myView;
    }
}
