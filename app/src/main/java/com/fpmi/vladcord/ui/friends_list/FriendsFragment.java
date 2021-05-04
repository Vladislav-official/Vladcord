package com.fpmi.vladcord.ui.friends_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.messages_list.MessageActivity;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FriendsFragment extends Fragment {

    private FriendsViewModel friendsViewModel;
    private RecyclerView vListOfFriends;
    private EditText friendSearch;
    private List<User> listOfFriends;
    private FriendsAdapter friendsAdapter;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        init(root, this.getActivity());
        initEventListeners(root, getActivity());
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
    private void init(View root, Activity friendsActivity)
    {
        friendsViewModel = new FriendsViewModel();
        vListOfFriends =  root.findViewById(R.id.friends_list);
        progressBar = root.findViewById(R.id.progress_bar);
        friendSearch = root.findViewById(R.id.search_input);
        progressBar.setVisibility(View.VISIBLE);
        listOfFriends = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(new RecycleFriendClick() {
            @Override
            public void onClick(String friendId, String friendName) {
                Intent intent = new Intent(friendsActivity, MessageActivity.class);
                intent.putExtra("friendId", friendId);
                intent.putExtra("friendName", friendName);
                startActivity(intent);
            }
        }, friendsActivity, listOfFriends);
        vListOfFriends.setAdapter(friendsAdapter);
        vListOfFriends.setLayoutManager(new LinearLayoutManager(friendsActivity));
        friendsViewModel.getDataFromDB(listOfFriends, friendsAdapter, progressBar);
    }

    @Override
    public void onResume() {
        super.onResume();
        friendsViewModel.getDataFromDB(listOfFriends, friendsAdapter, progressBar);
    }


    private void initEventListeners(View root, Activity friendsActivity) {

        friendSearch.addTextChangedListener(new TextWatcher() {
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
                    vListOfFriends.setAdapter(new FriendsAdapter(new RecycleFriendClick() {
                        @Override
                        public void onClick(String friendId, String friendName) {
                            Intent intent = new Intent(friendsActivity, MessageActivity.class);
                            intent.putExtra("friendId", friendId);
                            intent.putExtra("friendName", friendName);
                            startActivity(intent);
                        }
                    }, friendsActivity, friendsViewModel.sortFriends(listOfFriends, s.toString())));
                } else {
                    vListOfFriends.setAdapter(new FriendsAdapter(new RecycleFriendClick() {
                        @Override
                        public void onClick(String friendId, String friendName) {
                            Intent intent = new Intent(friendsActivity, MessageActivity.class);
                            intent.putExtra("friendId", friendId);
                            intent.putExtra("friendName", friendName);
                            startActivity(intent);
                        }
                    }, friendsActivity, friendsViewModel.sortFriends(listOfFriends, "")));
                }
            }
        });
    }
}
