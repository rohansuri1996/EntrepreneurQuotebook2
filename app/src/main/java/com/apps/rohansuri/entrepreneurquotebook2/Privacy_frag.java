package com.apps.rohansuri.entrepreneurquotebook2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by ROHAN SURI on 15-02-2017.
 */

public class Privacy_frag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        //View v=inflater.inflate(R.layout.privacy_policy, parent, false);
        View v = inflater.inflate(R.layout.privacy_policy, parent, false);
        WebView myWebView = (WebView) v.findViewById(R.id.webview);
        myWebView.loadUrl("https://sites.google.com/view/equ/privacy-policy");
        return v;
    }
}
