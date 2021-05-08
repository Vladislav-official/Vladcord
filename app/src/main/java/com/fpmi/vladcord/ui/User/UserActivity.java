package com.fpmi.vladcord.ui.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fpmi.vladcord.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    public static final int SEARCH_BY_NAME = 102;
    public static final int SEARCH_BY_EMAIL = 103;

    //toolbar
    private EditText userEmailSearch;
    private EditText userNameSearch;
    private TextView title_view;
    private ImageView search_view;
    private Toolbar toolbar;
    private ExtendedFloatingActionButton buttonTap;

    //content
    private UsersViewModel usersViewModel;
    private RecyclerView vListOfUsers;
    private List<User> listOfUsers;
    private List<String> listOfAddFriends;
    private List<View> vlistOfAddFriends;
    private UsersAdapter usersAdapter;
    private ProgressBar progressBar;
    private String friendClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        setStatusOnline();
        Activity usersActivity = this;
            init(usersActivity);
        initEventListeners(usersActivity);
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

    private void init(Activity usersActivity) {

        toolbar = (Toolbar) findViewById(R.id.toolbar_users);
        title_view = usersActivity.findViewById(R.id.title_toolbar);
        search_view = usersActivity.findViewById(R.id.search_view);
        userNameSearch = usersActivity.findViewById(R.id.search_name_input);
        userEmailSearch = usersActivity.findViewById(R.id.search_email_input);
        buttonTap = findViewById(R.id.button_tap);

        listOfAddFriends = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        vListOfUsers =  findViewById(R.id.users_list);
        listOfUsers = new ArrayList<>();
        vlistOfAddFriends = new ArrayList<>();
        registerForContextMenu(search_view);
        search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContextMenu(search_view);
            }
        });
        buttonTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < listOfAddFriends.size(); ++i){
                    usersViewModel.addFriend(listOfAddFriends.get(i));
                    vlistOfAddFriends.get(i).findViewById(R.id.user_chosed).setVisibility(View.GONE);
                }
                Toast.makeText(usersActivity, R.string.send_request_for_add_friend, Toast.LENGTH_LONG)
                        .show();
                buttonTap.setText(R.string.tap_to_choose_friend);
            }
        });
        usersAdapter = new UsersAdapter(new RecycleUserClick() {
            @Override
            public void onClick(String friendId, View view) {
                if(view.findViewById(R.id.user_chosed).getVisibility() == View.GONE){
                    listOfAddFriends.add(friendId);
                    vlistOfAddFriends.add(view);
                    view.findViewById(R.id.user_chosed).setVisibility(View.VISIBLE);
                }
                else{
                    listOfAddFriends.remove(friendId);
                    vlistOfAddFriends.remove(view);
                    view.findViewById(R.id.user_chosed).setVisibility(View.GONE);
                }
                if(listOfAddFriends.size() != 0){
                    buttonTap.setText(R.string.invite_friends);
                }
                else{
                    buttonTap.setText(R.string.tap_to_choose_friend);
                }
            }
        }, usersActivity, listOfUsers);
        vListOfUsers.setAdapter(usersAdapter);
        vListOfUsers.setLayoutManager(new LinearLayoutManager(usersActivity));
        usersViewModel = new UsersViewModel(usersAdapter, progressBar);
        usersViewModel.getDataFromDB(listOfUsers);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public void onBackPressed() {
            if(toolbar.findViewById(R.id.search_name_input).getVisibility() == View.VISIBLE){
                toolbar.findViewById(R.id.search_name_input).setVisibility(View.GONE);
                toolbar.findViewById(R.id.search_view).setVisibility(View.VISIBLE);
                toolbar.findViewById(R.id.title_toolbar).setVisibility(View.VISIBLE);
            }
            else if(toolbar.findViewById(R.id.search_email_input).getVisibility() == View.VISIBLE){
                toolbar.findViewById(R.id.search_email_input).setVisibility(View.GONE);
                toolbar.findViewById(R.id.search_view).setVisibility(View.VISIBLE);
                toolbar.findViewById(R.id.title_toolbar).setVisibility(View.VISIBLE);
            }
            else {
                super.onBackPressed();
            }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch (v.getId()) {
            case R.id.search_view:
                menu.add(Menu.NONE, SEARCH_BY_NAME, Menu.NONE, "Search by name");
                menu.add(Menu.NONE, SEARCH_BY_EMAIL, Menu.NONE, "Search by email");
                break;
        }
        //menu.add(Menu.NONE, ADDING_FRIEND, Menu.NONE, R.string.ask_add_friend);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        InputMethodManager imm;
        InputMethodManager imm1;
        switch (item.getItemId()) {
            case SEARCH_BY_EMAIL:
                search_view.setVisibility(View.GONE);
                userEmailSearch.setVisibility(View.VISIBLE);
                title_view.setVisibility(View.GONE);
                userEmailSearch.requestFocus();
                imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(userEmailSearch, InputMethodManager.SHOW_IMPLICIT);
                return true;
            case SEARCH_BY_NAME:
                search_view.setVisibility(View.GONE);
                userNameSearch.setVisibility(View.VISIBLE);
                title_view.setVisibility(View.GONE);
                userNameSearch.requestFocus();
                imm1 = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(userNameSearch, InputMethodManager.SHOW_IMPLICIT);
                return true;
        }
        return super.onContextItemSelected(item);
    }




    private void initEventListeners(Activity usersActivity)
    {

        userEmailSearch.addTextChangedListener(new TextWatcher() {
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
                            usersViewModel.sortEmailUsers(listOfUsers, s.toString())));
                }
                else{
                    vListOfUsers.setAdapter(new UsersAdapter(new RecycleUserClick() {
                        @Override
                        public void onClick(String friendId, View view) {
                            registerForContextMenu(view);
                            friendClicked = friendId;
                        }
                    }, usersActivity.getApplicationContext(),
                            usersViewModel.sortEmailUsers(listOfUsers, "")));
                }
            }
        });

        userNameSearch.addTextChangedListener(new TextWatcher() {
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
                            usersViewModel.sortNameUsers(listOfUsers, s.toString())));
                }
                else{
                    vListOfUsers.setAdapter(new UsersAdapter(new RecycleUserClick() {
                        @Override
                        public void onClick(String friendId, View view) {
                            registerForContextMenu(view);
                            friendClicked = friendId;
                        }
                    }, usersActivity.getApplicationContext(),
                            usersViewModel.sortNameUsers(listOfUsers, "")));
                }
            }
        });


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