package com.fpmi.vladcord.ui.friends_list;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FriendModel {
    private FirebaseListAdapter<Friend> adapter;
    private DatabaseReference friendsRef;

    public FriendModel() {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends");
        adapter = null;

    }

    public void setAdapter(Activity activity) {
        if(friendsRef == null){
            return;
        }
        adapter = new FirebaseListAdapter<Friend>(activity, Friend.class, R.layout.list_friend,
                this.friendsRef.orderByChild("name")) {
            @Override
            protected void populateView(View v, Friend model, int position) {
                TextView name, uID, email;
                ImageView friendAva;
                name = v.findViewById(R.id.name_friend);
                email = v.findViewById(R.id.mail_friend);
                uID = v.findViewById(R.id.id_friend);
                friendAva = v.findViewById(R.id.friend_avatar);
                name.setText(model.getName());
                email.setText(model.getEmail());
                uID.setText(model.getuID());
                friendAva.setImageURI(Uri.parse(model.getUrl()));
            }
        };
    }

    public void getDataFromDB(List<Friend> friendList)
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendsRef.addValueEventListener(vListener);
    }

    public FirebaseListAdapter<Friend> getAdapter() {
        return adapter;
    }
}
