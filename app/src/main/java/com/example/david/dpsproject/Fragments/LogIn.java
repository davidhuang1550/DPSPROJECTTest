package com.example.david.dpsproject.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.dpsproject.AsyncTask.UserAsyncTask;
import com.example.david.dpsproject.Class.Users;
import com.example.david.dpsproject.R;
import com.example.david.dpsproject.navigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

/**
 * Created by david on 2016-10-25.
 */
public class LogIn extends Fragment implements View.OnClickListener {
    View myView;
    FirebaseAuth authentication;
    DatabaseReference dbReference;
    FirebaseUser firebaseUser;
    FirebaseAuth.AuthStateListener authStateListener;
    EditText userName;
    EditText userPassword;
    ProgressDialog pDialog;
    Activity mActivity;
    Users tempU;
    TextView name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Login");
        myView = inflater.inflate(R.layout.login_in,container,false);
        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        firebaseUser = authentication.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.compose);
        if(fab!=null)fab.hide(); // hide it in the create post area

        userName = (EditText)myView.findViewById(R.id.userName);
        userPassword = (EditText) myView.findViewById(R.id.userPassword);
        Button b = (Button) myView.findViewById(R.id.signIn);
        b.setOnClickListener(this);
        Button signup = (Button)myView.findViewById(R.id.signup);
        signup.setOnClickListener(this);
        //UserAsyncTask ue = new UserAsyncTask(mActivity);



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
                    Users u = new Users(email,password, new HashMap<String,ArrayList<String>>(),new HashMap<String,ArrayList<String>>(),
                            new HashMap<String,ArrayList<String>>(),"",new ArrayList<String>());
                    final FrontPage fragment = new FrontPage();
                    FragmentManager fragmentManager = getFragmentManager();

                    bundle.putString("UID",task.getResult().getUser().getUid().toString());
                    fragment.setArguments(bundle);

                    final AsyncTask<Void, Void, Void> getuserName = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {
                            ShowProgressDialog();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                do {

                                    dbReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                tempU = dataSnapshot.getValue(Users.class);
                                                //  byte[] decodedString = Base64.decode(tempU.getPicture(), Base64.DEFAULT);
                                                //   decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                                // setprofilepic(decodedByte);
                                            } catch (DatabaseException e) {
                                                Toast.makeText(mActivity, "something went wrong", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    //   Thread.sleep(1000);
                                } while (tempU != null);
                                Thread.sleep(1000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            HideProgressDialog();
                            if (tempU != null) {
                                name = (TextView) mActivity.findViewById(R.id.headText);
                                if(name!=null)name.setText(tempU.getUserName());
                                ((navigation)mActivity).setUser(tempU);
                                final View layout = (View) mActivity.findViewById(R.id.navPic);
                                if (tempU.getPicture() != "" && tempU.getPicture() != null) {
                                    byte[] decodedString = Base64.decode(tempU.getPicture(), Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    ((navigation)mActivity).setprofilepic(decodedByte);
                                    layout.setBackground(new BitmapDrawable(getResources(), decodedByte));
                                }
                                Menu menu=((navigation)mActivity).getSubMenu();

                                if(menu!=null) {
                                    ArrayList<String> subcat = tempU.getSubcategory();
                                    for (int i = 0; i < subcat.size(); i++) {
                                        menu.add(R.id.second_nav, Menu.NONE, 0, subcat.get(i));
                                    }
                                }
                                getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();
                            }
                            else System.out.println("error1"); // set logout settings if this happens
                        }

                    };
                    firebaseUser=authentication.getCurrentUser();
                    ((navigation)mActivity).setFirebaseUser(firebaseUser);
                    Handler userhandler = new Handler();
                    getuserName.execute();
                    Runnable userthread= new Runnable() {
                        @Override
                        public void run() {
                            if(getuserName.getStatus()==AsyncTask.Status.RUNNING){
                                getuserName.cancel(true);
                                HideProgressDialog();
                                Toast.makeText(mActivity,"Error has occured ",Toast.LENGTH_SHORT).show();
                            }
                        }
                    };//,5000);
                    userhandler.postDelayed(userthread,5000);
                }
            }
        });
    }
    public void onDestroy() {
        ViewGroup container = (ViewGroup)mActivity.findViewById(R.id.content_frame);
        container.removeAllViews();
        super.onDestroy();
    }
}
