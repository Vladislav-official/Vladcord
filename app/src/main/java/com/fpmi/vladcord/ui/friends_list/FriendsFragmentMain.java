package com.fpmi.vladcord.ui.friends_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.friends_request_list.FriendsReqFragment;

public class FriendsFragmentMain extends Fragment {

    private Button friendsList;
    private Button friendsReqList;
    FriendsFragment friendsFragment;
    FriendsReqFragment friendsReqFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.layout_friends, container, false);
        getParentFragmentManager().beginTransaction().replace(R.id.container_for_friends_lists, new FriendsFragment()).commit();
        init(root);
        friendsReqList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_for_friends_lists, new FriendsReqFragment()).commit();
            }
        });
        friendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.container_for_friends_lists,  new FriendsFragment()).commit();
            }
        });
        return root;



    }
    public void init(View view){
        friendsList = view.findViewById(R.id.friends_list);
        friendsReqList = view.findViewById(R.id.friends_request_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        getParentFragmentManager().beginTransaction().replace(R.id.container_for_friends_lists, new FriendsFragment()).commit();
    }
}

