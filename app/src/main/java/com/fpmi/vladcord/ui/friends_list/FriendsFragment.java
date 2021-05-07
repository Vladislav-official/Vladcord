package com.fpmi.vladcord.ui.friends_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.messages_list.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FriendsFragment extends Fragment {

    private FriendsViewModel friendsViewModel;
    private RecyclerView vListOfFriends;
    private TextView titleToolbar;
    private EditText friendSearch;
    private List<User> listOfFriends;
    private FriendsAdapter friendsAdapter;
    private ProgressBar progressBar;
    private ImageView search_view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            init(root, this.getActivity());
            initEventListeners(root, getActivity());

        }
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
        search_view = friendsActivity.findViewById(R.id.search_view);
        friendSearch = friendsActivity.findViewById(R.id.search_input);
        titleToolbar = friendsActivity.findViewById(R.id.title_toolbar);

        friendsViewModel = new FriendsViewModel();
        vListOfFriends =  root.findViewById(R.id.friends_list);
        progressBar = root.findViewById(R.id.progress_bar);

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

    private void initEventListeners(View root, Activity friendsActivity) {
        search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_view.setVisibility(View.GONE);
                friendSearch.setVisibility(View.VISIBLE);
                titleToolbar.setVisibility(View.GONE);
                friendSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) friendsActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(friendSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });

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
