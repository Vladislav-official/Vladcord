package com.fpmi.vladcord;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.messages_list.Message;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.loader.content.CursorLoader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.fahmisdk6.avatarview.AvatarView;


public class MainActivity extends AppCompatActivity {

    private String imagePath;
    private ImageView imgview;
    private Uri selectedImage;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 2;
    private static final int STORAGE_PERMISSION_CODE = 911;


    private AvatarView user_avatar;
    private ProgressBar progressBar;
    private TextView user_name;
    private TextView user_email;
    private AppBarConfiguration mAppBarConfiguration;
    private static final int SIGN_IN_CODE = 1;
    private NavigationView navigationView;
    private DrawerLayout activity_main;
    private DrawerLayout drawer;
    private  User user = null;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, getString(R.string.you_logged), Snackbar.LENGTH_LONG).show();
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    this.setAndCheckCurUser();
                }
            } else {
                Snackbar.make(activity_main, getString(R.string.you_not_logged), Snackbar.LENGTH_LONG).show();
                user = null;
                finish();
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            selectedImage = data.getData();
            imagePath = getRealPathFromURI(selectedImage);
            uploadImage();



        }

    }

    private void uploadImage() {

        if(selectedImage != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            progressBar.setVisibility(View.VISIBLE);
            user_avatar.setVisibility(View.GONE);
            StorageReference ref = storageReference.child("Images").child("UsersAvs").child(user.getuID());
                       ref.putFile(selectedImage)
                               .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                   @Override
                                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                       storageReference.child("Images").child("UsersAvs").child(user.getuID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                           @Override
                                           public void onSuccess(Uri uri) {
                                               user_avatar.bind("userAva", uri.toString());
                                               user_avatar.setVisibility(View.VISIBLE);
                                               progressBar.setVisibility(View.GONE);
                                               FirebaseDatabase.getInstance().getReference("Users").child(user.getuID()).child("urlAva").setValue(uri.toString());
                                               //Handle whatever you're going to do with the URL here
                                           }
                                       });
                                       progressDialog.dismiss();
                                       Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                   }
                               })
                               .addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       progressDialog.dismiss();
                                       Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                               })
                               .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                   @Override
                                   public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                       double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                               .getTotalByteCount());
                                       progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                   }
                               });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        activity_main = findViewById(R.id.activity_main);
        navigationView = findViewById(R.id.nav_view);
        progressBar = navigationView.getHeaderView(0).findViewById(R.id.progress_bar);
        user_name = navigationView.getHeaderView(0).findViewById(R.id.user_main_name);
        user_email = navigationView.getHeaderView(0).findViewById(R.id.user_main_email);
        user_avatar = navigationView.getHeaderView(0).findViewById(R.id.user_main_avatar);

        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
                showFileChooser();
                user_avatar.setVisibility(View.GONE);
            }
        });

        drawer = new DrawerLayout(this.getBaseContext());
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).build(), SIGN_IN_CODE);
        } else {
            this.getCurUser();
            Snackbar.make(activity_main, getString(R.string.you_logged), Snackbar.LENGTH_LONG).show();
            FirebaseDatabase.getInstance().getReference("Users/".concat
                    (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status").setValue("Online");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.activity_main);





        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_friends, R.id.nav_users)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void getCurUser(){
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userD = snapshot.getValue(User.class);
                        if(userD != null) {
                            user = userD;
                            user_name.setText(user.getName());
                            user_email.setText(user.getEmail());
                            user_avatar.bind("userAva", user.getUrlAva());
                            user_name.setVisibility(View.VISIBLE);
                            user_email.setVisibility(View.VISIBLE);
                            user_avatar.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public void setAndCheckCurUser(){
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userD = snapshot.getValue(User.class);
                        if(userD != null) {
                            user = userD;

                        }
                        else{
                            user = new User(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    "https://www.freepngimg.com/thumb/facebook/62681-flat-icons-face-computer-design-avatar-icon.png", "Online");
                            FirebaseDatabase.getInstance().getReference("Users/".concat
                                    (FirebaseAuth.getInstance().getCurrentUser().getUid())).setValue(user);
                        }
                        user_name.setText(user.getName());
                        user_email.setText(user.getEmail());
                        user_avatar.bind("userAva", user.getUrlAva());
                        user_name.setVisibility(View.VISIBLE);
                        user_email.setVisibility(View.VISIBLE);
                        user_avatar.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int id = item.getItemId();
        if (id == R.id.signOut){
            FirebaseAuth.getInstance().signOut();
            onRestart();
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).build(), SIGN_IN_CODE);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
    }


    @Override
    protected void onPause() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference("Users/".concat
                    (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status")
                    .setValue(getString(R.string.last_seen) + (DateFormat.format("HH:mm", (new Date().getTime())))
                            + " " + DateFormat.format("dd:MM", (new Date().getTime())));
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference("Users/".concat
                    (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status")
                    .setValue(getString(R.string.last_seen) + (DateFormat.format("HH:mm", (new Date().getTime())))
                            + " " + DateFormat.format("dd:MM", (new Date().getTime())));
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status").setValue("Online");}
        super.onResume();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
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
}