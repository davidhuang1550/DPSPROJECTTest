package com.example.david.dpsproject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by david on 2016-11-03.
 */
public class postview extends Fragment implements View.OnClickListener {
    private TextView Title;
    private TextView Description;
    private Button yes;
    private Button No;
    private View myView;
    Post post;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_postview,container,false);

        Bundle bundle = getArguments();
        post = (Post)bundle.getSerializable("Post_Object");
       // post = (Post)savedInstanceState.getSerializable("Post_Object");
        Title= (TextView)myView.findViewById(R.id.PostTitle);
        Description= (TextView)myView.findViewById(R.id.PostDesc);
        Title.setText( post.getTitle());
        Description.setText(post.getDescription());
        yes = (Button)myView.findViewById(R.id.yes);
        No = (Button)myView.findViewById(R.id.no);
        yes.setOnClickListener(this);
        No.setOnClickListener(this);

        return myView;
    }
    public void onClick(View v) {
        firebaseAuth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        switch (v.getId()){
            case R.id.yes:
                post.IncYes();
                databaseReference.child("Sub").child("Soccer").child(post.getKey()).setValue(post);
                break;
            case R.id.no:
                post.IncNo();
                databaseReference.child("Sub").child("Soccer").child(post.getKey()).setValue(post);
                break;
        }

    }
}
