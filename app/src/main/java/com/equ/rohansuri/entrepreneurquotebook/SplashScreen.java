package com.equ.rohansuri.entrepreneurquotebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
} // SplashScreen class end