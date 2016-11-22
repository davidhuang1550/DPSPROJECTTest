package com.example.david.dpsproject;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
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

import com.example.david.dpsproject.Dialog.PleaseLogin;
import com.example.david.dpsproject.Dialog.SearchDialog;
import com.example.david.dpsproject.Fragments.CreatePost;
import com.example.david.dpsproject.Fragments.FrontPage;
import com.example.david.dpsproject.Fragments.LogIn;
import com.example.david.dpsproject.Fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected Toolbar toolbar;
    private  String UID;
    DatabaseReference dbReference;
    FirebaseAuth authentication;
    FirebaseUser firebaseUser;
    FirebaseAuth.AuthStateListener authStateListener;
    NavigationView navigationView;
    String filePath;

    Uri imageUpload =null;
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
        fab_desc.setSize(1);
        fab_image.setSize(1);


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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame,new FrontPage()).commit();

    }
    public void setUID(String uid){
        UID=uid;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            FirebaseAuth.getInstance().signOut();
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.login).setVisible(true);// set logout and login respectively
            nav_Menu.findItem(R.id.signout).setVisible(false);
            nav_Menu.findItem(R.id.profile).setVisible(false);

            TextView name = (TextView) findViewById(R.id.headText); // remove menu name
            name.setText("");

            fragmentManager.beginTransaction().replace(R.id.content_frame,new LogIn()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public String getFilePath(){
        return filePath;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==1){
               imageUpload = data.getData();
                filePath=getPath(this,imageUpload);
               /* String[] filePathColumn={MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(imageUpload,filePathColumn,null,null,null);
                if(cursor==null){
                    filePath=imageUpload.getPath();
                }
                else{
                    cursor.moveToFirst();

                    // int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    filePath= cursor.getString((columnIndex));
                    cursor.close();

                }*/
                //cursor.moveToFirst();

               // int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
              //  int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
              //  filePath= cursor.getString((columnIndex));
              //  cursor.close();

                final String action = data.getDataString();
                String prefix = "/image";
                String split = action.substring(action.indexOf(prefix)+prefix.length());
                TextView textView = (TextView)findViewById(R.id.uploadTextview);
                textView.setText("image"+split);
            }
        }
        else{
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
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

