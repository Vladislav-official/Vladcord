package com.fpmi.vladcord.ui.friends_request_list;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.friends_list.FriendsViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FriendReqActivity extends AppCompatActivity {
    //ViewModel
    private FriendsReqViewModel friendsViewModel;
    //Список пользователей и его адаптер
    private List<User> listOfFriends;
    private FriendsReqAdapter friendsAdapter;
    //Views
    private RecyclerView vListOfFriends;
    private SwipeRefreshLayout swipeRefreshLayout;
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

        if(hasConnection(getApplicationContext())) {
            setTitle(getString(R.string.friends_requests_title));
        }
        else{
            setTitle(getString(R.string.waiting_for_network));
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar_friends_requests);

        vListOfFriends = findViewById(R.id.friends_request_list);
        friendSearch = findViewById(R.id.search_input);
        swipeRefreshLayout = findViewById(R.id.swipe_container);
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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!hasConnection(getApplicationContext())){
                    getSupportActionBar().setTitle(R.string.waiting_for_network);
                }
                else{
                    getSupportActionBar().setTitle(R.string.friends_requests_title);
                    vListOfFriends.setAdapter(friendsAdapter);
                    vListOfFriends.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    friendsViewModel = new FriendsReqViewModel(friendsAdapter, progressBar);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            friendsViewModel.setStatusOffline(getString(R.string.last_seen));
        }

    }

}