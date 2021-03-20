package com.fpmi.vladcord.ui.friends_list;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.UsersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FriendsViewModel extends ViewModel {
    private FriendModel friendModel;
    private List<Friend> friendList = new ArrayList<>();
    private View root;
    private Activity activity;

    public FriendsViewModel(Activity activity, View root) {
        this.root = root;
        this.activity = activity;
        friendModel = new FriendModel();
        friendModel.setAdapter(activity);
        friendModel.getDataFromDB(friendList);
    }

    public void displayAllFriends(ListView listOfFriends) {
        if(friendModel.getAdapter() != null) {
            listOfFriends.setAdapter(friendModel.getAdapter());
        }
    }





}
