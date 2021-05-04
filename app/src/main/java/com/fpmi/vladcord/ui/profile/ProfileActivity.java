package com.fpmi.vladcord.ui.profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.fpmi.vladcord.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fpmi.vladcord.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.schwaab.avvylib.AvatarView;

public class ProfileActivity extends AppCompatActivity{
    private Uri selectedImage;
    private String id;
    private FirebaseStorage storage;
    private CircleImageView user_avatar;
    private StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 2;
    private static final int STORAGE_PERMISSION_CODE = 911;

    private TextView profileEmail;
    private TextView profileName;
    private TextView profileBio;
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolBarLayout;
    private FloatingActionButton loadPhoto;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImage = data.getData();
            //imagePath = getRealPathFromURI(selectedImage);
            uploadImage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();




        loadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                showFileChooser();
            }
        });
    }

    void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);



        user_avatar = findViewById(R.id.user_avatar);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_profile);
        loadPhoto = (FloatingActionButton) findViewById(R.id.make_photo);
        profileEmail = findViewById(R.id.profile_email);
        profileName = findViewById(R.id.profile_name);
        profileBio = findViewById(R.id.profile_bio);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        toolbar.setTitle(getIntent().getStringExtra("profileName"));

        id = getIntent().getStringExtra("profileId");
        Picasso.get().load(getIntent().getStringExtra("profileAvatar")).into(user_avatar);
        if(getIntent().getStringExtra("profileBio") != null) {
            profileBio.setText(getIntent().getStringExtra("profileBio"));
        }
        profileName.setText(getIntent().getStringExtra("profileName"));
        profileEmail.setText(getIntent().getStringExtra("profileEmail"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }



    private void showFileChooser() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("outputFormat", Uri.PARCELABLE_WRITE_RETURN_VALUE);
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_image)), PICK_IMAGE_REQUEST);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.has_rights_for_reading), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.hasnt_rights_for_reading), Toast.LENGTH_LONG).show();
            }
        }
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private void uploadImage() {

        if(selectedImage != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.uploading) + "...");
            progressDialog.show();
            StorageReference ref = storageReference.child("Images").child("UsersAvs").child(id);
            ref.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.child("Images").child("UsersAvs").child(id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get()
                                            .load(uri.toString())
                                            .into(user_avatar);
                                    FirebaseDatabase.getInstance().getReference("Users").child(id).child("urlAva").setValue(uri.toString());
                                    //Handle whatever you're going to do with the URL here
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, getString(R.string.failed)+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage(getString(R.string.uploaded) +" "+(int)progress+"%");
                        }
                    });
        }
    }

}
