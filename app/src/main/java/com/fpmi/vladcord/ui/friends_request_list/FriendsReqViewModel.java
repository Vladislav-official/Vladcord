package com.fpmi.vladcord.ui.friends_request_list;

import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.FirebaseChangeInterface;
import com.fpmi.vladcord.ui.User.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsReqViewModel extends ViewModel implements FirebaseChangeInterface {
    private final FriendReqModel friendModel;
    private FriendsReqAdapter friendsReqAdapter;
    private ProgressBar progressBar;

    public FriendsReqViewModel(FriendsReqAdapter friendsReqAdapter, ProgressBar progressBar) {
        friendModel = new FriendReqModel(this);
        this.friendsReqAdapter = friendsReqAdapter;
        this.progressBar = progressBar;
    }

    public void getDataFromDB(List<User> list) {
        friendModel.getDataFromDB(list);
    }

    public List<User> sortFriends(List<User> listOfFriends, String string) {
        List<User> resultListOfFriends = new ArrayList<>();
        for (User r : listOfFriends) {
            if (r.getName().toLowerCase().contains(string.toLowerCase())) {
                resultListOfFriends.add(r);
            }
        }
        return resultListOfFriends;
    }

    public FriendReqModel getFriendModel() {
        return friendModel;
    }

    @Override
    public void DataChanged() {
        progressBar.setVisibility(View.GONE);
        friendsReqAdapter.notifyDataSetChanged();
    }

    public void setStatusOnline() {
        friendModel.setStatusOnline();
    }

    public void setStatusOffline(String status) {
        friendModel.setStatusOffline(status);
    }
}
