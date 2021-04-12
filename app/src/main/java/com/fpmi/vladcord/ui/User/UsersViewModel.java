package com.fpmi.vladcord.ui.User;

import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.friends_list.Friend;

import java.util.ArrayList;
import java.util.List;
//think about ListAdapter and diffUtil and ViewHolder
public class UsersViewModel extends ViewModel {

    private final UsersModel usersModel;


    public UsersViewModel() {
        usersModel = new UsersModel();
    }

    public void getDataFromDB(List<User> listOfUsers, UsersAdapter usersAdapter, ProgressBar progressBar) {
        usersModel.getDataFromDB(listOfUsers, usersAdapter,progressBar);
    }


    public List<User> sortUsers(List<User> listOfUsers, String string) {
        List<User> help = new ArrayList<>();
        for(User r: listOfUsers){
            if(r.getName().toLowerCase().contains(string.toLowerCase())){
                help.add(r);
            }
        }
        return help;
    }

    public void addFriend(Friend friend) {
        usersModel.addFriend(friend);
    }

    






}
