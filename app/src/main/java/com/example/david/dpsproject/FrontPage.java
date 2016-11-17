package com.example.david.dpsproject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by david on 2016-10-25.
 */
public class FrontPage extends Fragment {
    View myView;
    View mainView;

    ArrayList<Users> users;
    FirebaseAuth authentication;
    DatabaseReference dbReference;
    ArrayList<Post> posts;
    Bundle bundle;

    FloatingActionButton fab;
    NavigationView navigationView;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;

    String Uid;

    @Override
    public void onStart() {
        super.onStart();
        firebaseUser = authentication.getCurrentUser();
        Menu nav_Menu = navigationView.getMenu();

        NavigationView navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        if(firebaseUser!=null){ // find if user is logged in set the title and replace sign in with logout

            nav_Menu.findItem(R.id.login).setVisible(false);
            nav_Menu.findItem(R.id.signout).setVisible(true);
            dbReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        Users tempU = dataSnapshot.getValue(Users.class);
                        TextView name = (TextView) mainView.findViewById(R.id.headText);
                        name.setText(tempU.getUserName());
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            nav_Menu.findItem(R.id.login).setVisible(true);
            nav_Menu.findItem(R.id.profile).setVisible(false);
            nav_Menu.findItem(R.id.signout).setVisible(false);
        }
        posts =new ArrayList<Post>();
        dbReference.child("Sub").child("Soccer").child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                for(DataSnapshot s: dataSnapshot.getChildren()){
                    Post p= s.getValue(Post.class);
                    p.setKey(s.getKey());

                    posts.add(p);


                    DataSnapshot s2 = s.child("Comments");
                    ArrayList<Comment> comments = new ArrayList<Comment>();
                    for(DataSnapshot s3 : s2.getChildren()) {
                        comments.add(s3.getValue(Comment.class));
                    }

                    p.setComments(comments);



                }



                ListView listView = (ListView)myView.findViewById(R.id.postview);
                MyPostAdapter adapter = new MyPostAdapter(getActivity(),posts);
                listView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Front Page");
        myView = inflater.inflate(R.layout.front_page,container,false);
        mainView = inflater.inflate(R.layout.nav_header_main, container, false);
        bundle = new Bundle();
        bundle = getArguments();
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        users = new ArrayList<>();

        if(firebaseUser!=null){
            SharedPreferences preferences = getContext().getSharedPreferences("pref",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =preferences.edit().putString("UID",firebaseUser.getUid());
            editor.commit();
        }



        fab = (FloatingActionButton) getActivity().findViewById(R.id.compose);
        FloatingActionButton fab_image = (FloatingActionButton) getActivity().findViewById(R.id.compse_images);
        FloatingActionButton fab_desc = (FloatingActionButton) getActivity().findViewById(R.id.compse_desc);
        if(fab_image!=null)fab_image.hide();
        if(fab_desc!=null)fab_desc.hide();
        if(fab!=null)fab.show();

        return myView;
    }


    @Override
    public void onResume() {
        super.onResume();
        Menu nav_Menu = navigationView.getMenu();
        if(nav_Menu!=null)nav_Menu.findItem(R.id.search).setVisible(true);
        if(fab!=null)fab.show(); // when in front page you must show compose option
    }
}
