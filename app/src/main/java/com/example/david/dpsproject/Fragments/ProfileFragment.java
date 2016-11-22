package com.example.david.dpsproject.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.david.dpsproject.Adapters.MyPostAdapter;
import com.example.david.dpsproject.Class.Post;
import com.example.david.dpsproject.Class.Posts;
import com.example.david.dpsproject.Class.Sub;
import com.example.david.dpsproject.Class.SubName;
import com.example.david.dpsproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by david on 2016-11-16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    private View myView;
    private Button History;
    private Button Bookmarks;
    private Button Upload;

    FirebaseAuth authentication;
    DatabaseReference dbReference;
    FirebaseUser firebaseUser;

    ArrayList<Post> ptemp;
    private ListView listView;
    private Activity mActivity;
    ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity= getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.profile_fragment,container,false);

        History = (Button)myView.findViewById(R.id.selfposts);
        Bookmarks = (Button)myView.findViewById(R.id.bookmarks);
        Upload = (Button)myView.findViewById(R.id.uploadprofile);

        authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
        dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
        firebaseUser = authentication.getCurrentUser();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.compose);
        if(fab!=null)fab.hide();

        History.setOnClickListener(this);
        Bookmarks.setOnClickListener(this);
        Upload.setOnClickListener(this);

        return myView;
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selfposts:
                ptemp = new ArrayList<Post>();
                final AsyncTask<Void,Void,Void> getProfilePost = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        ShowProgressDialog();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        try{
                        //    boolean keepgoing = true;
                            do {

                                dbReference.child("Users").child(firebaseUser.getUid()).child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (DataSnapshot s : dataSnapshot.getChildren()) {



                                            for (DataSnapshot temp : s.getChildren()) {
                                                dbReference.child("Sub").child(s.getKey()).child("posts").child(temp.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {


                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        try {
                                                            Post post = dataSnapshot.getValue(Post.class);
                                                            if (post != null) {

                                                                ptemp.add(post);
                                                            }

                                                        } catch (DatabaseException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                        /*if (ptemp.size() != 0) {
                                            listView = (ListView) myView.findViewById(R.id.profile_list_view);
                                            MyPostAdapter adapter = new MyPostAdapter(mActivity, ptemp);
                                            listView.setAdapter(adapter);
                                        }*/
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                Thread.sleep(1000);
                            }while(ptemp.size() == 0);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        return  null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        HideProgressDialog();
                        listView = (ListView) myView.findViewById(R.id.profile_list_view);
                        MyPostAdapter adapter = new MyPostAdapter(mActivity, ptemp);
                        listView.setAdapter(adapter);
                    }
                };

                        //   listView = (ListView)myView.findViewById(R.id.profile_list_view);
                        //  MyPostAdapter adapter = new MyPostAdapter(getActivity(),posts);
                        //  listView.setAdapter(adapter);
                Handler handler = new Handler();
                getProfilePost.execute();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getProfilePost.getStatus()==AsyncTask.Status.RUNNING){
                            getProfilePost.cancel(true);
                            HideProgressDialog();
                            Toast.makeText(mActivity,"Nothing was found",Toast.LENGTH_SHORT).show();
                        }
                    }
                },10000);
              //  getProfilePost.execute();
                break;
            case R.id.bookmarks:

                break;
            case R.id.uploadprofile:

                break;
        }
    }
}
