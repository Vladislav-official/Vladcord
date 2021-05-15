package com.fpmi.vladcord.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.firebase.ui.auth.AuthUI;
import com.fpmi.vladcord.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Uri selectedImage;
    private String id;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public final Activity activity = getParent();
    private final int PICK_IMAGE_REQUEST = 2;
    private static final int SIGN_IN_CODE = 1;
    private static final int SIGN_IN_CODEIN = 3;
    private static final int STORAGE_PERMISSION_CODE = 911;
    private ProfileViewModel profileViewModel;

    private TextView profileEmail;
    private TextView profileName;
    private TextView profileBio;
    private TextView bioDiscription;
    private CircleImageView user_avatar;

    private Toolbar toolbar;
    private LinearLayout nameChangerLayout;
    private LinearLayout emailChangerLayout;
    private LinearLayout bioChangerLayout;
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
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                recreate();
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        initEventListeners();
        profileViewModel.setStatusOnline();


    }

    void initEventListeners() {
        Activity activity = this;
        nameChangerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameChange();
                Intent intent = new Intent(ProfileActivity.this, NameChangeActivity.class);
                intent.putExtra("profileName", profileName.getText());
                startActivity(intent);
            }
        });
        emailChangerLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                    Dialog dialog = new Dialog(ProfileActivity.this);
                    dialog.setContentView(R.layout.dialog_verification_view);
                    dialog.setTitle("Email Verification");

                    dialog.findViewById(R.id.button_verify).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(activity, new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this,
                                                        "Verification email sent to " + profileEmail.getText(),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ProfileActivity.this,
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(ProfileActivity.this,
                            "You've already verify email",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
        bioChangerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameChange();
                Intent intent = new Intent(ProfileActivity.this, BioChangeActivity.class);
                intent.putExtra("profileBio", profileBio.getText());
                startActivity(intent);

            }
        });
        loadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
            }
        });
    }

    void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);


        user_avatar = findViewById(R.id.user_avatar);
        toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout_profile);
        loadPhoto = (FloatingActionButton) findViewById(R.id.make_photo);
        profileEmail = findViewById(R.id.profile_email);
        profileName = findViewById(R.id.profile_name);
        profileBio = findViewById(R.id.profile_bio);
        bioDiscription = findViewById(R.id.change_bio);
        nameChangerLayout = findViewById(R.id.name_changer);
        emailChangerLayout = findViewById(R.id.email_changer);
        bioChangerLayout = findViewById(R.id.bio_changer);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileViewModel = new ProfileViewModel(id, profileEmail, profileName, profileBio,
                bioDiscription, user_avatar, toolbar);
        profileViewModel.getCurUser();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int id = item.getItemId();
        if (id == R.id.signOut) {
            if (hasConnection(getApplicationContext())) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseDatabase.getInstance().getReference("Users/".concat
                            (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status")
                            .setValue(getString(R.string.last_seen) + (DateFormat.format("HH:mm", (new Date().getTime())))
                                    + " " + DateFormat.format("dd:MM", (new Date().getTime())));
                }
                FirebaseAuth.getInstance().signOut();
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).build(), SIGN_IN_CODE);
            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
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
        String[] proj = {MediaStore.Images.Media.DATA};
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
                showFileChooser();
            } else {
                Toast.makeText(this, getString(R.string.hasnt_rights_for_reading), Toast.LENGTH_LONG).show();
            }
        }
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showFileChooser();
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, getString(R.string.hasnt_rights_for_reading), Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private void uploadImage() {
        if (hasConnection(getApplicationContext())) {
            if (selectedImage != null) {
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
                                Toast.makeText(ProfileActivity.this, getString(R.string.failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage(getString(R.string.uploaded) + " " + (int) progress + "%");
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Photo will be download when connection will be available", Toast.LENGTH_SHORT).show();
        }
    }

    public void nameChange() {
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onPause() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            profileViewModel.setStatusOffline(getString(R.string.last_seen));
        }
        super.onPause();
    }

}
