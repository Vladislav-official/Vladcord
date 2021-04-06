package com.fpmi.vladcord.ui.messages_list;

import android.text.format.DateFormat;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.ui.friends_list.Friend;
import com.fpmi.vladcord.ui.friends_list.FriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageModel {
    private final DatabaseReference friendsRef;
    private final String friendId;

    public MessageModel(String friendId) {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends").child(friendId).child("Messages");
        this.friendId = friendId;
    }

    public void getDataFromDB(List<Message> messageList, MessageAdapter adapter)
    {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(messageList.size() != 0)messageList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Message message = ds.getValue(Message.class);
                    assert message != null;
                    messageList.add(message);
                }
                Collections.sort(messageList, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        if(o1.getMessageTime().getTime() > o2.getMessageTime().getTime()){
                            return 1;
                        }else if(o1.getMessageTime().getTime() < o2.getMessageTime().getTime())return -1;
                        return 0;
                    }
                });
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendsRef.addValueEventListener(vListener);
    }
    public void addMessage(Message message) {
        if (!message.textMessage.equals("")) {
            friendsRef.child(message.getMessageTime().toString())
                    .setValue(message);
            FirebaseDatabase.getInstance().getReference("Users").child(this.friendId).
                    child("Friends").child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()).child("Messages").child(message.getMessageTime().toString()).setValue(message);
        }
    }

}
