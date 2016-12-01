package com.example.david.dpsproject.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.icu.math.BigDecimal;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.dpsproject.R;

/**
 * Created by david on 2016-11-23.
 */
public class VoteBarFrame extends Fragment {
    private float yes;
    private float no;
    private View myView;
    private ProgressBar pBar;
    private TextView textView;
    int pStatus = 0;
    int spStatus=0;
    private TextView totalVote;
    private Handler handler = new Handler();
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.circlebarlayout, container, false);
        Bundle bundle = getArguments();
        textView=(TextView)myView.findViewById(R.id.percentagedesc);
        totalVote=(TextView)myView.findViewById(R.id.totalvoted);
        if(bundle!=null){
            yes=bundle.getInt("yes",0);
            no=bundle.getInt("no",0);
            //final int total;
            try {
               final float total = (yes / (yes + no))*100;
                pBar = (ProgressBar)myView.findViewById(R.id.progressBar1);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        while ((pStatus < total && pStatus<=100)||spStatus<=100) {

                            handler.post(new Runnable() {

                                @Override
                                public void run() {

                                    pBar.setProgress(pStatus);
                                    pBar.setSecondaryProgress(spStatus);
                                    textView.setText(pStatus+"%");
                                }
                            });
                            try {
                                // Sleep for 10 milliseconds.
                                // Just to display the progress slowly
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(pStatus<total)pStatus++;
                            if(spStatus<=100)spStatus++;
                        }


                    }
                }).start();
                int yesint=bundle.getInt("yes",0);
                int noint=bundle.getInt("no",0);
                int tot=noint+yesint;

                totalVote.setText(tot+" person(s) voted.");
            }catch (ArithmeticException e){
                Toast.makeText(mActivity,"An error has occurd",Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(mActivity,"An error has occurd",Toast.LENGTH_SHORT).show();
        }

        return myView;
    }
   /* public void onDestroy() {
        ViewGroup container = (ViewGroup)mActivity.findViewById(R.id.content_frame);
        container.removeAllViews();
        super.onDestroy();
    }*/

}
