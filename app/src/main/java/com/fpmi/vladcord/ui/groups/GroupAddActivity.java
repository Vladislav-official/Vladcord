package com.fpmi.vladcord.ui.groups;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.RecycleUserClick;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.User.UsersAdapter;
import com.fpmi.vladcord.ui.friends_request_list.FriendsReqViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GroupAddActivity extends AppCompatActivity {

    private EditText group_name;
    private Toolbar toolbar;
    private ExtendedFloatingActionButton buttonTap;

    //content
    private GroupAddViewModel usersViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView vListOfUsers;
    private List<User> listOfUsers;
    private List<String> listOfAddFriends;
    private List<View> vlistOfAddFriends;
    private UsersAdapter usersAdapter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(hasConnection(getApplicationContext())) {
            toolbar.setTitle(R.string.create_group_title);
        }
        else{
            toolbar.setTitle(R.string.waiting_for_network);
        }
        setSupportActionBar(toolbar);
        Activity usersActivity = this;

        buttonTap = findViewById(R.id.button_tap);

        listOfAddFriends = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        vListOfUsers = findViewById(R.id.users_list);
        group_name = findViewById(R.id.group_name);
        swipeRefreshLayout = findViewById(R.id.swipe_container);

        listOfUsers = new ArrayList<>();
        vlistOfAddFriends = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!hasConnection(getApplicationContext())){
                    getSupportActionBar().setTitle(R.string.waiting_for_network);
                }
                else{
                    getSupportActionBar().setTitle(R.string.create_group_title);
                    vListOfUsers.setAdapter(usersAdapter);
                    vListOfUsers.setLayoutManager(new LinearLayoutManager(usersActivity));
                    usersViewModel = new GroupAddViewModel(usersAdapter, progressBar);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        buttonTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (buttonTap.getText().equals(getString(R.string.create_group))) {
                    if (!group_name.getText().toString().equals("")) {
                        for (int i = 0; i < listOfAddFriends.size(); ++i) {
                            vlistOfAddFriends.get(i).findViewById(R.id.user_chosed).setVisibility(View.GONE);
                        }
                        usersViewModel.addGroup(usersActivity, listOfAddFriends, group_name.getText().toString());
                        buttonTap.setText(R.string.tap_to_choose_friend);

                        group_name.setText("");
                    } else {
                        Toast.makeText(usersActivity, getString(R.string.forget_name_of_group), Toast.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });
        usersAdapter = new UsersAdapter(new RecycleUserClick() {
            @Override
            public void onClick(String friendId, View view) {
                if (view.findViewById(R.id.user_chosed).getVisibility() == View.GONE) {
                    listOfAddFriends.add(friendId);
                    vlistOfAddFriends.add(view);
                    view.findViewById(R.id.user_chosed).setVisibility(View.VISIBLE);
                } else {
                    listOfAddFriends.remove(friendId);
                    vlistOfAddFriends.remove(view);
                    view.findViewById(R.id.user_chosed).setVisibility(View.GONE);
                }
                if (listOfAddFriends.size() != 0) {
                    buttonTap.setText(R.string.create_group);
                } else {
                    buttonTap.setText(R.string.tap_to_choose_friend);
                }
            }
        }, usersActivity, listOfUsers);
        vListOfUsers.setAdapter(usersAdapter);
        vListOfUsers.setLayoutManager(new LinearLayoutManager(usersActivity));
        usersViewModel = new GroupAddViewModel(usersAdapter, progressBar);
        usersViewModel.getDataFromDB(listOfUsers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            usersViewModel.setStatusOffline(getString(R.string.last_seen));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        usersViewModel.setStatusOnline();
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
}