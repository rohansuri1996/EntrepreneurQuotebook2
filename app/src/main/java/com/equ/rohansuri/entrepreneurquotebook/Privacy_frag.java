package com.equ.rohansuri.entrepreneurquotebook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;


public class Privacy_frag extends Fragment {
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        //View v=inflater.inflate(R.layout.privacy_policy, parent, false);
        final View v = inflater.inflate(R.layout.privacy_policy, parent, false);
        //final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Loading...",true);
        //WebView webview = (WebView) v.findViewById(R.id.webview_a);
        // webview.loadUrl("https://sites.google.com/view/equ/privacy-policy");
        progressBar = (ProgressBar) v.findViewById(R.id.progressbar);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);

        webView = (WebView) v.findViewById(R.id.webview_a);
        //webView.setWebViewClient(new WebViewClient());
        //progressBarT4.setVisibility(View.VISIBLE);
        webView.loadUrl("https://sites.google.com/view/equ/privacy-policy");

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);


                // Your custom code.
                //getActivity().setTitle(getString(R.string.loading)); //changes app title monetarily

                progressBar.setVisibility(View.VISIBLE);

                progressBar.bringToFront();
                progressBar.setProgress(progress);

                // Return the app name after finish loading
                if(progress == 100) {
                    getActivity().setTitle(getString(R.string.privacy));
                    progressBar.setVisibility(View.GONE);
                }



            }
        });





        return v;
    }



}




