package com.fpmi.vladcord.ui.profile;

import android.os.Bundle;

import com.google.android.gms.auth.api.Auth;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fpmi.vladcord.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class NameChangeActivity extends AppCompatActivity {

    private ImageView changeName;
    private EditText editText;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusOnline();
        setContentView(R.layout.activity_name_change);
        Toolbar toolbar = findViewById(R.id.toolbar);
        changeName = findViewById(R.id.image_change_name);
        editText = findViewById(R.id.edit_name);
        name = getIntent().getStringExtra("profileName");
        if(!name.equals("")){
            editText.setText(name);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().equals("")){
                    if(editText.getText().length() < 5){
                        Toast.makeText(NameChangeActivity.this, "Too short name", Toast.LENGTH_SHORT).show();
                    } else if (editText.getText().length() > 21){
                        Toast.makeText(NameChangeActivity.this, "Too long name", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        changeName(editText.getText().toString());
                        finish();
                    }
                }
                else{
                    Toast.makeText(NameChangeActivity.this, "Empty field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void changeName(String name) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("name").setValue(name);
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(name);
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(builder.build());

    }
    @Override
    protected void onPause() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            setStatusOffline();
        }
        super.onPause();
    }
    public void setStatusOnline(){
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status").setValue("Online");
    }
    public void setStatusOffline(){
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status")
                .setValue(getString(R.string.last_seen) + " " + (DateFormat.format("HH:mm", (new Date().getTime())))
                        + " " + DateFormat.format("dd:MM", (new Date().getTime())));
    }
}