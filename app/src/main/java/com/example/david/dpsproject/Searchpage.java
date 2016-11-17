package com.example.david.dpsproject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by xlhuang3 on 11/8/2016.
 */
public class Searchpage extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.searchpage,container,false);

        TextView textView = (TextView)myView.findViewById(R.id.tempview);
        Bundle b = getArguments();
        textView.setText(b.getString("Sub"));

        return myView;
    }

}
