package com.fpmi.vladcord;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.User.UserActivity;
import com.fpmi.vladcord.ui.friends_request_list.FriendReqActivity;
import com.fpmi.vladcord.ui.groups.GroupAddActivity;
import com.fpmi.vladcord.ui.groups.GroupsActivity;
import com.fpmi.vladcord.ui.profile.ProfileActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.squareup.picasso.Picasso;

import org.intellij.lang.annotations.Language;

import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private NavController navController;
    private CircleImageView user_avatar;
    private ProgressBar progressBar;
    private TextView user_name;
    private TextView user_email;
    private AppBarConfiguration mAppBarConfiguration;
    private static final int SIGN_IN_CODE = 1;
    private static final int SIGN_IN_CODEIN = 3;
    private Toolbar toolbar;
    private DrawerLayout activity_main;
    private DrawerLayout drawer;
    private User user = null;
    private ImageButton changeTheme;

    static boolean calledAlready = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, getString(R.string.you_logged), Snackbar.LENGTH_LONG).show();
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    this.setAndCheckCurUser();
                    navController.navigate(R.id.nav_friends);
                }
            } else {
                Snackbar.make(activity_main, getString(R.string.you_not_logged), Snackbar.LENGTH_LONG).show();
            }
        }
        if (requestCode == SIGN_IN_CODEIN) {
            if (resultCode == RESULT_OK) {
                recreate();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).build(), SIGN_IN_CODE);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                this.setAndCheckCurUser();
            }
        } else {
            this.setAndCheckCurUser();
            setStatusOnline();
        }

        activity_main = findViewById(R.id.activity_main);
        NavigationView navigationView = findViewById(R.id.nav_view);
        progressBar = navigationView.getHeaderView(0).findViewById(R.id.progress_bar);
        user_name = navigationView.getHeaderView(0).findViewById(R.id.user_main_name);
        user_email = navigationView.getHeaderView(0).findViewById(R.id.user_main_email);
        user_avatar = navigationView.getHeaderView(0).findViewById(R.id.user_main_avatar);
        changeTheme = navigationView.getHeaderView(0).findViewById(R.id.change_theme);
        changeTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });

        Activity mainActivity = this;

        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Intent intent = new Intent(mainActivity, ProfileActivity.class);
                    intent.putExtra("profileName", user.getName());
                    intent.putExtra("profileAvatar", user.getUrlAva());
                    intent.putExtra("profileStatus", user.getStatus());
                    intent.putExtra("profileEmail", user.getEmail());
                    intent.putExtra("profileBio", user.getBio());
                    intent.putExtra("profileId", user.getuID());
                    startActivityForResult(intent, SIGN_IN_CODEIN);
                }

            }
        });
        drawer = new DrawerLayout(this.getBaseContext());


        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitleTextAppearance(this, R.style.RobotoBoldTextAppearance);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = activity_main;


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_friends, R.id.nav_users, R.id.nav_friends_request)
                .setOpenableLayout(drawer)
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
            if (toolbar.findViewById(R.id.search_input).getVisibility() == View.VISIBLE) {
                toolbar.findViewById(R.id.search_input).setVisibility(View.GONE);
                toolbar.findViewById(R.id.search_view).setVisibility(View.VISIBLE);
                toolbar.findViewById(R.id.title_toolbar).setVisibility(View.VISIBLE);
            } else {
                super.onBackPressed();
            }
        }
    }


    @Override
    protected void onPause() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setStatusOffline();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setStatusOffline();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setStatusOnline();
            this.setAndCheckCurUser();
        }

        super.onResume();
    }


    public void setAndCheckCurUser() {
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userD = snapshot.getValue(User.class);
                        if (userD != null && userD.getuID() != null) {
                            user = userD;
                        } else {
                            user = new User(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    "https://www.freepngimg.com/thumb/facebook/62681-flat-icons-face-computer-design-avatar-icon.png", "Online", "");
                            FirebaseDatabase.getInstance().getReference("Users/".concat
                                    (FirebaseAuth.getInstance().getCurrentUser().getUid())).setValue(user);
                            UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                            builder.setPhotoUri(Uri.parse("https://www.freepngimg.com/thumb/facebook/62681-flat-icons-face-computer-design-avatar-icon.png"));
                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(builder.build());
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

        if (id == R.id.nav_settings) {
            if (user != null) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("profileName", user.getName());
                intent.putExtra("profileAvatar", user.getUrlAva());
                intent.putExtra("profileStatus", user.getStatus());
                intent.putExtra("profileEmail", user.getEmail());
                intent.putExtra("profileBio", user.getBio());
                intent.putExtra("profileId", user.getuID());
                startActivityForResult(intent, SIGN_IN_CODEIN);
            }
        }
        if (id == R.id.nav_friends) {
            navController.navigate(R.id.nav_friends);
        }
        if (id == R.id.nav_users) {
            Intent intent1 = new Intent(this, UserActivity.class);
            startActivity(intent1);
        }
        if (id == R.id.nav_friends_request) {
            startActivity(new Intent(MainActivity.this, FriendReqActivity.class));
        }
        if (id == R.id.nav_group_creation) {
            startActivity(new Intent(MainActivity.this, GroupAddActivity.class));
        }
        if (id == R.id.nav_groups) {
            startActivity(new Intent(MainActivity.this, GroupsActivity.class));
        }
        return true;
    }

    public void setStatusOnline() {
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status").setValue("Online");
    }

    public void setStatusOffline() {
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status")
                .setValue(getString(R.string.last_seen) + " " + (DateFormat.format("HH:mm", (new Date().getTime())))
                        + " " + DateFormat.format("dd:MM", (new Date().getTime())));
    }
}