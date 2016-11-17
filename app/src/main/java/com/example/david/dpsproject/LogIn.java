package com.example.david.dpsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * Created by david on 2016-10-25.
 */
public class LogIn extends Fragment implements View.OnClickListener {
    View myView;
    FirebaseAuth authentication;
    DatabaseReference dbReference;
    FirebaseAuth.AuthStateListener authStateListener;
    EditText userName;
    EditText userPassword;
    ProgressDialog pDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Login");
        myView = inflater.inflate(R.layout.login_in,container,false);
        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        FirebaseUser firebaseUser = authentication.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.compose);
        if(fab!=null)fab.hide(); // hide it in the create post area

        userName = (EditText)myView.findViewById(R.id.userName);
        userPassword = (EditText) myView.findViewById(R.id.userPassword);
        Button b = (Button) myView.findViewById(R.id.signIn);
        b.setOnClickListener(this);
        Button signup = (Button)myView.findViewById(R.id.signup);
        signup.setOnClickListener(this);



        return myView;
    }
    public void ShowProgressDialog() { // progress
        if (pDialog == null) {
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Signing In");
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signIn:
                ShowProgressDialog();
                Login(userName.getText().toString(), userPassword.getText().toString());
                break;
            case R.id.signup:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new SignUp()).commit();
                break;
        }

    }
    protected void Login(final String email, final String password){
        authentication.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 HideProgressDialog();
                if(!(task.isSuccessful())){
                    Toast.makeText(getContext(),"authentication failed",Toast.LENGTH_SHORT).show();
                }
                else{
                    Bundle bundle = new Bundle();
                    Users u = new Users(email,password, new Profile(new ArrayList<SubString>(),new ArrayList<SubString>()));
                    FrontPage fragment = new FrontPage();
                    FragmentManager fragmentManager = getFragmentManager();

                    dbReference.child("Users").child(task.getResult().getUser().getUid()).setValue(u);
                    bundle.putString("UID",task.getResult().getUser().getUid().toString());
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();
                   // setArguments(bundle);
                   // fragmentManager.beginTransaction().replace(R.id.content_frame,new FirstFragment()).commit();
                }
            }
        });
    }
}
