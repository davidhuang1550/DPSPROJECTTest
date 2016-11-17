package com.example.david.dpsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by david on 2016-10-25.
 */
public class SignUp extends Fragment implements View.OnClickListener {
    View myView;

    EditText userName;
    EditText userPassword;
    EditText passwordConfirm;
    Button signUp;
    ProgressDialog pDialog;
    //Firebase variables
    FirebaseAuth authentication;
    DatabaseReference dbReference;



    protected void createNewAccount(final String email, final String password){
        authentication.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                HideProgressDialog();
                if(!(task.isSuccessful())){
                    Toast.makeText(getActivity(),"authentication failed",Toast.LENGTH_SHORT).show();
                }
                else{
                    Users u = new Users(email,password, new Profile(new ArrayList<SubString>(),new ArrayList<SubString>()));
                    dbReference.child("Users").child(task.getResult().getUser().getUid()).setValue(u);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame,new LogIn()).commit();

                }
            }
        });
    }
    public void ShowProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Creating New Account");
            pDialog.setIndeterminate(true);
        }
        pDialog.show();
    }
    public void HideProgressDialog() {
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }


    public void onClick(View v) {
        createNewAccount(userName.getText().toString(),userPassword.getText().toString());
        ShowProgressDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Sign Up");
        myView = inflater.inflate(R.layout.sign_up,container,false);
        userName = (EditText)myView.findViewById(R.id.userName);
        userPassword = (EditText)myView.findViewById(R.id.userPassword);
        passwordConfirm =(EditText)myView.findViewById(R.id.passwordConfirm);
        signUp =(Button)myView.findViewById(R.id.signup);


        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database

        signUp.setOnClickListener(this);

        return myView;
    }
}
