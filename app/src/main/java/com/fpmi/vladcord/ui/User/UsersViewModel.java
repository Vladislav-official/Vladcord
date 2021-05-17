package com.fpmi.vladcord.ui.User;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.FirebaseChangeInterface;

import java.util.ArrayList;
import java.util.List;

//think about ListAdapter and diffUtil and ViewHolder
public class UsersViewModel extends ViewModel implements FirebaseChangeInterface {

    private final UsersModel usersModel;
    @SuppressLint("StaticFieldLeak")
    private  ProgressBar progressBar;
    private final UsersAdapter adapter;

    public UsersViewModel(UsersAdapter usersAdapter, ProgressBar progressBar) {
        usersModel = new UsersModel(this);
        this.progressBar = progressBar;
        this.adapter = usersAdapter;
    }

    public void getDataFromDB(List<User> listOfUsers) {
        usersModel.getDataFromDB(listOfUsers);
    }

    public void setStatusOnline() {
        usersModel.setStatusOnline();
    }

    public void setStatusOffline(String status) {
        usersModel.setStatusOffline(status);
    }

    public List<User> sortNameUsers(List<User> listOfUsers, String string) {
        if (!string.equals("")) {
            List<User> help = new ArrayList<>();
            for (User r : listOfUsers) {
                if (r.getName().toLowerCase().contains(string.toLowerCase())) {
                    help.add(r);
                }
            }
            return help;
        }
        return null;
    }

    public List<User> sortEmailUsers(List<User> listOfUsers, String string) {
        List<User> help = new ArrayList<>();
        for (User r : listOfUsers) {
            if (r.getEmail().toLowerCase().contains(string.toLowerCase())) {
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
            progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}
