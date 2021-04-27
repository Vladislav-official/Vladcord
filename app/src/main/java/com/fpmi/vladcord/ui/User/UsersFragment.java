package com.fpmi.vladcord.ui.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
//think about ListAdapter and diffUtil and ViewHolder
public class UsersFragment extends Fragment {
    public static final int ADDING_FRIEND = 101;

    private UsersViewModel usersViewModel;
    private EditText userSearch;
    private RecyclerView vListOfUsers;
    private List<User> listOfUsers;
    private UsersAdapter usersAdapter;
    private ProgressBar progressBar;
    private Friend friendClicked;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);
        Activity usersActivity = this.getActivity();
        init(root, usersActivity);
        initEventListeners(root, usersActivity);
        return root;
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

    private void init(View root, Activity usersActivity)
    {
        usersViewModel = new UsersViewModel();
        progressBar = root.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        userSearch = root.findViewById(R.id.search_input);
        vListOfUsers =  root.findViewById(R.id.users_list);
        listOfUsers = new ArrayList<>();
        usersAdapter = new UsersAdapter(new RecycleUserClick() {
            @Override
            public void onClick(Friend friend, View view) {
                registerForContextMenu(view);
                friendClicked = friend;

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
                Toast.makeText(getActivity(), R.string.send_request_for_add_friend, Toast.LENGTH_LONG)
                        .show();
                usersViewModel.addFriend(friendClicked);
                return true;
        }
        return super.onContextItemSelected(item);
    }



    @Override
    public void onResume() {
        super.onResume();
        usersViewModel.getDataFromDB(listOfUsers, usersAdapter, progressBar);
    }

    private void initEventListeners(View root, Activity usersActivity)
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
                        public void onClick(Friend friend, View view) {
                            registerForContextMenu(view);
                            friendClicked = friend;
                        }
                    }, root.getContext(),
                            usersViewModel.sortUsers(listOfUsers, s.toString())));
                }
                else{
                    vListOfUsers.setAdapter(new UsersAdapter(new RecycleUserClick() {
                        @Override
                        public void onClick(Friend friend, View view) {
                            registerForContextMenu(view);
                            friendClicked = friend;
                        }
                    }, root.getContext(),
                            usersViewModel.sortUsers(listOfUsers, "")));
                }
            }
        });


    }

}

