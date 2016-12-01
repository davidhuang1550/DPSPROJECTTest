package com.example.david.dpsproject.AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.dpsproject.Class.Users;
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
 * Created by david on 2016-11-28.
 */
public class UserAsyncTask extends AsyncTask<Void,Void,Void>{

    private FirebaseAuth authentication;
    private DatabaseReference dbReference;
    private FirebaseUser firebaseUser;
    private Activity mActivity;
    private Users tempU;
    private ProgressDialog pDialog;
    public void UserAsyncTask(){

        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        firebaseUser = authentication.getCurrentUser();

    }

    public void UserAsyncTask(navigation activity){
        mActivity=activity;
       // authentication=auth;
     //   dbReference=dbRef;
      //  firebaseUser=firebaseU;
    }
    public void ShowProgressDialog() { // progress
        if (pDialog == null) {
            pDialog = new ProgressDialog(mActivity);
            pDialog.setMessage("Loading Posts");
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
    protected void onPreExecute() {
        ShowProgressDialog();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //ShowProgressDialog();
        try {
            //    boolean keepgoing = true;
            //Thread.sleep(1000);
            do {

                dbReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            tempU = dataSnapshot.getValue(Users.class);
                            //name = (TextView) findViewById(R.id.headText);
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
            Thread.sleep(500);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        HideProgressDialog();
        if (tempU != null) {
            ((navigation)mActivity).setUser(tempU);
            TextView name = (TextView)mActivity.findViewById(R.id.headText);
            name.setText(tempU.getUserName());
            final View layout = (View) mActivity.findViewById(R.id.navPic);
            if (tempU.getPicture() != "" && tempU.getPicture() != null) {
                byte[] decodedString = Base64.decode(tempU.getPicture(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ((navigation)mActivity).setprofilepic(decodedByte);
                layout.setBackground(new BitmapDrawable(mActivity.getResources(), decodedByte));
            }
            Menu menu=((navigation)mActivity).getSubMenu();

            if(menu!=null) {
                ArrayList<String> subcat = tempU.getSubcategory();
                for (int i = 0; i < subcat.size(); i++) {
                    menu.add(R.id.second_nav, Menu.NONE, 0, subcat.get(i));
                }
            }
        }
        else System.out.println("error1"); // set logout settings if this happens
    }


}
