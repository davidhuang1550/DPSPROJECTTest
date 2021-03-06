package com.example.david.dpsproject.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.dpsproject.AsyncTask.LoadDefaultPostTask;
import com.example.david.dpsproject.AsyncTask.LoadPostTask;
import com.example.david.dpsproject.Class.Comment;
import com.example.david.dpsproject.Class.Post;
import com.example.david.dpsproject.Class.Users;
import com.example.david.dpsproject.Adapters.MyPostAdapter;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by david on 2016-10-25.
 * look into firebase auth refresh token because app will not work if the app is open for more than an hour
 */
public class FrontPage extends Fragment implements FragmentManager.OnBackStackChangedListener {
    View myView;

    FirebaseAuth authentication;
    DatabaseReference dbReference;
    ArrayList<Post> posts;
    Bundle bundle;
    FloatingActionButton fab;
    NavigationView navigationView;
    FirebaseUser firebaseUser;
    SwipeRefreshLayout refreshLayout;
    Activity mActivity;
    ListView listView;
    Users user;
    String Sub;
    LoadPostTask loadPostTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager= getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        mActivity=getActivity();
    }

    public void setDefaultPostView(){
        posts =new ArrayList<Post>();
        ArrayList<String> sub = new ArrayList<String>();
        sub.add("Jesus");
        sub.add("Soccer");
        sub.add("Uplifting");
        final LoadDefaultPostTask loadDefaultPostTask= new LoadDefaultPostTask(mActivity,dbReference,authentication,firebaseUser,sub,myView,refreshLayout);

        loadDefaultPostTask.execute();
        ((navigation) mActivity).ShowProgressDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(loadDefaultPostTask.getStatus()==AsyncTask.Status.RUNNING){
                        loadDefaultPostTask.cancel(true);
                        ((navigation) mActivity).HideProgressDialog();
                        Toast.makeText(mActivity, "Connection too slow", Toast.LENGTH_SHORT).show();

                }
            }
        },10000);
    }
    public void setPostView(){
        posts =new ArrayList<Post>();
        loadPostTask= new LoadPostTask(mActivity,user,dbReference,authentication,firebaseUser,new ArrayList<String>(),myView,refreshLayout);
            loadPostTask.execute();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(loadPostTask.getStatus()==AsyncTask.Status.RUNNING){
                        if(firebaseUser!=null) {
                            loadPostTask.cancel(true);
                            ((navigation) mActivity).HideProgressDialog();
                            Toast.makeText(mActivity, "Connection too slow", Toast.LENGTH_SHORT).show();

                        }else{

                            setDefaultPostView();
                        }
                    }
                }
            },10000);
    }
    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        authentication = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        firebaseUser = authentication.getCurrentUser();

        if(firebaseUser!=null) {
           // NavigationView navigationView = (NavigationView) mActivity.findViewById(R.id.nav_view);
            // find if user is logged in set the title and replace sign in with logout

                nav_Menu.findItem(R.id.login).setVisible(false);
                nav_Menu.findItem(R.id.profile).setVisible(true);
                nav_Menu.findItem(R.id.signout).setVisible(true);

            ((navigation) mActivity).ShowProgressDialog();
                setPostView();
          //  }
        }
        else if(bundle!=null){
            if (bundle.get("user").equals("true")) {
                setPostView();
            } else if (bundle.get("user").equals("false")) {

                nav_Menu.findItem(R.id.login).setVisible(true);
                nav_Menu.findItem(R.id.profile).setVisible(false);
                nav_Menu.findItem(R.id.signout).setVisible(false);
                setDefaultPostView();
            }
        }

        else{
            setDefaultPostView();
            nav_Menu.findItem(R.id.login).setVisible(true);
            nav_Menu.findItem(R.id.profile).setVisible(false);
            nav_Menu.findItem(R.id.signout).setVisible(false);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        ViewGroup container = (ViewGroup)mActivity.findViewById(R.id.content_frame);
        container.removeAllViews();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity.setTitle("Front Page");
        myView = inflater.inflate(R.layout.front_page,container,false);
        ((navigation)mActivity).hideAllSubscribe();
        refreshLayout = (SwipeRefreshLayout)myView.findViewById(R.id.swiperefresh);
        listView = (ListView)myView.findViewById(R.id.postview);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (listView != null){
                    final Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.splashfadeout);
                    listView.startAnimation(animation);
                    MyPostAdapter tempAdapter= (MyPostAdapter)listView.getAdapter();
                    tempAdapter.clearData();
                    tempAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(true);
                    setPostView();
                }
                else{
                    refreshLayout.setRefreshing(true);
                    setPostView();
                }


            }
        });

        fab = (FloatingActionButton) mActivity.findViewById(R.id.compose);
        FloatingActionButton fab_image = (FloatingActionButton) mActivity.findViewById(R.id.compse_images);
        FloatingActionButton fab_desc = (FloatingActionButton) mActivity.findViewById(R.id.compse_desc);
        if(fab_image!=null)fab_image.hide();
        if(fab_desc!=null)fab_desc.hide();
        if(fab!=null)fab.show();

        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Menu nav_Menu = navigationView.getMenu();
        if(nav_Menu!=null)nav_Menu.findItem(R.id.search).setVisible(true);
        if(fab!=null)fab.show(); // when in front page you must show compose option
    }

    @Override
    public void onBackStackChanged() {
        final DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        if (((navigation) mActivity).getFragmentManager().getBackStackEntryCount() > 0) {
            ((navigation) mActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((navigation) mActivity).onBackPressed();
                }
            });
        } else {
            ((navigation) mActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toggle.syncState();
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }
    }
}
