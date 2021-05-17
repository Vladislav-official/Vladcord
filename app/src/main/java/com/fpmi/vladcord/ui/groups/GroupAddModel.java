package com.fpmi.vladcord.ui.groups;

import android.app.Activity;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupAddModel {
    //Путь в базе данных к нужным нам данным
    private final DatabaseReference friendsRef;
    //Экземпляр ViewModel для оповещения об изменении
    private final GroupAddViewModel usersViewModel;

    public GroupAddModel() {
        this.friendsRef = null;
        this.usersViewModel = null;
    }

    public GroupAddModel(GroupAddViewModel friendsViewModel) {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends");
        this.usersViewModel = friendsViewModel;
    }

    public void addGroup(Activity activity, List<String> list, String name) {
        FirebaseDatabase.getInstance().getReference("Groups")
                .child(String.valueOf(name.hashCode())).child("GroupName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String groupName = snapshot.getValue(String.class);
                if (groupName != null) {
                    Toast.makeText(activity, "Group with this name is already exist", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < list.size(); ++i) {
                        FirebaseDatabase.getInstance().getReference("Groups").child(String.valueOf(name.hashCode()))
                                .child("Users").child(list.get(i)).setValue(list.get(i));
                        FirebaseDatabase.getInstance().getReference("Groups").child(String.valueOf(name.hashCode()))
                                .child("GroupName").setValue(name);
                        FirebaseDatabase.getInstance().getReference("Users").child(list.get(i))
                                .child("Groups").child(String.valueOf(name.hashCode())).setValue(new Group(name, "Mute"));
                    }
                    FirebaseDatabase.getInstance().getReference("Groups").child(String.valueOf(name.hashCode())).child("Creator")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Groups").child(String.valueOf(name.hashCode())).setValue(new Group(name, "Mute"));
                    Toast.makeText(activity, activity.getString(R.string.group_with_name) + name + activity.getString(R.string.created), Toast.LENGTH_LONG)
                            .show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getDataFromDB(List<User> friendList) {
        List<String> friendsIds = new ArrayList<>();
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (friendsIds.size() != 0) friendsIds.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Friend friend = ds.getValue(Friend.class);
                    friendsIds.add(friend.getFriendId());
                }
                getDataFromDBS2(friendList, friendsIds);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendsRef.addValueEventListener(vListener);
    }

    //Тут через полученный выше список id пользователй, происходит считывание их данных
    public void getDataFromDBS2(List<User> friendList, List<String> friendIds) {
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (friendList.size() != 0) friendList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if (friendIds.contains(user.getuID())) {
                        friendList.add(new User(user));
                    }
                }
                //Сообщаем модели, что список изменился
                usersViewModel.DataChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setStatusOnline() {
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status").setValue("Online");
    }

    public void setStatusOffline(String status) {
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status")
                .setValue(status + " " + (DateFormat.format("HH:mm", (new Date().getTime())))
                        + " " + DateFormat.format("dd:MM", (new Date().getTime())));
    }

}
