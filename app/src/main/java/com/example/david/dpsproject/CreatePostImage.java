package com.example.david.dpsproject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.LayoutInflater;
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

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by xlhuang3 on 11/8/2016.
 */
public class CreatePostImage  extends Fragment implements View.OnClickListener{
    private View myView;
    private Button Upload;
    private Button Create;

    String sub_cat;
    TextView title;

    FirebaseAuth authentication;
    DatabaseReference dbReference;
    //FirebaseUser firebaseUser;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.create_post_image,container,false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.compose);
        if(fab!=null)fab.hide(); // hide it in the create post area

        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
     //   firebaseUser = authentication.getCurrentUser();

        Upload = (Button)myView.findViewById(R.id.upload);
        Create = (Button)myView.findViewById(R.id.post_button_upload);

        Upload.setOnClickListener(this);
        Create.setOnClickListener(this);


        return myView;

    }
    public boolean checkReadExternalPermission(){
        String permission = "android.permission.READ_EXTERNAL_STORAGE"; // get permissions
        int res= getContext().checkCallingOrSelfPermission(permission);
        return (res== PackageManager.PERMISSION_GRANTED);
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    Toast.makeText(getActivity(),"Permission needed to read image",Toast.LENGTH_SHORT).show();
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
                getActivity().startActivityForResult(galleryIntent, 1);


                break;
            case R.id.post_button_upload:
                TextView textView = (TextView)myView.findViewById(R.id.uploadTextview);
                TextView sub_cat_view = (TextView) myView.findViewById(R.id.sub_post);
                sub_cat = sub_cat_view.getText().toString();
                title = (TextView) myView.findViewById(R.id.title_post);
                if(((navigation)getActivity()).imageUpload!=null&&!sub_cat.equals("") && !title.getText().equals("")) {
                    dbReference.child("Sub").child(sub_cat).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if(checkReadExternalPermission()) {
                                    SharedPreferences preferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                                    //Post post = new Post(preferences.getString("UID", ""), title.getText().toString(), "");

                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inSampleSize = 8;

                                    Bitmap bitMap = BitmapFactory.decodeFile(((navigation) getActivity()).filePath, options);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    byte[] bytes = stream.toByteArray();
                                    String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

                                    Post post = new Post(FirebaseAuth.getInstance().getCurrentUser().getUid(), title.getText().toString(), "", base64Image);
                                    if (dataSnapshot.getValue() != null) {

                                        DatabaseReference postref = dbReference.child("Sub").child(sub_cat).child("posts").push();
                                        postref.setValue(post);
                                        String key = postref.getKey();
                                        FrontPage frontPage = new FrontPage();

                                        // DatabaseReference postimg = dbReference.child("Sub").child(sub_cat).child("posts").child(key).child("Image").;
                                        // postimg.putFile

                                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, frontPage).commit();

                                    } else {
                                        Sub sub = new Sub();
                                        Post first_post = new Post("ADMIN", "FIRST POST OF THE SUB", ""); // first one

                                        sub.pushPost(first_post);
                                        dbReference.child("Sub").child(sub_cat).setValue(sub);

                                        DatabaseReference postref = dbReference.child("Sub").child(sub_cat).child("posts").push();
                                        postref.setValue(post);

                                        dbReference.child("Sub").child(sub_cat).child("posts").child("0").removeValue(); // remove inital commit

                                        FrontPage frontPage = new FrontPage();
                                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.content_frame, frontPage).commit();
                                        // subRef.setValue(subMap);

                                    }
                                }
                                else{
                                   requestForSpecificPermission();
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
                }
                else{
                        Toast.makeText(getActivity(),"Every Field Must not be empty",Toast.LENGTH_SHORT).show();
                    }


                break;



        }
    }
}
