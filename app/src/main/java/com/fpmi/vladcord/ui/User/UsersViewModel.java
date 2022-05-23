package com.fpmi.vladcord.ui.User;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.FirebaseChangeInterface;

import java.util.ArrayList;
import java.util.List;

//think about ListAdapter and diffUtil and ViewHolder
public class UsersViewModel extends ViewModel {

    private final UsersModel usersModel;

    private final MutableLiveData<Integer> progressBarLive = new MutableLiveData<>();
    private final MutableLiveData<List<User>> listOfUsersLive = new MutableLiveData<>();



    public UsersViewModel() {
        usersModel = new UsersModel(this);
        listOfUsersLive.setValue(new ArrayList<>());
        this.progressBarLive.setValue(View.VISIBLE);
    }

    public LiveData<Integer> getProgressBarLive() {
        return progressBarLive;
    }


    public void getDataFromDB() {
        usersModel.getDataFromDB(listOfUsersLive);
    }

    public LiveData<List<User>> getUsers() { return listOfUsersLive; }

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


}
