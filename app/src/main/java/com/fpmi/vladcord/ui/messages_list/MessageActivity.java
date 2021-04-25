package com.fpmi.vladcord.ui.messages_list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.fpmi.vladcord.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MessageActivity extends AppCompatActivity implements
        Application.ActivityLifecycleCallbacks {
private String friendId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("Status").setValue("Online");
        String friendId = getIntent().getStringExtra("friendId");
        String friendName = getIntent().getStringExtra("friendName");
        this.friendId = friendId;
        setTitle(friendName);
        setupActionBar();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MessageFragment.newInstance(friendId))
                    .commitNow();
        }
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }



    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("current_user", userid);
        editor.apply();
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser(friendId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentUser("none");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}