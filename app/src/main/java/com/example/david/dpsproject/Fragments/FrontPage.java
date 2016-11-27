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
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by david on 2016-10-25.
 * look into firebase auth refresh token because app will not work if the app is open for more than an hour
 */
public class FrontPage extends Fragment implements FragmentManager.OnBackStackChangedListener {
    View myView;

    ArrayList<Users> users;
    FirebaseAuth authentication;
    DatabaseReference dbReference;
    ArrayList<Post> posts;
    Bundle bundle;
    ProgressDialog pDialog;
    FloatingActionButton fab;
    NavigationView navigationView;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;

    SwipeRefreshLayout refreshLayout;
    String Uid;
    Users tempU;
    Activity mActivity;
    TextView name;
    ListView listView;
    Handler userhandler;
    Runnable userthread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager= getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        mActivity=getActivity();
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
    public void setPostView(){
        posts =new ArrayList<Post>();
        dbReference.child("Sub").child("Soccer").child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                for(DataSnapshot s: dataSnapshot.getChildren()){
                    Post p= s.getValue(Post.class);
                    p.setKey(s.getKey());
                    p.setSubN("Soccer");
                    posts.add(p);
                }
                Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.splashfadeoutleft);
                listView = (ListView)myView.findViewById(R.id.postview);
                MyPostAdapter adapter = new MyPostAdapter(mActivity,posts);
                listView.startAnimation(animation);
                listView.setAdapter(adapter);
                if(refreshLayout!=null)refreshLayout.setRefreshing(false);
                HideProgressDialog();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        Menu nav_Menu = navigationView.getMenu();
        authentication= FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        firebaseUser = authentication.getCurrentUser();
        NavigationView navigationView = (NavigationView)mActivity.findViewById(R.id.nav_view);
        if(firebaseUser!=null) { // find if user is logged in set the title and replace sign in with logout

            nav_Menu.findItem(R.id.login).setVisible(false);
            nav_Menu.findItem(R.id.profile).setVisible(true);
            nav_Menu.findItem(R.id.signout).setVisible(true);
            ((navigation)mActivity).getUser();
           /* final AsyncTask<Void, Void, Void> getuserName = new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    ShowProgressDialog();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        //    boolean keepgoing = true;
                        //Thread.sleep(1000);
                        do {

                            dbReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        tempU = dataSnapshot.getValue(Users.class);
                                        name = (TextView) mActivity.findViewById(R.id.headText);
                                    } catch (DatabaseException e) {
                                        Toast.makeText(mActivity,"something went wrong",Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } while (name != null && tempU!=null);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(Void aVoid) {

                  if(tempU!=null){
                      name.setText(tempU.getUserName());
                      if(tempU.getPicture()!=""&& tempU.getPicture()!=null) {
                          final View layout = (View) mActivity.findViewById(R.id.navPic);
                          byte[] decodedString = Base64.decode(tempU.getPicture(), Base64.DEFAULT);
                          Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                          ((navigation)mActivity).setprofilepic(decodedByte);
                          layout.setBackground(new BitmapDrawable(mActivity.getResources(),decodedByte));
                      }
                      Menu menu=((navigation)mActivity).getSubMenu();
                      if(menu!=null){
                          ArrayList<String> subcat =tempU.getSubcategory();
                        for(int i=0; i<subcat.size();i++){
                            menu.add(R.id.second_nav,Menu.NONE,0,subcat.get(i));
                        }
                      }

                  }
                    else System.out.println("error1");
                  }


            };
            userhandler = new Handler();
            getuserName.execute();
            //userhandler.postDelayed
            userthread= new Runnable() {
                @Override
                public void run() {
                    if(getuserName.getStatus()==AsyncTask.Status.RUNNING){
                        getuserName.cancel(true);
                        HideProgressDialog();
                        Toast.makeText(mActivity,"Error has occured ",Toast.LENGTH_SHORT).show();
                    }
                }
            };//,5000);
            userhandler.postDelayed(userthread,5000);*/
        }
        else{
            nav_Menu.findItem(R.id.login).setVisible(true);
            nav_Menu.findItem(R.id.profile).setVisible(false);
            nav_Menu.findItem(R.id.signout).setVisible(false);
        }
        setPostView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Front Page");
        myView = inflater.inflate(R.layout.front_page,container,false);
        bundle = new Bundle();
        bundle = getArguments();
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        users = new ArrayList<>();

        if(firebaseUser!=null){
            SharedPreferences preferences = getContext().getSharedPreferences("pref",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =preferences.edit().putString("UID",firebaseUser.getUid());
            editor.commit();
        }
        refreshLayout = (SwipeRefreshLayout)myView.findViewById(R.id.swiperefresh);
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

        fab = (FloatingActionButton) getActivity().findViewById(R.id.compose);
        FloatingActionButton fab_image = (FloatingActionButton) getActivity().findViewById(R.id.compse_images);
        FloatingActionButton fab_desc = (FloatingActionButton) getActivity().findViewById(R.id.compse_desc);
        if(fab_image!=null)fab_image.hide();
        if(fab_desc!=null)fab_desc.hide();
        if(fab!=null)fab.show();

        return myView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    //    if(userhandler!=null)userhandler.removeCallbacks(userthread);
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
