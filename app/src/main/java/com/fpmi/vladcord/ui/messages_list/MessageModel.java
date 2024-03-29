package com.fpmi.vladcord.ui.messages_list;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.media.Image;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.messages_list.Notifications.Client;
import com.fpmi.vladcord.ui.messages_list.Notifications.Data;
import com.fpmi.vladcord.ui.messages_list.Notifications.MyResponse;
import com.fpmi.vladcord.ui.messages_list.Notifications.Sender;
import com.fpmi.vladcord.ui.messages_list.Notifications.Token;
import com.fpmi.vladcord.ui.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageModel {
    private final DatabaseReference friendsRef;
    private final DatabaseReference groupRef;
    private final String friendId;
    private final String myId;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private final String groupName;
    private final MessageViewModel messageViewModel;

    ValueEventListener seenListener;
    APIService apiService;
    boolean notify = false;

    public MessageModel() {
        friendsRef = null;
        groupRef = null;
        friendId = null;
        myId = null;
        groupName = null;
        messageViewModel = null;
    }

    public MessageModel(String friendId, MessageViewModel messageViewModel, String groupName) {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Chats");
        if(groupName != null) {
            this.groupRef = FirebaseDatabase.getInstance().getReference("Groups").child(String.valueOf(groupName.hashCode()))
                    .child("Chat");
        }
        else{
            groupRef = null;
        }
        this.myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.friendId = friendId;
        this.groupName = groupName;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        this.messageViewModel = messageViewModel;
    }

    public void getDataFromDB(List<Message> messageList) {

        ValueEventListener vListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (messageList.size() != 0) {
                    messageList.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    if ((message.getReceiver().equals(friendId) && message.getSender().equals(myId)) ||
                            (message.getReceiver().equals(myId) && message.getSender().equals(friendId))) {
                        messageList.add(message);
                    }
                    Log.d("check list of messages", message.getTextMessage());
                }

                messageViewModel.DataChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendsRef.addValueEventListener(vListener);
    }

    public void getGroupChatDataFromDB(List<Message> messageList) {

        ValueEventListener vListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (messageList.size() != 0) {
                    messageList.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    messageList.add(message);
                }
                messageViewModel.DataChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        groupRef.addValueEventListener(vListener);
    }



    public void deleteChat() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Friends").child(friendId).removeValue();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(friendId)
                .child("Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
    }

    public void leaveGroupChat() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Groups").child(String.valueOf(groupName.hashCode())).removeValue();
        FirebaseDatabase.getInstance().getReference("Groups")
                .child(String.valueOf(groupName.hashCode())).child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

    }

    public void sendNotification(String type, String receiver, String username, String message, String privateMessage) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data;
                    if (type.equals("voiceMessage")) {
                        data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                message, username, receiver, "voice message", groupName);
                    }
                    else{
                        data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                message, username, receiver, privateMessage, groupName);
                    }
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success != 1) {
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

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token1);
    }


    public void muteFriend(String status) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("Friends")
                .child(friendId).child("notificationStatus").setValue(status);
    }

    public void muteGroup(String status) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("Groups").child(String.valueOf(groupName.hashCode()))
                .child("groupNotificationStatus").setValue(status);
    }

    public void getNotificationStatus(Activity activity, MenuItem item) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("Friends")
                .child(friendId).child("notificationStatus");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if (status != null) {
                    if (status.equals(activity.getString(R.string.mute))) {
                        item.setTitle(activity.getString(R.string.mute));
                    } else {
                        item.setTitle(activity.getString(R.string.unmute_friend));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    public void getGroupNotificationStatus(Activity activity, MenuItem item) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserId)
                .child("Groups").child(String.valueOf(groupName.hashCode())).child("groupNotificationStatus");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if (status != null) {
                    if (status.equals(activity.getString(R.string.mute))) {
                        item.setTitle(activity.getString(R.string.mute));
                    } else {
                        item.setTitle(activity.getString(R.string.unmute_friend));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    public void addGroupMessage(String privateMessage, Message message, Uri attachedPic) {
        if (!message.getTextMessage().equals("")) {
            notify = true;
            String chatId = FirebaseDatabase.getInstance().getReference("Groups").child(String.valueOf(groupName.hashCode())).
                    child("Chat").push().getKey();
            message.setChatId(chatId);
            FirebaseDatabase.getInstance().getReference("Groups").child(String.valueOf(groupName.hashCode())).
                    child("Chat").child(message.getChatId()).setValue(message);
            apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
            final String msg = message.getTextMessage();
            FirebaseDatabase.getInstance().getReference("Groups").child(String.valueOf(groupName.hashCode())).child("Users")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String friend = ds.getValue(String.class);
                                if (!friend.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    sendNotification(message.getType(), friend, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), msg, privateMessage);
                                    updateToken(FirebaseInstanceId.getInstance().getToken());
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }
    }
    public void addMessage(String privateMessage, Message message, Uri attachedPic) {
        if (attachedPic != null) {
            String chatId = FirebaseDatabase.getInstance().getReference("Chats").push().getKey();
            message.setChatId(chatId);
            StorageReference ref = storageReference.child("Images").child("Chats").child(message.getChatId());
            ref.putFile(attachedPic)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            storageReference.child("Images").child("Chats").child(message.getChatId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    message.setAttachedPic(uri.toString());
                                    notify = true;
                                    FirebaseDatabase.getInstance().getReference("Chats").child(message.getChatId()).setValue(message);
                                    apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
                                    final String msg = message.getTextMessage();
                                    sendNotification(message.getType(), friendId, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), msg, privateMessage);
                                    updateToken(FirebaseInstanceId.getInstance().getToken());
                                    //Handle whatever you're going to do with the URL here
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        }
        else if (!message.getTextMessage().equals("")) {
            notify = true;
            String chatId = FirebaseDatabase.getInstance().getReference("Chats").push().getKey();
            message.setChatId(chatId);
            FirebaseDatabase.getInstance().getReference("Chats").child(message.getChatId()).setValue(message);
            apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
            final String msg = message.getTextMessage();
            sendNotification(message.getType(), friendId, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), msg, privateMessage);
            updateToken(FirebaseInstanceId.getInstance().getToken());
        }
    }

    public void editMessage(String privateMessage, String chatId) {
        FirebaseDatabase.getInstance().getReference("Chats").child(chatId).child("textMessage")
                .setValue(privateMessage);
    }

    public void editGroupMessage(String privateMessage, String chatId) {
        FirebaseDatabase.getInstance().getReference("Groups").child(String.valueOf(groupName.hashCode())).
                child("Chat").child(chatId).child("textMessage").setValue(privateMessage);
    }

    public void sendMessage(String userId) {
        seenListener = friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message.getReceiver().equals(myId)) {
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

    public void sendGroupMessage(String userId) {
        seenListener = groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (!message.getSender().equals(myId)) {
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

    public void deleteMessage(Message message) {
        Query deleteQuery = friendsRef.child(message.getChatId());
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deleteQuery.getRef().removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteGroupMessage(Message message) {
        Query deleteQuery = groupRef.child(message.getChatId());
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deleteQuery.getRef().removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeSeen() {
        friendsRef.removeEventListener(seenListener);
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

    public void getSender(TextView textView, String id) {
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        textView.setText(snapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getSenderAvatar(ImageView imageView, String id) {
        FirebaseDatabase.getInstance().getReference("Users").child(id).child("urlAva")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String url = snapshot.getValue(String.class);
                        Picasso.get()
                                .load(url)
                                .into(imageView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
