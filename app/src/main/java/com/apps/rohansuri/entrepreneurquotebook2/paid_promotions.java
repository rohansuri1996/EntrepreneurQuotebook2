package com.apps.rohansuri.entrepreneurquotebook2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ROHAN SURI on 19-03-2017.
 */

public class paid_promotions extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View v=inflater.inflate(R.layout.paid_promotion, parent, false);
        return v;
    }
}