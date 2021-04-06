package com.fpmi.vladcord.ui.User;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.ui.friends_list.Friend;
import com.fpmi.vladcord.ui.friends_list.FriendModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
//think about ListAdapter and diffUtil and ViewHolder
public class UsersModel {
    private final DatabaseReference userRef;

    public UsersModel() {
        this.userRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public  void getDataFromDB(List<User> listOfUsers, UsersAdapter adapter)
    {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if(listOfUsers.size() != 0) listOfUsers.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if(!user.equals(new User(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        Uri.parse(
                                "https://im0-tub-by.yandex.net/i?id=37805a40978d4f627f37dafa996381a8&n=13")
                                .toString())))
                    {
                        listOfUsers.add(new User(user));
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        userRef.addValueEventListener(vListener);
    }

    public void addFriend(User friend){
            DatabaseReference addFriendRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid());
                        addFriendRef.child("Friends/".concat(friend.getuID())).setValue(friend);
        }


}
