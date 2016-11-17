package com.example.david.dpsproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URI;

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
                   PleaseLogin pleaseLogin = new PleaseLogin();
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

            TextView name = (TextView) findViewById(R.id.headText); // remove menu name
            name.setText("");

            fragmentManager.beginTransaction().replace(R.id.content_frame,new LogIn()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==1){
               imageUpload = data.getData();
                String[] filePathColumn={MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(imageUpload,filePathColumn,null,null,null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath= cursor.getString((columnIndex));
                cursor.close();
                Bitmap bitmap;
             //   try {
          //          bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUpload));
         //       }catch (IOException ie){

         //       }





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
}

