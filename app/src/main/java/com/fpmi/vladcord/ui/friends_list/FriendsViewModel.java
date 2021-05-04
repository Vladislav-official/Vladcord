package com.fpmi.vladcord.ui.friends_list;

import android.app.Activity;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.ui.User.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsViewModel extends ViewModel {
    private final FriendModel friendModel;

    public FriendsViewModel() {
        friendModel = new FriendModel();
    }

    public void getDataFromDB(List<User> listOfFriends, FriendsAdapter adapter, ProgressBar progressBar) {
        friendModel.getDataFromDB(listOfFriends, adapter, progressBar);
    }

    public List<User> sortFriends(List<User> listOfFriends, String string) {
        List<User> resultListOfFriends = new ArrayList<>();
        for(User r: listOfFriends){
            if(r.getName().toLowerCase().contains(string.toLowerCase())){
                resultListOfFriends.add(r);
            }
        }
        return resultListOfFriends;
    }




}
