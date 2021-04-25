package com.fpmi.vladcord;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.messages_list.Message;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;
import java.util.List;

import me.fahmisdk6.avatarview.AvatarView;


public class MainActivity extends AppCompatActivity {

    private AvatarView user_avatar;
    private TextView user_name;
    private TextView user_email;
    private AppBarConfiguration mAppBarConfiguration;
    private static final int SIGN_IN_CODE = 1;
    private NavigationView navigationView;
    private DrawerLayout activity_main;
    private DrawerLayout drawer;
    private static User user;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            user = getCurUser();
                            if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue() == null && user != null){
                                FirebaseDatabase.getInstance().getReference("Users/".concat
                                        (FirebaseAuth.getInstance().getCurrentUser().getUid())).setValue(getCurUser());

                                FirebaseDatabase.getInstance().getReference("Users/".concat
                                        (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("Status").setValue("Online");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                user_name = navigationView.getHeaderView(0).findViewById(R.id.user_main_name);
                user_email = navigationView.getHeaderView(0).findViewById(R.id.user_main_email);
                user_avatar = navigationView.getHeaderView(0).findViewById(R.id.user_main_avatar);

                user_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                user_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                user_avatar.bind("userAvatar", "https://im0-tub-by.yandex.net/i?id=37805a40978d4f627f37dafa996381a8&n=13");
            } else {
                Snackbar.make(activity_main, "Вы не авторизованы", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity_main = findViewById(R.id.activity_main);
        navigationView = findViewById(R.id.nav_view);
        drawer = new DrawerLayout(this.getBaseContext());
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
            user = null;
        } else {
            Snackbar.make(activity_main, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
            FirebaseDatabase.getInstance().getReference("Users/".concat
                    (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("Status").setValue("Online");
            user_name = navigationView.getHeaderView(0).findViewById(R.id.user_main_name);
            user_email = navigationView.getHeaderView(0).findViewById(R.id.user_main_email);
            user_avatar = navigationView.getHeaderView(0).findViewById(R.id.user_main_avatar);

            user = getCurUser();

            user_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            user_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            user_avatar.bind("userAvatar", "https://im0-tub-by.yandex.net/i?id=37805a40978d4f627f37dafa996381a8&n=13");
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

    public User getCurUser(){
        return new User(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                Uri.parse(
                        "https://im0-tub-by.yandex.net/i?id=37805a40978d4f627f37dafa996381a8&n=13")
                        .toString(), "Online");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int id = item.getItemId();
        if (id == R.id.signOut){
            FirebaseAuth.getInstance().signOut();
            onRestart();
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);

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
                    (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("Status")
                    .setValue("Last seen " + (DateFormat.format("HH:mm", (new Date().getTime())))
                            + " " + DateFormat.format("dd:MM", (new Date().getTime())));
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("Status").setValue("Online");}
        super.onResume();
    }
}