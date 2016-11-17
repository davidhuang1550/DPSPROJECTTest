package com.example.david.dpsproject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

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
 * Created by david on 2016-11-16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    private View myView;
    private Button History;
    private Button Bookmarks;
    private Button Upload;

    FirebaseAuth authentication;
    DatabaseReference dbReference;
    FirebaseUser firebaseUser;

    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.profile_fragment,container,false);

        History = (Button)myView.findViewById(R.id.selfposts);
        Bookmarks = (Button)myView.findViewById(R.id.bookmarks);
        Upload = (Button)myView.findViewById(R.id.uploadprofile);

        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        firebaseUser = authentication.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.compose);
        if(fab!=null)fab.hide();

        History.setOnClickListener(this);
        Bookmarks.setOnClickListener(this);
        Upload.setOnClickListener(this);

        return myView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selfposts:
                dbReference.child("Users").child(firebaseUser.getUid()).child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);
                        ArrayList<SubString> postString = new ArrayList<SubString>();
                        postString = profile.getSubs();
                        final ArrayList<Post> posts = new ArrayList<Post>();
                        posts.clear();
                        for(SubString s : postString){
                            for(String p : s.getPosts()){
                                dbReference.child("Subs").child(s.getsubName()).child("posts").child(p).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try{
                                            Post p = dataSnapshot.getValue(Post.class);
                                            posts.add(p);
                                        }catch(DatabaseException e){
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                          listView = (ListView)myView.findViewById(R.id.profile_list_view);
                          MyPostAdapter adapter = new MyPostAdapter(getActivity(),posts);
                          listView.setAdapter(adapter);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                        //   listView = (ListView)myView.findViewById(R.id.profile_list_view);
                        //  MyPostAdapter adapter = new MyPostAdapter(getActivity(),posts);
                        //  listView.setAdapter(adapter);
                break;
            case R.id.bookmarks:

                break;
            case R.id.uploadprofile:

                break;
        }
    }
}
