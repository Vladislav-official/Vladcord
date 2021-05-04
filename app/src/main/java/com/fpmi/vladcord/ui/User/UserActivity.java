package com.fpmi.vladcord.ui.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fpmi.vladcord.R;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    public static final int ADDING_FRIEND = 101;

    private UsersViewModel usersViewModel;
    private EditText userSearch;
    private RecyclerView vListOfUsers;
    private List<User> listOfUsers;
    private UsersAdapter usersAdapter;
    private ProgressBar progressBar;
    private String friendClicked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_users);
        setupActionBar();
        Activity usersActivity = this;
        init(usersActivity);
        initEventListeners(usersActivity);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int MESSAGE_LIST = 2;
        if (requestCode == MESSAGE_LIST) {
            if (resultCode == RESULT_OK) {
            }
        }
    }

    private void init(Activity usersActivity)
    {
        usersViewModel = new UsersViewModel();
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        userSearch = findViewById(R.id.search_input);
        vListOfUsers =  findViewById(R.id.users_list);
        listOfUsers = new ArrayList<>();
        usersAdapter = new UsersAdapter(new RecycleUserClick() {
            @Override
            public void onClick(String friendId, View view) {
                registerForContextMenu(view);
                friendClicked = friendId;

            }
        }, usersActivity, listOfUsers);
        vListOfUsers.setAdapter(usersAdapter);
        vListOfUsers.setLayoutManager(new LinearLayoutManager(usersActivity));
        usersViewModel.getDataFromDB(listOfUsers, usersAdapter, progressBar);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, ADDING_FRIEND, Menu.NONE, R.string.ask_add_friend);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ADDING_FRIEND:

                Toast.makeText(this, R.string.send_request_for_add_friend, Toast.LENGTH_LONG)
                        .show();
                usersViewModel.addFriend(friendClicked);
                return true;
        }
        return super.onContextItemSelected(item);
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    private void initEventListeners(Activity usersActivity)
    {

        userSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {

                    vListOfUsers.setAdapter(new UsersAdapter(new RecycleUserClick() {
                        @Override
                        public void onClick(String friendId, View view) {
                            registerForContextMenu(view);
                            friendClicked = friendId;
                        }
                    }, usersActivity.getApplicationContext(),
                            usersViewModel.sortUsers(listOfUsers, s.toString())));
                }
                else{
                    vListOfUsers.setAdapter(new UsersAdapter(new RecycleUserClick() {
                        @Override
                        public void onClick(String friendId, View view) {
                            registerForContextMenu(view);
                            friendClicked = friendId;
                        }
                    }, usersActivity.getApplicationContext(),
                            usersViewModel.sortUsers(listOfUsers, "")));
                }
            }
        });


    }

}