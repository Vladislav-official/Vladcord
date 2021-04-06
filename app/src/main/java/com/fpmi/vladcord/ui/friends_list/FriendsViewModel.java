package com.fpmi.vladcord.ui.friends_list;

import android.app.Activity;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.ui.User.User;

import java.util.List;

public class FriendsViewModel extends ViewModel {
    private final FriendModel friendModel;

    public FriendsViewModel() {
        friendModel = new FriendModel();
    }

    public void getDataFromDB(List<Friend> listOfFriends, FriendsAdapter adapter) {
        friendModel.getDataFromDB(listOfFriends, adapter);
    }





}
