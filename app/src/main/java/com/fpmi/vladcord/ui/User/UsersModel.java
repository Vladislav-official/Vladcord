package com.fpmi.vladcord.ui.User;

import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

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

    public  void getDataFromDB(List<User> listOfUsers, UsersAdapter adapter, ProgressBar progressBar)
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
                                .toString(), "Online")))
                    {
                        listOfUsers.add(new User(user));
                    }
                }
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        userRef.addValueEventListener(vListener);
    }

    public void addFriend(Friend friend){

            DatabaseReference addFriendRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(friend.getuID());
            addFriendRef.child("Friends_request/".concat(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid())).setValue(new Friend(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    Uri.parse(
                            "https://im0-tub-by.yandex.net/i?id=37805a40978d4f627f37dafa996381a8&n=13")
                            .toString()));
        checkFriend(friend);
        }

    public void checkFriend( Friend friend)
    {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends");
        List<Friend> list = new ArrayList<>();
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Friend friend = ds.getValue(Friend.class);
                    assert friend != null;
                    list.add(new Friend(friend));
                }
                for(Friend r: list) {
                    if (r.getEmail().equals(friend.getEmail())) {
                        DatabaseReference addFriendRef = FirebaseDatabase.getInstance().getReference("Users").child(friend.getuID());
                        addFriendRef.child("Friends_request/".concat(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid())).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        firebaseDatabase.addValueEventListener(vListener);
    }

}
