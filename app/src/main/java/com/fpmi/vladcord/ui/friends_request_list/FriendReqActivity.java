package com.fpmi.vladcord.ui.friends_request_list;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendReqActivity extends AppCompatActivity {
    //ViewModel
    private FriendsReqViewModel friendsViewModel;
    //Список пользователей и его адаптер
    private List<User> listOfFriends;
    private FriendsReqAdapter friendsAdapter;
    //Views
    private RecyclerView vListOfFriends;
    private EditText friendSearch;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    //Создание экрана
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_requests);
        init(this);
        initEventListeners(this);

    }

    private void init(Activity friendsActivity) {


        setTitle("Friend requests");
        toolbar = (Toolbar) findViewById(R.id.toolbar_friends_requests);

        vListOfFriends = findViewById(R.id.friends_request_list);
        friendSearch = findViewById(R.id.search_input);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        listOfFriends = new ArrayList<>();

        friendsAdapter = new FriendsReqAdapter(friendsActivity, listOfFriends, new FriendReqModel());
        vListOfFriends.setAdapter(friendsAdapter);
        vListOfFriends.setLayoutManager(new LinearLayoutManager(friendsActivity));

        friendsViewModel = new FriendsReqViewModel(friendsAdapter, progressBar);
        friendsViewModel.getDataFromDB(listOfFriends);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        friendsViewModel.setStatusOnline();
    }

    @Override
    public void onResume() {
        super.onResume();
        //friendsViewModel.getDataFromDB(listOfFriends);

    }


    private void initEventListeners(Activity friendsActivity) {

        /*friendSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    vListOfFriends.setAdapter(new FriendsReqAdapter(friendsActivity.getApplication().getApplicationContext(),
                            friendsViewModel.sortFriends(listOfFriends, s.toString()), friendsViewModel.getFriendModel()));
                } else {
                    vListOfFriends.setAdapter(new FriendsReqAdapter(friendsActivity.getApplication().getApplicationContext(),
                            friendsViewModel.sortFriends(listOfFriends, ""), friendsViewModel.getFriendModel()));
                }
            }
        });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            friendsViewModel.setStatusOffline(getString(R.string.last_seen));
        }

    }

}