package com.fpmi.vladcord.ui.friends_request_list;

import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.fpmi.vladcord.ui.friends_list.FriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendReqModel {
    private final DatabaseReference friendsRef;

    public FriendReqModel() {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends_request");

    }



    public void getDataFromDB(List<Friend> friendList, FriendsReqAdapter adapter, ProgressBar progressBar)
    {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(friendList.size() != 0)friendList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Friend friend = ds.getValue(Friend.class);
                    assert friend != null;
                    friendList.add(new Friend(friend));
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendsRef.addValueEventListener(vListener);
    }

    public void addFriend(Friend friend){
        DatabaseReference addFriendRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid());
        addFriendRef.child("Friends/".concat(friend.getuID())).setValue(friend);
        FirebaseDatabase.getInstance().getReference("Users").child(friend.getuID()).child("Friends/"
                .concat(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())).setValue(new Friend(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                Uri.parse(
                        "https://im0-tub-by.yandex.net/i?id=37805a40978d4f627f37dafa996381a8&n=13")
                        .toString()));
        deleteFriend(friend);
    }

    public void deleteFriend(Friend friend){
        DatabaseReference addFriendRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid());
        addFriendRef.child("Friends_request/".concat(friend.getuID())).removeValue();
    }
}
