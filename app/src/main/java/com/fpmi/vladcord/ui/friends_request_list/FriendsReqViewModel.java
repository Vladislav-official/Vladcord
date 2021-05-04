package com.fpmi.vladcord.ui.friends_request_list;

import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.fpmi.vladcord.ui.friends_list.FriendModel;
import com.fpmi.vladcord.ui.friends_list.FriendsAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendsReqViewModel extends ViewModel {
    private final FriendReqModel friendModel;

    public FriendsReqViewModel() {
        friendModel = new FriendReqModel();
    }

    public void getDataFromDB(List<User> list, FriendsReqAdapter friendsReqAdapter, ProgressBar progressBar){
        friendModel.getDataFromDB(list, friendsReqAdapter, progressBar);
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
    public FriendReqModel getFriendModel() {
        return friendModel;
    }
}
