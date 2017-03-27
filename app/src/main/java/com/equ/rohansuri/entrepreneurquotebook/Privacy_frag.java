package com.equ.rohansuri.entrepreneurquotebook;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class Privacy_frag extends Fragment {
    private WebView webView;
    private ProgressBar progressBarT4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        //View v=inflater.inflate(R.layout.privacy_policy, parent, false);
         final View v = inflater.inflate(R.layout.privacy_policy, parent, false);
        //final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Loading...",true);
        //WebView webview = (WebView) v.findViewById(R.id.webview_a);
       // webview.loadUrl("https://sites.google.com/view/equ/privacy-policy");
        progressBarT4 = (ProgressBar) v.findViewById(R.id.progressbar);

        webView = (WebView) v.findViewById(R.id.webview_a);
        //webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                view.setVisibility(View.VISIBLE);
                progressBarT4.setVisibility(View.GONE);
                //you might need this
                view.bringToFront();
            }
            @Override
            public void onPageStarted(WebView view, String url,  Bitmap favicon) {
                progressBarT4.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);//hide the webview that will display your dialog

            }
        });

        //progressBarT4.setVisibility(View.VISIBLE);
        webView.loadUrl("https://sites.google.com/view/equ/privacy-policy");



        return v;
    }



}
