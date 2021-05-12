package com.fpmi.vladcord.ui.groups;

import android.app.Activity;
import android.os.Bundle;

import com.fpmi.vladcord.ui.User.RecycleUserClick;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.User.UsersAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fpmi.vladcord.R;

import java.util.ArrayList;
import java.util.List;

public class GroupAddActivity extends AppCompatActivity {

    private EditText group_name;
    private Toolbar toolbar;
    private ExtendedFloatingActionButton buttonTap;

    //content
    private GroupeAddViewModel usersViewModel;
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
        setSupportActionBar(toolbar);
        Activity usersActivity = this;

        buttonTap = findViewById(R.id.button_tap);

        listOfAddFriends = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        vListOfUsers =  findViewById(R.id.users_list);
        group_name = findViewById(R.id.group_name);

        listOfUsers = new ArrayList<>();
        vlistOfAddFriends = new ArrayList<>();

        buttonTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(buttonTap.getText().equals(getString(R.string.create_group) )) {
                    if(!group_name.getText().toString().equals("")) {
                        for (int i = 0; i < listOfAddFriends.size(); ++i) {
                            vlistOfAddFriends.get(i).findViewById(R.id.user_chosed).setVisibility(View.GONE);
                        }
                        usersViewModel.addGroup(listOfAddFriends, group_name.getText().toString());
                        buttonTap.setText(R.string.tap_to_choose_friend);
                        Toast.makeText(usersActivity, "Group with name " + group_name.getText() + " created", Toast.LENGTH_LONG)
                                .show();
                        group_name.setText("");
                    }
                    else{
                        Toast.makeText(usersActivity, "Please enter name of group", Toast.LENGTH_LONG)
                                .show();
                    }
                }
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
                    buttonTap.setText(R.string.create_group);
                }
                else{
                    buttonTap.setText(R.string.tap_to_choose_friend);
                }
            }
        }, usersActivity, listOfUsers);
        vListOfUsers.setAdapter(usersAdapter);
        vListOfUsers.setLayoutManager(new LinearLayoutManager(usersActivity));
        usersViewModel = new GroupeAddViewModel(usersAdapter, progressBar);
        usersViewModel.getDataFromDB(listOfUsers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}