package com.fpmi.vladcord.ui.friends_request_list;

import android.net.Uri;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.fpmi.vladcord.ui.friends_list.FriendsAdapter;
import com.fpmi.vladcord.ui.friends_list.FriendsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendReqModel {
    private final DatabaseReference friendsRef;
    private final FriendsReqViewModel friendsReqViewModel;

    public FriendReqModel(FriendsReqViewModel friendsReqViewModel) {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends_request");
        this.friendsReqViewModel = friendsReqViewModel;

    }

    public FriendReqModel() {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends_request");
        this.friendsReqViewModel = null;
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
                    String friend = ds.getValue(String.class);
                    assert friend != null;
                    friendsIds.add(friend);
                }
                getDataFromDBS2(friendList, friendsIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendsRef.addValueEventListener(vListener);
    }
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
                friendsReqViewModel.DataChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    public void addFriend(String friendId){
        DatabaseReference addFriendRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid());
        addFriendRef.child("Friends").child(friendId).setValue(new Friend(friendId, "Mute"));
        FirebaseDatabase.getInstance().getReference("Users").child(friendId)
                .child("Friends").child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid())
                .setValue(new Friend(FirebaseAuth.getInstance().getCurrentUser().getUid(), "Mute"));
        deleteFriend(friendId);
    }

    public void deleteFriend(String friendId){
        DatabaseReference addFriendRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid());
        addFriendRef.child("Friends_request").child(friendId).removeValue();
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
