package com.fpmi.vladcord.ui.groups;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupeAddModel {
    //Путь в базе данных к нужным нам данным
    private final DatabaseReference friendsRef;
    //Экземпляр ViewModel для оповещения об изменении
    private final GroupeAddViewModel usersViewModel;

    public GroupeAddModel() {
        this.friendsRef = null;
        this.usersViewModel = null;
    }

    public GroupeAddModel(GroupeAddViewModel friendsViewModel) {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends");
        this.usersViewModel = friendsViewModel;
    }

    public void addGroup(List<String> list, String name){
        for(int i = 0; i < list.size(); ++i){
            FirebaseDatabase.getInstance().getReference("Groups").child(name)
                    .child("Users").child(list.get(i)).setValue(list.get(i));
            FirebaseDatabase.getInstance().getReference("Users").child(list.get(i))
                    .child("Groups").push().setValue(new Group(name, "Mute"));
        }
        FirebaseDatabase.getInstance().getReference("Groups").child(name).child("Creator")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Groups").push().setValue(new Group(name, "Mute"));
    }

    public void getDataFromDB(List<User> friendList)
    {
        List<String> friendsIds = new ArrayList<>();
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(friendsIds.size() != 0)friendsIds.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
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
    public void getDataFromDBS2(List<User> friendList, List<String > friendIds){
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(friendList.size() != 0) friendList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if(friendIds.contains(user.getuID()))
                    {
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


}
