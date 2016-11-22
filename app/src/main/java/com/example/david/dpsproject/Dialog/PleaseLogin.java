package com.example.david.dpsproject.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.david.dpsproject.Fragments.LogIn;
import com.example.david.dpsproject.R;

/**
 * Created by david on 2016-11-07.
 */
public class PleaseLogin extends DialogFragment {


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        Bundle bundle =getArguments();

        alertDialog.setTitle("Please Log In");
        if(bundle!=null){
            alertDialog.setMessage(bundle.getString("Message","Please Login"));
        }
        else{
            alertDialog.setMessage(bundle.getString("Message","Please Login"));
        }

        alertDialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame,new LogIn()).commit();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setShowsDialog(false);
                dismiss();
            }
        });
        return alertDialog.create();
    }


}
