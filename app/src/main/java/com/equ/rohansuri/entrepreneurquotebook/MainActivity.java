package com.equ.rohansuri.entrepreneurquotebook;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;

    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;//for login
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    //Toast.makeText(MainActivity.this, "onAuthStateChanged", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                } else {
                    //Toast.makeText(MainActivity.this, "onAuthStateChanged Else Statement", Toast.LENGTH_SHORT).show();
                }
            }
        };
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);
        mBlogList = (RecyclerView) findViewById(R.id.blog_List);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLayoutManager.setReverseLayout(true); // THIS ALSO SETS setStackFromBottom to true
        mBlogList.setLayoutManager(mLayoutManager);
        mLayoutManager.setStackFromEnd(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1832954170852232/3364450909");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("8AB8A07791626C0E0F227AA014F0D980")
                .addTestDevice("11E9DD4E27F18CF68FA8F6584F4B1D67") //Avi's phone
                .build();
        mAdView.loadAd(adRequest);

        SharedPreferences preferences = getSharedPreferences("progress", MODE_PRIVATE);
        int appUsedCount = preferences.getInt("appUsedCount", 0);
        appUsedCount++;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("appUsedCount", appUsedCount);
        editor.apply();

        if (appUsedCount == 10 || appUsedCount == 20 || appUsedCount == 40 || appUsedCount == 60 || appUsedCount == 80 || appUsedCount == 200) {
            AskForRating();
        } else {
            //finish();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSION_REQUEST = 0;
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else { //do nothing
        }
        checkUserExist();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.mShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareItem(model.getImage());
                    }
                });
            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        //Toast.makeText(MainActivity.this, "UserExit" + user_id, Toast.LENGTH_SHORT).show();
                        // Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        //setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Toast.makeText(MainActivity.this, "Canceled" + user_id, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
//            postDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void postDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.DialogeTheme);
        alert.setTitle(R.string.coming);
        alert.setIcon(R.drawable.ic_menu_camera);
        alert.setMessage(R.string.post_feature);
        alert.setPositiveButton(R.string.ok, new Dialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.home_nav:
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), R.string.uhome, Toast.LENGTH_SHORT).show();
                break;
            case R.id.ads_nav:
                Toast.makeText(getApplicationContext(), R.string.ads_toast, Toast.LENGTH_SHORT).show();
                fragment = new WhyAds_frag();
                break;
            case R.id.about_nav:
                Toast.makeText(getApplicationContext(), R.string.about_toast, Toast.LENGTH_SHORT).show();
                fragment = new About_frag();
                break;
            case R.id.share_nav:
                Toast.makeText(getApplicationContext(), R.string.share_toast, Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent();
                intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_toast));
                startActivity(Intent.createChooser(intent2, "Share via"));
                break;
            case R.id.rate_nav:
                Toast.makeText(getApplicationContext(), R.string.rate_toast, Toast.LENGTH_SHORT).show();
                rateApp();
                //AskForRating();
                break;
            case R.id.logout_nav:
                Toast.makeText(getApplicationContext(), R.string.logout_toast, Toast.LENGTH_SHORT).show();
                logout();
                break;
            case R.id.promotion_nav:
                Toast.makeText(getApplicationContext(), R.string.promotion_toast, Toast.LENGTH_SHORT).show();
                fragment = new paid_promotions();
                break;
            case R.id.privacy_nav:
                Toast.makeText(getApplicationContext(), R.string.privact_toast, Toast.LENGTH_SHORT).show();
                fragment = new Privacy_frag();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void logout() {
        //firebae sign out
        mAuth.signOut();
        //FirebaseAuth.getInstance().signOut();


    }

    private void AskForRating() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this,R.style.DialogeTheme);
        alert.setTitle(R.string.rate);
        alert.setIcon(R.drawable.ic_star_border_black_18dp);
        alert.setMessage(R.string.rate_feed);
        alert.setPositiveButton(R.string.rate_b, new Dialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        alert.setNegativeButton(R.string.l, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        alert.show();

    }

    public void rateApp() {
        try {
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        } catch (ActivityNotFoundException e1) {
            Toast.makeText(this, R.string.rate_error, Toast.LENGTH_SHORT).show();
        }
    }

    // Can be triggered by a view event such as a button press
    public void shareItem(String url) {
        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharedvia));
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                startActivity(Intent.createChooser(i, getString(R.string.share_img)));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Keep
    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Button mShareButton;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mShareButton = (Button) mView.findViewById(R.id.btn_share);
        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setImage(final Context ctx, final String image) {

            final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);

            Picasso.with(ctx)
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(post_image, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx)
                                    .load(image)
                                    .error(R.drawable.header)
                                    .placeholder(R.drawable.progress_animation)
                                    .into(post_image);
                        }

                    });
        }
    }
}



