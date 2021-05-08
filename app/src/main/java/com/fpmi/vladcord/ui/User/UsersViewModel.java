package com.fpmi.vladcord.ui.User;

import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.FireChangeInterface;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.fpmi.vladcord.ui.friends_list.FriendsAdapter;

import java.util.ArrayList;
import java.util.List;
//think about ListAdapter and diffUtil and ViewHolder
public class UsersViewModel extends ViewModel implements FireChangeInterface {

    private final UsersModel usersModel;
    private ProgressBar progressBar;
    private UsersAdapter adapter;

    public UsersViewModel( UsersAdapter usersAdapter, ProgressBar progressBar) {
        usersModel = new UsersModel(this);
        this.progressBar = progressBar;
        this.adapter = usersAdapter;

    }

    public void getDataFromDB(List<User> listOfUsers){
        usersModel.getDataFromDB(listOfUsers);
    }


    public List<User> sortNameUsers(List<User> listOfUsers, String string) {
        List<User> help = new ArrayList<>();
        for(User r: listOfUsers){
            if(r.getName().toLowerCase().contains(string.toLowerCase())){
                help.add(r);
            }
        }
        return help;
    }

    public List<User> sortEmailUsers(List<User> listOfUsers, String string) {
        List<User> help = new ArrayList<>();
        for(User r: listOfUsers){
            if(r.getEmail().toLowerCase().contains(string.toLowerCase())){
                help.add(r);
            }
        }
        return help;
    }

    public void addFriend(String friendId) {
        usersModel.addFriend(friendId);
    }


    @Override
    public void DataChanged() {
        System.out.println();
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}
