package com.equ.rohansuri.entrepreneurquotebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by ROHAN SURI on 19-03-2017.
 */

public class paid_promotions extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View v=inflater.inflate(R.layout.paid_promotion, parent, false);

       final Button txt = (Button) v.findViewById(R.id.emailBtn);
        txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tigonstudios1@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.promotion));
                //intent.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), R.string.rate_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }



}