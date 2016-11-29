package com.example.david.dpsproject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.app.FragmentManager;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.dpsproject.AsyncTask.UserAsyncTask;
import com.example.david.dpsproject.Class.Users;
import com.example.david.dpsproject.Dialog.PleaseLogin;
import com.example.david.dpsproject.Dialog.SearchDialog;
import com.example.david.dpsproject.Fragments.CreatePost;
import com.example.david.dpsproject.Fragments.FrontPage;
import com.example.david.dpsproject.Fragments.LogIn;
import com.example.david.dpsproject.Fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
/*
change it up in a way so that the user does not pull everything from db just to change one thing but append to the end instead of adding to arraylist
 */
public class navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,FragmentManager.OnBackStackChangedListener{
    protected Toolbar toolbar;
    private  String UID;

    DatabaseReference dbReference;
    FirebaseAuth authentication;
    FirebaseUser firebaseUser;

    NavigationView navigationView;

    String filePath;
    Bitmap decodedprofilepic;
    ProgressDialog pDialog;
    Menu subMenu;
    Users tempU;
    String SubCat;
    TextView name;
    MenuItem subscribeId;
    MenuItem unsubscribId;
    Uri imageUpload =null;
    Bitmap decodedByte;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        firebaseUser = authentication.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.compose);
        final FloatingActionButton fab_image = (FloatingActionButton) findViewById(R.id.compse_images);
        final FloatingActionButton fab_desc = (FloatingActionButton) findViewById(R.id.compse_desc);
        fab_desc.setSize(FloatingActionButton.SIZE_MINI);
        fab_image.setSize(FloatingActionButton.SIZE_MINI);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser = authentication.getCurrentUser();
               if(firebaseUser!=null){ // show and hide compose
                   if(fab_image.getVisibility()==View.VISIBLE && fab_desc.getVisibility()==view.VISIBLE){
                       fab_desc.hide();
                       fab_image.hide();
                   }
                   else{
                       fab_desc.show();
                       fab_image.show();
                   }

               }
                else{
                   Bundle bundle = new Bundle();
                   bundle.putString("Message","You must be logged in to create post");
                   PleaseLogin pleaseLogin = new PleaseLogin();
                   pleaseLogin.setArguments(bundle);
                   pleaseLogin.show(getFragmentManager(),"Alert Dialog Fragment");
               }



            }
        });
        fab_image.setOnClickListener(new View.OnClickListener(){ // go to create image
            @Override
            public void onClick(View view) {
                fab_desc.hide();
                fab_image.hide();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame,new CreatePostImage()).commit();
            }
        });
        fab_desc.setOnClickListener(new View.OnClickListener(){ // go to create desc
            public void onClick(View view) {
                fab_desc.hide();
                fab_image.hide();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame,new CreatePost()).commit();
            }
        });
      //  final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        subMenu=navigationView.getMenu();

        FrontPage frontPage= new FrontPage();
        Bundle bundle = new Bundle();
        if(firebaseUser!=null){
            getUser();
            bundle.putString("user","true");
            frontPage.setArguments(bundle);
        }
        else{
            bundle.putString("user","false");
            frontPage.setArguments(bundle);

        }
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame,frontPage,"FrontPage").commit();

    }
    public void setprofilepic(Bitmap p){
        decodedprofilepic=p;
    }
    public Bitmap getprofilepic(){
        return decodedprofilepic;
    }
    public Toolbar getToolbar(){
        return toolbar;
    }
    public void setUID(String uid){
        UID=uid;
    }
    public void setSubCat(String sub){
        SubCat=sub;
    }
    public String getSubCat(){
        return SubCat;
    }
    private void switchFragment(Fragment fragment){
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).addToBackStack("Tag").commit();
    }
    @Override
    public void onBackStackChanged() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        subscribeId=menu.getItem(0);
        subscribeId.setVisible(false);
        unsubscribId=menu.getItem(1);
        unsubscribId.setVisible(false);
        return true;
    }
    public void showUnsubscribe(){
        if(unsubscribId!=null)unsubscribId.setVisible(true);
    }
    public void showSubscribe(){
        if(subscribeId!=null)subscribeId.setVisible(true);
    }
    public void hideUnsubscribe(){
        if(unsubscribId!=null)unsubscribId.setVisible(false);
    }
    public void hidesubscribe(){
        if(subscribeId!=null)subscribeId.setVisible(false);
    }
    public void hideAllSubscribe(){

        if(subscribeId!=null)subscribeId.setVisible(false);
        if(unsubscribId!=null)unsubscribId.setVisible(false);

    }

    private void pushsubtodatabase(){
        dbReference.child("Users").child(firebaseUser.getUid()).child("Subcategory").setValue(tempU.getSubcategory());
    }
    public void reload_menu(){
        Menu menu=getSubMenu();
        if(menu!=null){
            menu.removeGroup(R.id.second_nav);
            ArrayList<String> subcat =tempU.getSubcategory();
            for(int i=0; i<subcat.size();i++){
                menu.add(R.id.second_nav,Menu.NONE,0,subcat.get(i));
            }
        }
    }
    public void remove_menu(){
        Menu menu=getSubMenu();
        if(menu!=null){
            menu.removeGroup(R.id.second_nav);
        }
    }
    public void ModifySub(String s,int p){
        if(tempU!=null){
            if(p==0){
                tempU.addSub(s);
                pushsubtodatabase();
            }else if(p==1){
                tempU.deleteSub(s);
                pushsubtodatabase();
            }else{Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();}

        }else{Toast.makeText(this,"Please sign in to subscribe",Toast.LENGTH_SHORT).show();}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final ProgressDialog mprogressDialog = ProgressDialog.show(this, "Please wait","Saving Changes", true);
        if(SubCat!=null) {
            if (id == R.id.subscribe) {
                new Thread(){
                    @Override
                    public void run() {
                        ModifySub(SubCat,0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reload_menu();
                                showUnsubscribe();
                                hidesubscribe();
                                mprogressDialog.dismiss();
                            }
                        });

                    }
                }.start();


            } else if (id == R.id.Unsubscribe) {
                new Thread(){
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ModifySub(SubCat,1);
                                showSubscribe();
                                hideUnsubscribe();
                                mprogressDialog.dismiss();
                            }
                        });
                    }
                }.start();


            }
        }

        return super.onOptionsItemSelected(item);
    }
    public void Load_User_profile(){

        firebaseUser = authentication.getCurrentUser();
        getUser();
    }
    public void remove_nav_image(){
        View Layout = (View)findViewById(R.id.navPic);
        Layout.setBackgroundResource(R.drawable.default_desktop);
    }
    public void freeUserData(){
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.login).setVisible(true);// set logout and login respectively
        nav_Menu.findItem(R.id.signout).setVisible(false);
        nav_Menu.findItem(R.id.profile).setVisible(false);
        TextView name = (TextView) findViewById(R.id.headText); // remove menu name
        name.setText("");
        remove_menu();
        remove_nav_image();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id=item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();


        if (id == R.id.frontpage) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new FrontPage(),"FrontPage").commit();
        }
        else if(id==R.id.profile){
            fragmentManager.beginTransaction().replace(R.id.content_frame,new ProfileFragment()).commit();
        }else if (id == R.id.login) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new LogIn()).commit();
        } else if(id == R.id.search){
           // fragmentManager.beginTransaction().add(R.id.content_frame,new SearchFragment(),"search").commit();
            SearchDialog searchDialog = new SearchDialog();
            searchDialog.show(getFragmentManager(),"Search Dialog Fragment");

        } else if(id==R.id.signout){
            freeUserData();
            FirebaseAuth.getInstance().signOut();
            fragmentManager.beginTransaction().replace(R.id.content_frame,new LogIn()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public String getFilePath(){
        return filePath;
    }
    public boolean checkReadExternalPermission(){
        String permission = "android.permission.READ_EXTERNAL_STORAGE"; // get permissions
        int res= checkCallingOrSelfPermission(permission);
        return (res== PackageManager.PERMISSION_GRANTED);
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }
    public void setFirebaseUser(FirebaseUser firebaseUsers){
        firebaseUser=firebaseUsers;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    Toast.makeText(this,"Permission needed to read image",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public Users getworkingUser(){
        return tempU;
    }
    public void getUser(){

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
                                    name = (TextView) findViewById(R.id.headText);
                                  //  byte[] decodedString = Base64.decode(tempU.getPicture(), Base64.DEFAULT);
                                 //   decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                   // setprofilepic(decodedByte);
                                } catch (DatabaseException e) {
                                    Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //   Thread.sleep(1000);
                    } while (name != null && tempU != null);
                    Thread.sleep(500);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
             //   HideProgressDialog();
                if (tempU != null) {
                    name.setText(tempU.getUserName());
                    final View layout = (View) findViewById(R.id.navPic);
                    if (tempU.getPicture() != "" && tempU.getPicture() != null) {
                        byte[] decodedString = Base64.decode(tempU.getPicture(), Base64.DEFAULT);
                        decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        setprofilepic(decodedByte);
                        layout.setBackground(new BitmapDrawable(getResources(), decodedByte));
                    }
                    Menu menu=getSubMenu();

                    if(menu!=null) {
                        ArrayList<String> subcat = tempU.getSubcategory();
                        for (int i = 0; i < subcat.size(); i++) {
                            menu.add(R.id.second_nav, Menu.NONE, 0, subcat.get(i));
                        }
                    }
                }
                else System.out.println("error1"); // set logout settings if this happens
            }

        };

        Handler userhandler = new Handler();

        getuserName.execute();

        Runnable userthread= new Runnable() {
            @Override
            public void run() {
                if(getuserName.getStatus()==AsyncTask.Status.RUNNING){
                    getuserName.cancel(true);
                    HideProgressDialog();
                    Toast.makeText(getApplicationContext(),"Error has occured ",Toast.LENGTH_SHORT).show();
                }
            }
        };//,5000);
        userhandler.postDelayed(userthread,5000);
    }
    public void setUser(Users user){
        tempU=user;
    }
    public Menu getSubMenu(){
        return subMenu;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==1){
                imageUpload = data.getData();
                filePath=getPath(this,imageUpload);

                final String action = data.getDataString();
                String prefix = "/image";
                String split = action.substring(action.indexOf(prefix)+prefix.length());
                TextView textView = (TextView)findViewById(R.id.uploadTextview);
                textView.setText("image"+split);
            }
            else if(requestCode==2){
                imageUpload = data.getData();
                filePath=getPath(this,imageUpload);

                //if (checkReadExternalPermission()) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();

                    options.inSampleSize = 3;
                    options.inScaled=false;
                    Bitmap bitMap = BitmapFactory.decodeFile(filePath, options);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    final String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);


                    dbReference.child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Users users = dataSnapshot.getValue(Users.class);
                            users.setPicture(base64Image);
                            dbReference.child("Users").child(firebaseUser.getUid()).child("picture").setValue(base64Image);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

        }
        else{
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }
    public void ShowProgressDialog() { // progress
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
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

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}

