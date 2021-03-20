package com.fpmi.vladcord.ui.User;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
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
import java.util.HashMap;
import java.util.List;

public class UsersModel {
    private FirebaseListAdapter<User> adapter;
    private final DatabaseReference userRef;
    private  String avatar;

    public UsersModel(Activity activity) {
        this.userRef = FirebaseDatabase.getInstance().getReference("Users");

        setAdapter(activity);
    }
    public UsersModel() {
        this.userRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void setAdapter(Activity activity){
        adapter = new FirebaseListAdapter<User>(activity, User.class, R.layout.list_user,
                this.userRef.orderByChild("name")) {
            @Override
            protected void populateView(View v, User model, int position) {
                TextView name, uID, email;
                ImageView urlAva;
                if(model.getuID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    v.setVisibility(v.GONE);
                }
                    urlAva = v.findViewById(R.id.user_avatar);
                    name = v.findViewById(R.id.name_user);
                    email = v.findViewById(R.id.mail_user);
                    uID = v.findViewById(R.id.id_user);
                    urlAva.setImageURI(Uri.parse(model.getUrlAva()));

                    name.setText(model.getName());
                    email.setText(model.getEmail());
                    uID.setText(model.getuID());
            }
        };
    }

    public FirebaseListAdapter<User> getAdapter(){return this.adapter;};

    public void getDataFromDB(List<User> listOfUsers)
    {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(listOfUsers.size() != 0)listOfUsers.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    listOfUsers.add(new User(user));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userRef.addValueEventListener(vListener);
    }

    public void addFriend(Friend friend){
        List<Friend> friendList = new ArrayList<>();
        FriendModel friendModel = new FriendModel();
        friendModel.getDataFromDB(friendList);
        DatabaseReference addFriendRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid());
                if(!friendList.contains(friend)) {
                    addFriendRef.child("Friends/".concat(friend.getuID())).setValue(friend);
                }
    }

    public String getAvatar(String id){
        return "https://im0-tub-by.yandex.net/i?id=37805a40978d4f627f37dafa996381a8&n=13";
    }

}
