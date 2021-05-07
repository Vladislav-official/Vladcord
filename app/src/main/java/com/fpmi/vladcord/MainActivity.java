    package com.fpmi.vladcord;

    import android.Manifest;
    import android.app.Activity;
    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.database.Cursor;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.text.format.DateFormat;
    import android.view.Menu;
    import android.view.MenuInflater;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.ProgressBar;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.core.view.GravityCompat;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.loader.content.CursorLoader;
    import androidx.navigation.NavController;
    import androidx.navigation.Navigation;
    import androidx.navigation.ui.AppBarConfiguration;
    import androidx.navigation.ui.NavigationUI;

    import com.firebase.ui.auth.AuthUI;
    import com.fpmi.vladcord.ui.User.User;
    import com.fpmi.vladcord.ui.User.UserActivity;
    import com.fpmi.vladcord.ui.friends_request_list.FriendReqActivity;
    import com.fpmi.vladcord.ui.profile.ProfileActivity;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.material.navigation.NavigationView;
    import com.google.android.material.snackbar.Snackbar;
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

    import java.io.IOException;
    import java.io.InputStream;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.util.Date;

    import de.hdodenhof.circleimageview.CircleImageView;
    import xyz.schwaab.avvylib.AvatarView;


    public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{



private NavController navController;
        private CircleImageView user_avatar;
    private ProgressBar progressBar;
    private TextView user_name;
    private TextView user_email;
    private AppBarConfiguration mAppBarConfiguration;
    private static final int SIGN_IN_CODE = 1;
        private static final int SIGN_IN_CODEIN = 3;
    private NavigationView navigationView;
    private Toolbar toolbar;
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
                    navController.navigate(R.id.nav_friends);
                }
            } else {
                Snackbar.make(activity_main, getString(R.string.you_not_logged), Snackbar.LENGTH_LONG).show();
                user = null;
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
        progressBar = navigationView.getHeaderView(0).findViewById(R.id.progress_bar);
        user_name = navigationView.getHeaderView(0).findViewById(R.id.user_main_name);
        user_email = navigationView.getHeaderView(0).findViewById(R.id.user_main_email);
        user_avatar = navigationView.getHeaderView(0).findViewById(R.id.user_main_avatar);

        Activity mainActivity = this;
        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, ProfileActivity.class);
                intent.putExtra("profileName", user.getName());
                intent.putExtra("profileAvatar", user.getUrlAva());
                intent.putExtra("profileStatus", user.getStatus());
                intent.putExtra("profileEmail", user.getEmail());
                intent.putExtra("profileBio", user.getBio());
                intent.putExtra("profileId", user.getuID());
                startActivity(intent);

            }
        });

        drawer = new DrawerLayout(this.getBaseContext());
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).build(), SIGN_IN_CODE);
        } else {
            this.setAndCheckCurUser();
            FirebaseDatabase.getInstance().getReference("Users/".concat
                    (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status").setValue("Online");
        }

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.activity_main);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_friends, R.id.nav_users, R.id.nav_friends_request)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.navigate(R.id.nav_friends);
        navigationView.setNavigationItemSelectedListener(this);
    }



    @Override
    public boolean onSupportNavigateUp() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if(toolbar.findViewById(R.id.search_input).getVisibility() == View.VISIBLE){
                    toolbar.findViewById(R.id.search_input).setVisibility(View.GONE);
                    toolbar.findViewById(R.id.search_view).setVisibility(View.VISIBLE);
                    toolbar.findViewById(R.id.title_toolbar).setVisibility(View.VISIBLE);
                }
                else {
                    super.onBackPressed();
                }
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
                    .setValue(getString(R.string.last_seen) + " " + (DateFormat.format("HH:mm", (new Date().getTime())))
                            + " " + DateFormat.format("dd:MM", (new Date().getTime())));
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status").setValue("Online");}
        else{
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).build(), SIGN_IN_CODE);
        }
        super.onResume();
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
                            Picasso.get()
                                    .load(user.getUrlAva())
                                    .into(user_avatar);
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
                        if(userD != null && userD.getuID() != null) {
                            user = userD;
                        }
                        else{
                            user = new User(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    "https://www.freepngimg.com/thumb/facebook/62681-flat-icons-face-computer-design-avatar-icon.png", "Online", "");
                            FirebaseDatabase.getInstance().getReference("Users/".concat
                                    (FirebaseAuth.getInstance().getCurrentUser().getUid())).setValue(user);
                        }
                        user_name.setText(user.getName());
                        user_email.setText(user.getEmail());
                        Picasso.get()
                                .load(user.getUrlAva())
                                .into(user_avatar);
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
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch(id) {
                case R.id.nav_settings:
                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra("profileName", user.getName());
                    intent.putExtra("profileAvatar", user.getUrlAva());
                    intent.putExtra("profileStatus", user.getStatus());
                    intent.putExtra("profileEmail", user.getEmail());
                    intent.putExtra("profileBio", user.getBio());
                    intent.putExtra("profileId", user.getuID());
                    startActivity(intent);
                break;
                case R.id.nav_friends:
                    navController.navigate(R.id.nav_friends);
                    break;
                case R.id.nav_users:
                    startActivity(new Intent(MainActivity.this, UserActivity.class));
                    break;
                case R.id.nav_friends_request:
                    startActivity(new Intent(MainActivity.this, FriendReqActivity.class));
                    break;
            }
            return true;
        }
    }