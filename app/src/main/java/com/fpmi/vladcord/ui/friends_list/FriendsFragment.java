package com.fpmi.vladcord.ui.friends_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.messages_list.MessageActivity;
import com.fpmi.vladcord.ui.messages_list.MessageFragment;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FriendsFragment extends Fragment {

    private FriendsViewModel friendsViewModel;
    private RecyclerView vListOfFriends;
    private List<Friend> listOfFriends;
    private FriendsAdapter friendsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        init(root, this.getActivity());
        return root;



    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int MESSAGE_LIST = 2;
        if (requestCode == MESSAGE_LIST) {
            if (resultCode == RESULT_OK) {
                System.out.println("NICE");
            }
        }
    }
    private void init(View root, Activity friendsActivity)
    {
        friendsViewModel = new FriendsViewModel();
        vListOfFriends =  root.findViewById(R.id.friends_list);
        listOfFriends = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(new RecycleItemClick() {
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
        friendsViewModel.getDataFromDB(listOfFriends, friendsAdapter);
    }

    @Override
    public void onResume() {
        friendsViewModel.getDataFromDB(listOfFriends, friendsAdapter);
        super.onResume();
    }
}

