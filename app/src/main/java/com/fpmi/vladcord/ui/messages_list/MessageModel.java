package com.fpmi.vladcord.ui.messages_list;

import android.app.Activity;
import android.view.MenuItem;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageModel {
    private final DatabaseReference friendsRef;
    private boolean isNotificationsOn;
    private boolean isActivityHasFocus;
    private final String friendId;
    private final Activity activity;
    APIService apiService;
    boolean notify = false;

    public MessageModel(String friendId, Activity activity, boolean isNotificationsOn) {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends").child(friendId).child("Messages");
        this.friendId = friendId;
        this.isNotificationsOn = isNotificationsOn;
        this.activity = activity;
    }

    public void getDataFromDB(List<Message> messageList, MessageAdapter adapter)
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
                        assert message != null;
                        messageList.add(message);
                    }
                    Collections.sort(messageList, new Comparator<Message>() {
                        @Override
                        public int compare(Message o1, Message o2) {
                            if (o1.getMessageTime().getTime() > o2.getMessageTime().getTime()) {
                                return 1;
                            } else if (o1.getMessageTime().getTime() < o2.getMessageTime().getTime())
                                return -1;
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
            notify = true;
            friendsRef.child(message.getMessageTime().toString())
                    .setValue(message);
            FirebaseDatabase.getInstance().getReference("Users").child(this.friendId).
                    child("Friends").child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()).child("Messages").child(message.getMessageTime().toString()).setValue(message);
            apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
            final String msg = message.textMessage;
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
                .child(friendId).child("NotificationsStatus").setValue(status);
    }

    public void getNotificationStatus(MenuItem item){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("Friends")
                .child(friendId).child("NotificationsStatus");
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

}
