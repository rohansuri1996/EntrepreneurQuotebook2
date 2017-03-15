package com.apps.rohansuri.entrepreneurquotebook2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.apps.rohansuri.entrepreneurquotebook2.MainActivity;
import com.apps.rohansuri.entrepreneurquotebook2.R;


public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
} // SplashScreen class end