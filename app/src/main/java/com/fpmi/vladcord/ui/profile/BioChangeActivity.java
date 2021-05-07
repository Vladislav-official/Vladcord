package com.fpmi.vladcord.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fpmi.vladcord.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class BioChangeActivity extends AppCompatActivity {

    private ImageView changeBio;
    private EditText editText;
    private String bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_change);
        Toolbar toolbar = findViewById(R.id.toolbar);
        changeBio = findViewById(R.id.image_change_bio);
        editText = findViewById(R.id.edit_bio);
        bio = getIntent().getStringExtra("profileBio");
        if(!bio.equals("")){
            editText.setText(bio);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        changeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   changeBio(editText.getText().toString());
                   finish();
            }
        });
    }
    public void changeBio(String name) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("bio").setValue(name);
    }
}