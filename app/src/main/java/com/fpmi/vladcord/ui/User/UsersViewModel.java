package com.fpmi.vladcord.ui.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {

    private final List<User> listOfUsers = new ArrayList<>();
    private final UsersModel usersModel;
    private View root;
    private Activity activity;

    public UsersViewModel(View root, Activity activity) {
        this.root = root;
        this.activity = activity;
        usersModel = new UsersModel(activity);
        usersModel.getDataFromDB(listOfUsers);
    }

    public void displayAllUsers(ListView vListOfUsers) {
        vListOfUsers.setAdapter(usersModel.getAdapter());

    }

    public void sortUsers(ListView listView,String string) {
        List<User> help = new ArrayList<>();
        for(User r: listOfUsers){
            if(r.getName().toLowerCase().contains(string.toLowerCase())){
                help.add(r);
            }
        }
        listView.setAdapter(new UsersAdapter(activity, help));

    }

    public void addFriend(Friend friend) {
        usersModel.addFriend(friend);
    }

    public String getUserAva(String id){
        return usersModel.getAvatar(id);
    }
    






}
