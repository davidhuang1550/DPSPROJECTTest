package com.example.david.dpsproject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 2016-10-27.
 */

public class CreatePost extends Fragment implements View.OnClickListener{
    View myView;
    Button post_button;
    FirebaseAuth authentication;
    DatabaseReference dbReference;
    FirebaseUser firebaseUser;
    String sub_cat;
    TextView title;
    TextView desc;
    Users user;
    Profile profile;
    Activity mActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Create Post");
        myView = inflater.inflate(R.layout.create_post,container,false);
        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        post_button = (Button)myView.findViewById(R.id.post_button_upload);
        firebaseUser = authentication.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.compose);
        if(fab!=null)fab.hide(); // hide it in the create post area


        NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view); // hide search nav
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.search).setVisible(false);


        post_button.setOnClickListener(this);


        return myView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public void onClick(View v) {
        TextView sub_cat_view = (TextView) myView.findViewById(R.id.sub_post);
        sub_cat = sub_cat_view.getText().toString();
        title = (TextView) myView.findViewById(R.id.title_post);
        desc = (TextView) myView.findViewById(R.id.description_post);

        //System.out.println("JESUS");

        if (!sub_cat.equals("") && !title.getText().equals("") && !desc.getText().equals("")) {
            dbReference.child("Users").child(firebaseUser.getUid()).child("Posts").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        profile = dataSnapshot.getValue(Profile.class);
                        if(profile==null){
                            profile = new Profile(new ArrayList<SubString>(),new ArrayList<SubString>());
                        }
                        dbReference.child("Sub").child(sub_cat).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {

                                    Post post = new Post(FirebaseAuth.getInstance().getCurrentUser().getUid(),title.getText().toString(),desc.getText().toString());

                                    if (dataSnapshot.getValue() != null) {

                                        DatabaseReference postref=dbReference.child("Sub").child(sub_cat).child("posts").push();
                                        postref.setValue(post);
                                        profile.getSubstring(sub_cat,postref.getKey());

                                        dbReference.child("Users").child(firebaseUser.getUid()).child("Posts").setValue(profile);
                                        FrontPage frontPage = new FrontPage();
                                        FragmentManager fragmentManager = mActivity.getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, frontPage).commit();

                                    } else {
                                        Sub sub = new Sub();
                                        Post first_post = new Post("ADMIN","FIRST POST OF THE SUB", ""); // first one

                                        sub.pushPost(first_post);
                                        dbReference.child("Sub").child(sub_cat).setValue(sub);

                                        DatabaseReference postref=dbReference.child("Sub").child(sub_cat).child("posts").push();
                                        postref.setValue(post);

                                        profile.getSubstring(sub_cat,postref.getKey());

                                        dbReference.child("Users").child(firebaseUser.getUid()).child("Posts").setValue(profile);

                                        dbReference.child("Sub").child(sub_cat).child("posts").child("0").removeValue(); // remove inital commit
                                        FrontPage frontPage = new FrontPage();
                                        FragmentManager fragmentManager = mActivity.getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, frontPage).commit();
                                    }


                                } catch (DatabaseException e) {
                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                       // user = dataSnapshot.getValue(Users.class);
                    }
                    catch (DatabaseException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(),"something went wrong with grabbing user",Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            Toast.makeText(getActivity(),"Every Field Must not be empty",Toast.LENGTH_SHORT).show();
        }

    }

}
