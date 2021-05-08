package com.fpmi.vladcord.ui.messages_list;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.ui.messages_list.Notifications.Client;
import com.fpmi.vladcord.ui.messages_list.Notifications.Data;
import com.fpmi.vladcord.ui.messages_list.Notifications.MyResponse;
import com.fpmi.vladcord.ui.messages_list.Notifications.Sender;
import com.fpmi.vladcord.ui.messages_list.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageModel {
    private final DatabaseReference friendsRef;
    private final String friendId;
    private final String myId;
    private final MessageViewModel messageViewModel;

    ValueEventListener seenListener;
    APIService apiService;
    boolean notify = false;

    public MessageModel(String friendId, MessageViewModel messageViewModel) {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Chats");
        this.myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.friendId = friendId;
        this.messageViewModel = messageViewModel;
    }

    public void getDataFromDB(List<Message> messageList)
    {

        ValueEventListener vListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(messageList.size() != 0){
                    messageList.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Message message = ds.getValue(Message.class);
                        if((message.getReceiver().equals(friendId) && message.getSender().equals(myId)) ||
                                (message.getReceiver().equals(myId) && message.getSender().equals(friendId))) {
                            messageList.add(message);
                        }
                    }
                messageViewModel.DataChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendsRef.addValueEventListener(vListener);
    }



    public void addMessage(Message message) {
        if (!message.getTextMessage().equals("")) {
            notify = true;
            FirebaseDatabase.getInstance().getReference("Chats").push().setValue(message);
            apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
            final String msg = message.getTextMessage();
            sendNotification(friendId, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), msg);
            updateToken(FirebaseInstanceId.getInstance().getToken());
        }
    }

    public void sendNotification(String receiver, String username, String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            message, username, receiver);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200){
                                if(response.body().success != 1){
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }


    public void muteFriend(String status){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("Friends")
                .child(friendId).child("notificationStatus").setValue(status);
    }

    public void getNotificationStatus(MenuItem item){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("Friends")
                .child(friendId).child("notificationStatus");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String status = snapshot.getValue(String.class);
                    if(status != null) {
                        item.setTitle(status);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        databaseReference.addValueEventListener(valueEventListener);
    }
    public void sendMessage(String userId){
        seenListener = friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if(message.getReceiver().equals(myId) && message.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void removeSeen(){
        friendsRef.removeEventListener(seenListener);
    }
}
