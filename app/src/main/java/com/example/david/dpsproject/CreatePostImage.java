package com.example.david.dpsproject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.dpsproject.Adapters.MyPostAdapter;
import com.example.david.dpsproject.Class.Post;
import com.example.david.dpsproject.Class.Sub;
import com.example.david.dpsproject.Fragments.FrontPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InterruptedIOException;
import java.util.ArrayList;

/**
 * Created by xlhuang3 on 11/8/2016.
 */
public class CreatePostImage  extends Fragment implements View.OnClickListener{
    private View myView;
    private Button Upload;
    private Button Create;

    String sub_cat;
    TextView title;
    Bitmap bitMap;
    Activity mActivity;
    FirebaseAuth authentication;
    DatabaseReference dbReference;
    FirebaseUser firebaseUser;
    ProgressDialog pDialog;
    ArrayList<String>subString;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity= (navigation)this.getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.create_post_image,container,false);

        FloatingActionButton fab = (FloatingActionButton) mActivity.findViewById(R.id.compose);
        if(fab!=null)fab.hide(); // hide it in the create post area

        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        firebaseUser = authentication.getCurrentUser();

        Upload = (Button)myView.findViewById(R.id.upload);
        Create = (Button)myView.findViewById(R.id.post_button_upload);

        Upload.setOnClickListener(this);
        Create.setOnClickListener(this);


        return myView;

    }
    public boolean checkReadExternalPermission(){
        String permission = "android.permission.READ_EXTERNAL_STORAGE"; // get permissions
        int res= mActivity.checkCallingOrSelfPermission(permission);
        return (res== PackageManager.PERMISSION_GRANTED);
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }
    public void ShowProgressDialog() { // progress
        if (pDialog == null) {
            pDialog = new ProgressDialog(getContext());
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    Toast.makeText(mActivity,"Permission needed to read image",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.upload:
                final Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                mActivity.startActivityForResult(galleryIntent, 1);


                break;
            case R.id.post_button_upload:
                TextView textView = (TextView)myView.findViewById(R.id.uploadTextview);
                TextView sub_cat_view = (TextView) myView.findViewById(R.id.sub_post);
                sub_cat = sub_cat_view.getText().toString();
                title = (TextView) myView.findViewById(R.id.title_post);
                subString= new ArrayList<String>();
                if(((navigation)mActivity).imageUpload!=null&&!sub_cat.equals("") && !title.getText().equals("")) {
                    dbReference.child("Users").child(firebaseUser.getUid()).child("Posts").child(sub_cat).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {

                                subString.clear();
                                for (DataSnapshot s : dataSnapshot.getChildren()) {
                                    String p = s.getValue(String.class);
                                    subString.add(p);
                                }
                                dbReference.child("Sub").child(sub_cat).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        try {
                                            if (checkReadExternalPermission()) {
                                                mActivity=getActivity();
                                                final BitmapFactory.Options options = new BitmapFactory.Options();
                                                options.inSampleSize = 2;
                                                bitMap = BitmapFactory.decodeFile(((navigation) mActivity).filePath, options);
                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                                byte[] bytes = stream.toByteArray();
                                                String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
                                                FrontPage frontPage = new FrontPage();
                                                FragmentManager fragmentManager = mActivity.getFragmentManager();
                                                Post post = new Post(FirebaseAuth.getInstance().getCurrentUser().getUid(), title.getText().toString(), "", base64Image,System.currentTimeMillis()/1000,sub_cat);
                                                if (dataSnapshot.getValue() != null) {
                                                    DatabaseReference postref = dbReference.child("Sub").child(sub_cat).child("posts").push();
                                                    postref.setValue(post);
                                                    subString.add(postref.getKey());
                                                    dbReference.child("Users").child(firebaseUser.getUid()).child("Posts").child(sub_cat).setValue(subString);
                                                    fragmentManager.beginTransaction().replace(R.id.content_frame, frontPage).commit();
                                                } else {
                                                    Sub sub = new Sub();
                                                    Post first_post = new Post("ADMIN", "FIRST POST OF THE SUB", "",new Long(0),sub_cat); // first one
                                                    sub.pushPost(first_post);
                                                    dbReference.child("Sub").child(sub_cat).setValue(sub);
                                                    DatabaseReference postref = dbReference.child("Sub").child(sub_cat).child("posts").push();
                                                    postref.setValue(post);
                                                    subString.add(postref.getKey());
                                                    dbReference.child("Users").child(firebaseUser.getUid()).child("Posts").child(sub_cat).setValue(subString);

                                                    dbReference.child("Sub").child(sub_cat).child("posts").child("0").removeValue(); // remove inital commit

                                                    fragmentManager.beginTransaction().replace(R.id.content_frame, frontPage).commit();
                                                }



                                            } else {
                                                requestForSpecificPermission();
                                            }
                                        } catch (DatabaseException e) {
                                            Toast.makeText(mActivity, e.toString(), Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(mActivity, "something went wrong with grabbing user", Toast.LENGTH_SHORT).show();
                                    }
                                });
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
                        Toast.makeText(mActivity,"Every Field Must not be empty",Toast.LENGTH_SHORT).show();
                    }


                break;



        }
    }
}
