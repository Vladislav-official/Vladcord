package com.fpmi.vladcord.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fpmi.vladcord.R;
import com.google.firebase.auth.FirebaseAuth;

public class BioChangeActivity extends AppCompatActivity {

    private ImageView changeBio;
    private EditText editText;
    private String bio;
    private ProfielModel profielModel = new ProfielModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profielModel.setStatusOnline();
        setContentView(R.layout.activity_bio_change);
        Toolbar toolbar = findViewById(R.id.toolbar);
        changeBio = findViewById(R.id.image_change_bio);
        editText = findViewById(R.id.edit_bio);
        bio = getIntent().getStringExtra("profileBio");
        if (!bio.equals("")) {
            editText.setText(bio);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        changeBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profielModel.changeBio(editText.getText().toString());
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            profielModel.setStatusOffline(getString(R.string.last_seen));
        }
        super.onPause();
    }

}