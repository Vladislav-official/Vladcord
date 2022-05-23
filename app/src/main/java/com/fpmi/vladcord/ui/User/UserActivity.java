package com.fpmi.vladcord.ui.User;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.groups.GroupsViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
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
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExtendedFloatingActionButton buttonTap;

    //content
    private UsersViewModel usersViewModel;
    private RecyclerView vListOfUsers;
    private LiveData<List<User>> listOfUsers;
    private List<String> listOfAddFriends;
    private List<View> vlistOfAddFriends;
    private UsersAdapter usersAdapter;
    private ProgressBar progressBar;
    private LiveData<Integer> progressBarLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Activity usersActivity = this;
        init(usersActivity);
        initEventListeners(usersActivity);
        usersViewModel.setStatusOnline();
    }


    private void init(Activity usersActivity) {

        toolbar = (Toolbar) findViewById(R.id.toolbar_users);
        toolbar.setTitleTextAppearance(this, R.style.RobotoBoldTextAppearance);
        if(hasConnection(getApplicationContext())) {
            toolbar.setTitle(R.string.invite_friends);
        }
        else{
            toolbar.setTitle(R.string.waiting_for_network);
        }

        title_view = usersActivity.findViewById(R.id.title_toolbar);
        search_view = usersActivity.findViewById(R.id.search_view);
        userNameSearch = usersActivity.findViewById(R.id.search_name_input);
        userEmailSearch = usersActivity.findViewById(R.id.search_email_input);
        swipeRefreshLayout = findViewById(R.id.users_fragment);
        buttonTap = findViewById(R.id.button_tap);
        //Get ViewModel
        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        listOfUsers = usersViewModel.getUsers();
        listOfUsers.observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                    usersAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
            }
        });

        progressBarLiveData = usersViewModel.getProgressBarLive();
        progressBarLiveData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                progressBar.setVisibility(integer);
            }
        });
        listOfAddFriends = new ArrayList<>();



        vListOfUsers = findViewById(R.id.users_list);

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
                if (listOfAddFriends.size() == 0) {
                    return;
                }
                for (int i = 0; i < listOfAddFriends.size(); ++i) {
                    vlistOfAddFriends.get(i).findViewById(R.id.user_chosed).setVisibility(View.GONE);
                    usersViewModel.addFriend(listOfAddFriends.get(i));
                }
                Toast.makeText(usersActivity, R.string.send_request_for_add_friend, Toast.LENGTH_LONG)
                        .show();
                buttonTap.setText(R.string.tap_to_choose_friend);
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
                    buttonTap.setText(R.string.invite_friends);
                } else {
                    buttonTap.setText(R.string.tap_to_choose_friend);
                }
            }
        }, usersActivity, listOfUsers.getValue());

        vListOfUsers.setAdapter(usersAdapter);
        vListOfUsers.setLayoutManager(new LinearLayoutManager(usersActivity));

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        usersViewModel.getDataFromDB();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public void onBackPressed() {
        if (toolbar.findViewById(R.id.search_name_input).getVisibility() == View.VISIBLE) {
            toolbar.findViewById(R.id.search_name_input).setVisibility(View.GONE);
            toolbar.findViewById(R.id.search_view).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.title_toolbar).setVisibility(View.VISIBLE);
        } else if (toolbar.findViewById(R.id.search_email_input).getVisibility() == View.VISIBLE) {
            toolbar.findViewById(R.id.search_email_input).setVisibility(View.GONE);
            toolbar.findViewById(R.id.search_view).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.title_toolbar).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.search_view){
                menu.add(Menu.NONE, SEARCH_BY_NAME, Menu.NONE, R.string.search_by_name);
                menu.add(Menu.NONE, SEARCH_BY_EMAIL, Menu.NONE, R.string.search_by_email);
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


    private void initEventListeners(Activity usersActivity) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!hasConnection(getApplicationContext())){
                    title_view.setText(R.string.waiting_for_network);
                }
                else{
                    title_view.setText(R.string.invite_friends);
                    vListOfUsers.setAdapter(usersAdapter);
                    vListOfUsers.setLayoutManager(new LinearLayoutManager(usersActivity));
                    //usersViewModel = new ViewModelProvider(UserActivity.this).get(UsersViewModel.class);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        userEmailSearch.addTextChangedListener(new TextWatcher() {
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

                    vListOfUsers.setAdapter(new UsersAdapter(new RecycleUserClick() {
                        @Override
                        public void onClick(String friendId, View view) {
                            registerForContextMenu(view);
                        }
                    }, usersActivity.getApplicationContext(),
                            usersViewModel.sortEmailUsers(listOfUsers.getValue(), s.toString())));
                } else {
                    vListOfUsers.setAdapter(new UsersAdapter(new RecycleUserClick() {
                        @Override
                        public void onClick(String friendId, View view) {
                            registerForContextMenu(view);
                        }
                    }, usersActivity.getApplicationContext(),
                            usersViewModel.sortEmailUsers(listOfUsers.getValue(), "")));
                }
            }
        });

        userNameSearch.addTextChangedListener(new TextWatcher() {
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
                    vListOfUsers.setAdapter(new UsersAdapter(new RecycleUserClick() {
                        @Override
                        public void onClick(String friendId, View view) {
                            registerForContextMenu(view);
                        }
                    }, usersActivity.getApplicationContext(),
                            usersViewModel.sortNameUsers(listOfUsers.getValue(), s.toString())));
                } else {
                    vListOfUsers.setAdapter(new UsersAdapter(new RecycleUserClick() {
                        @Override
                        public void onClick(String friendId, View view) {
                            registerForContextMenu(view);
                        }
                    }, usersActivity.getApplicationContext(),
                            usersViewModel.sortNameUsers(listOfUsers.getValue(), "")));
                }
            }
        });


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
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            usersViewModel.setStatusOffline(getString(R.string.last_seen));
        }
        super.onPause();
    }

}