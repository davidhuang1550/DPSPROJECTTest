package com.example.david.dpsproject.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.dpsproject.Class.Post;
import com.example.david.dpsproject.Dialog.PleaseLogin;
import com.example.david.dpsproject.R;
import com.example.david.dpsproject.Fragments.postview;
import com.example.david.dpsproject.navigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by david on 2016-11-03.
 */
public class MyPostAdapter extends BaseAdapter {
    private ArrayList<Post> posts;
    private Context context;
    private FirebaseAuth authentication;
    private DatabaseReference dbReference;
    private FirebaseUser firebaseUser;
    private  ProgressDialog pDialog;

   public MyPostAdapter(Activity Activity, ArrayList<Post> p){
       posts=p;
       context=Activity;
       authentication= FirebaseAuth.getInstance(); // get instance of my firebase console
       dbReference = FirebaseDatabase.getInstance().getReference(); // access to database
    }
    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    public void clearData(){
       /* try{
            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.splashfadeout);
            v.startAnimation(animation);
            posts.remove(position);
            notifyDataSetChanged();
           // animation.cancel();
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }*/
        posts.clear();

    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View row;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.postlist,null);

        TextView tView = (TextView)row.findViewById(R.id.PostT);
        TextView subcatView = (TextView)row.findViewById(R.id.sub_cat_view);
        TextView TimeStamp =(TextView)row.findViewById(R.id.timestamp);
        final ImageView bookmark = (ImageView)row.findViewById(R.id.bookmark);


        tView.setText(posts.get(i).getTitle());
        if(posts.get(i).getTimestamp()!=null){
            long temp = (System.currentTimeMillis()/1000)-(posts.get(i).getTimestamp());

            if(temp<60)TimeStamp.setText(temp+" Second(s) ago");
            else if((temp/60)<60)TimeStamp.setText(temp/60+" Minute(s) ago");
            else if((temp/60)/60<24) TimeStamp.setText((temp/60)/60+" Hour(s) ago");
            else if(temp/60/60/24<31)TimeStamp.setText((temp/60)/60/24+" Day(s) ago");
            else if(temp/60/60/24/30<13)TimeStamp.setText((temp/60)/60/24/30+" Month(s) ago");
            else TimeStamp.setText((temp/60)/60/24/30/12+" Year(s) ago");

        }
        subcatView.setText(posts.get(i).getSubN());

        firebaseUser = authentication.getCurrentUser();
        if(firebaseUser!=null){
            final ArrayList<Post> ptemp = new ArrayList<Post>();
            dbReference.child("Users").child(firebaseUser.getUid()).child("Bookmarks").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                            String temps = s.getKey();
                            if(temps.equals(posts.get(i).getSubN())) {
                                for (DataSnapshot temp : s.getChildren()) {
                                    String post = temp.getValue(String.class);
                                    if (posts.get(i).getKey().equals(post)) {
                                        bookmark.setImageResource(R.drawable.bookmarkchecked);
                                    }
                                }
                            }
                        }
                    }catch (DatabaseException e){
                        e.printStackTrace();
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser = authentication.getCurrentUser();
                if(firebaseUser!=null){
                    final boolean check;
                    if(bookmark.getDrawable().getConstantState()== context.getResources().getDrawable(R.drawable.bookmarkchecked).getConstantState()){
                        bookmark.setImageResource(R.drawable.bookmarkunchecked);
                        check=false;
                    }
                    else{
                        bookmark.setImageResource(R.drawable.bookmarkchecked);
                        check=true;
                    }
                    final ArrayList<String> subString= new ArrayList<String>();
                    dbReference.child("Users").child(firebaseUser.getUid()).child("Bookmarks").child(posts.get(i).getSubN()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            subString.clear();
                            for(DataSnapshot s: dataSnapshot.getChildren()) {
                                String p = s.getValue(String.class);
                                subString.add(p);
                            }
                            if(check==true)subString.add(posts.get(i).getKey());
                            else{
                                subString.remove(posts.get(i).getKey());
                            }
                            dbReference.child("Users").child(firebaseUser.getUid()).child("Bookmarks").child(posts.get(i).getSubN()).setValue(subString);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //databaseError.toString()
                        }
                    });
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putString("Message","You must be logged in to bookmark");
                    PleaseLogin pleaseLogin = new PleaseLogin();
                    pleaseLogin.setArguments(bundle);
                    pleaseLogin.show(((Activity)context).getFragmentManager(),"Alert Dialog Fragment");
                }

            }
        });

        row.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();


              //  FragmentManager fragmentManager = ((Activity)context).getFragmentManager();
                postview pV = new postview();
                bundle.putSerializable("Post_Object", (Serializable) posts.get(i));
                bundle.putString("UID",posts.get(i).getPosterId());
                pV.setArguments(bundle);
               // fragmentManager.beginTransaction().replace(R.id.content_frame, pV).addToBackStack("Posts").commit();

                FragmentTransaction transaction= ((navigation)context).getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.enter_anim,R.animator.exit_anim,R.animator.enter_anim_back,R.animator.exit_anime_back);
                transaction.add(R.id.content_frame,pV).addToBackStack("Posts").commit();

            }
        });
        return row;

    }
}
