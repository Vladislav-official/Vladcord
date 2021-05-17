package com.fpmi.vladcord.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fpmi.vladcord.R;
import com.google.firebase.auth.FirebaseAuth;

public class NameChangeActivity extends AppCompatActivity {

    private ImageView changeName;
    private EditText editText;
    private String name;
    private ProfielModel profielModel = new ProfielModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profielModel.setStatusOnline();
        setContentView(R.layout.activity_name_change);
        Toolbar toolbar = findViewById(R.id.toolbar);
        changeName = findViewById(R.id.image_change_name);
        editText = findViewById(R.id.edit_name);
        name = getIntent().getStringExtra("profileName");
        if (!name.equals("")) {
            editText.setText(name);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().equals("")) {
                    if (editText.getText().length() < 5) {
                        Toast.makeText(NameChangeActivity.this, "Too short name", Toast.LENGTH_SHORT).show();
                    } else if (editText.getText().length() > 21) {
                        Toast.makeText(NameChangeActivity.this, "Too long name", Toast.LENGTH_SHORT).show();
                    } else {
                        profielModel.changeName(editText.getText().toString());
                        getParent().recreate();
                        finish();
                    }
                } else {
                    Toast.makeText(NameChangeActivity.this, "Empty field", Toast.LENGTH_SHORT).show();
                }
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