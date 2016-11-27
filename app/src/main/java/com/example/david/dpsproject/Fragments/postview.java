package com.example.david.dpsproject.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.dpsproject.Class.Post;
import com.example.david.dpsproject.R;
import com.example.david.dpsproject.navigation;
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
        bundle = getArguments();
        post = (Post) bundle.getSerializable("Post_Object");

        if(post.getImage()!=null&& (!post.getImage().equals(""))){
            myView =inflater.inflate(R.layout.fragment_postview_picture,container,false);
            ImageView imageView = (ImageView)myView.findViewById(R.id.imageView);
            byte[] decodedString = Base64.decode(post.getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageDrawable(new BitmapDrawable(mActivity.getResources(),decodedByte));
        }
        else{
            myView = inflater.inflate(R.layout.fragment_postview, container, false);
            Description = (TextView) myView.findViewById(R.id.PostDesc);
            Description.setText(post.getDescription());
        }

        //Fragment fragment = mActivity.getFragmentManager().findFragmentByTag("FrontPage");
       // View layout = (View)mActivity.findViewById(R.id.frontpage);
        //layout.setClickable(false);
       /// ListView listView = (ListView)layout.findViewById(R.id.postview);
        //listView.setClickable(false);
       /* layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });*/

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = firebaseAuth.getCurrentUser();

        Title = (TextView) myView.findViewById(R.id.PostTitle);
        Title.setText(post.getTitle());


        if (firebaseUser != null) {
            databaseReference.child("Users").child(firebaseUser.getUid()).child("viewed").child("Soccer").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        FragmentManager fragmentManager = mActivity.getFragmentManager();
                        boolean go_post_view_button = true;
                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                            String p = s.getValue(String.class);
                            if (p.equals(post.getKey())) {
                                go_post_view_button = false;
                            }
                        }
                        if (go_post_view_button) {
                            post_view_button pv = new post_view_button();
                            pv.setArguments(bundle);
                            fragmentManager.beginTransaction().add(R.id.replaceable_frame, pv).commit();
                        } else {
                            VoteBarFrame voteBarFrame = new VoteBarFrame();
                            bundle.putInt("yes", post.getYes());
                            bundle.putInt("no", post.getNo());
                            voteBarFrame.setArguments(bundle);
                            fragmentManager.beginTransaction().add(R.id.replaceable_frame, voteBarFrame).commit();
                        }

                    } catch (DatabaseException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(mActivity, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            Toast.makeText(mActivity,"Please sign in to vote",Toast.LENGTH_LONG).show();
        }
        return myView;
    }
}
