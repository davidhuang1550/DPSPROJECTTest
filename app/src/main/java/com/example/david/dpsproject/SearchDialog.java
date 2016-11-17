package com.example.david.dpsproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by xlhuang3 on 11/8/2016.
 */
public class SearchDialog extends DialogFragment {


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle("Search");

//        alertDialog.setMessage("You must be logged in to create post");

        final EditText editText = new EditText(getActivity());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(layoutParams);
        alertDialog.setView(editText);

        alertDialog.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!(editText.getText().toString().equals(""))) {
                    Searchpage searchpage = new Searchpage();
                    Bundle bundle = new Bundle();
                    bundle.putString("Sub", editText.getText().toString());
                    searchpage.setArguments(bundle);


                    getFragmentManager().beginTransaction().replace(R.id.content_frame, searchpage).commit();
                }
                else{
                    Toast.makeText(getActivity(),"Nothing was Searched",Toast.LENGTH_SHORT).show();
                   // editText.setHint("Intput Some Characters");
                  //  alertDialog.setView(editText);
                }
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
