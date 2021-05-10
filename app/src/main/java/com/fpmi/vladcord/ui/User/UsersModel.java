package com.fpmi.vladcord.ui.User;

import android.net.Uri;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.fpmi.vladcord.ui.friends_list.FriendModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//think about ListAdapter and diffUtil and ViewHolder
public class UsersModel {
    private final DatabaseReference userRef;
    private final UsersViewModel usersViewModel;

    public UsersModel(UsersViewModel usersViewModel) {
        this.userRef = FirebaseDatabase.getInstance().getReference("Users");
        this.usersViewModel = usersViewModel;
    }

    public  void getDataFromDB(List<User> listOfUsers)
    {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (listOfUsers.size() != 0) listOfUsers.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        User user = ds.getValue(User.class);
                        if(user != null) {
                            if (!user.getuID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                listOfUsers.add(new User(user));
                            }
                        }
                    }
usersViewModel.DataChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        userRef.addValueEventListener(vListener);
    }

    public void addFriend(String friendId){
           DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance()
                    .getReference("Users").child(friendId).child("Friends")
                   .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Friend friend = snapshot.getValue(Friend.class);
                    if(friend == null){
                        FirebaseDatabase.getInstance()
                                .getReference("Users").child(friendId).child("Friends_request")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            };
firebaseDatabase.addValueEventListener(valueEventListener);
        }
    public void setStatusOnline(){
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status").setValue("Online");
    }
    public void setStatusOffline(String status){
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status")
                .setValue(status + " " + (DateFormat.format("HH:mm", (new Date().getTime())))
                        + " " + DateFormat.format("dd:MM", (new Date().getTime())));
    }


}
