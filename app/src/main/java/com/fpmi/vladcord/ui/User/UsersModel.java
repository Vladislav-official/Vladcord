package com.fpmi.vladcord.ui.User;

import android.text.format.DateFormat;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

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

//think about ListAdapter and diffUtil and ViewHolder
public class UsersModel {
    private final DatabaseReference userRef;

    public UsersModel(UsersViewModel usersViewModel) {
        this.userRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void getDataFromDB(MutableLiveData<List<User>> listOfUsers) {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (listOfUsers.getValue().size() != 0) listOfUsers.setValue(new ArrayList<>());
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        User user = ds.getValue(User.class);
                        if (user.getuID() != null) {
                            if (!user.getuID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                List<User> list = listOfUsers.getValue();
                                list.add(new User(user));
                                listOfUsers.setValue(list);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        userRef.addValueEventListener(vListener);
    }

    public void addFriend(String friendId) {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance()
                .getReference("Users").child(friendId).child("Friends")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Friend friend = snapshot.getValue(Friend.class);
                System.out.println(friend);
                if (friend == null) {
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
