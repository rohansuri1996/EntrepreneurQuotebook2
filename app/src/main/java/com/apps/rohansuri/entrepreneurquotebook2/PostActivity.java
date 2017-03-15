package com.apps.rohansuri.entrepreneurquotebook2;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.apps.rohansuri.entrepreneurquotebook2.MainActivity;
import com.apps.rohansuri.entrepreneurquotebook2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PostActivity extends AppCompatActivity {

    private EditText mPostTitle;

    private Button mSubmitBtn;
    private Uri mImageUri=null;
    private ProgressDialog mProgress;


    private ImageButton mSelectImage;
    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");

        mSelectImage = (ImageButton) findViewById(R.id.imageSelect);
        mPostTitle=(EditText)findViewById(R.id.titleField);

        mSubmitBtn=(Button) findViewById(R.id.submitBtn);
        mProgress=new ProgressDialog(this);

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
                mSubmitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startPosting();
                    }
                });
    }
    private void startPosting() {

        mProgress.setMessage("Posting...");

        final String title_val = mPostTitle.getText().toString().trim();

        if (mImageUri != null) {

            mProgress.show();

            StorageReference filepath = mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());
                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUrl=taskSnapshot.getDownloadUrl();

                            DatabaseReference newPost=mDatabase.push();

                            newPost.child("title").setValue(title_val);
                            newPost.child("image").setValue(downloadUrl.toString());

                            mProgress.dismiss();
                            startActivity(new Intent(PostActivity.this,MainActivity.class));

                        }
                    });

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


            super.onActivityResult(resultCode, resultCode, data);
            if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
                  mImageUri = data.getData();


                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

    }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                mSelectImage.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
}}

