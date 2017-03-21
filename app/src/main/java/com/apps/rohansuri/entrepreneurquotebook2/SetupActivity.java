package com.apps.rohansuri.entrepreneurquotebook2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetupActivity extends AppCompatActivity {
    private EditText mNameField;
    private Button mSubmitBtn;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mNameField=(EditText)findViewById(R.id.setupNameField);
        mSubmitBtn=(Button)findViewById(R.id.setupSubmitBtn);
        mProgress=new ProgressDialog(this);
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetupAccount();
            }
        });
    }

    private void startSetupAccount() {

        String user_id=mAuth.getCurrentUser().getUid();

         String name = mNameField.getText().toString().trim();

        if (!TextUtils.isEmpty(name)) {

            mProgress.setMessage("Finishing setup.");
            mProgress.show();
            mDatabaseUsers.child(user_id).child("name").setValue(name);
            mProgress.dismiss();

            Intent mainIntent=new Intent(SetupActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
