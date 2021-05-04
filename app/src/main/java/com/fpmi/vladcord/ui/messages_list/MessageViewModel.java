package com.fpmi.vladcord.ui.messages_list;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.MainActivity;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageViewModel extends ViewModel {

    private MessageModel messageModel;

    public MessageViewModel() {
    }
    public void setFriendId(String friendId, Activity activity){
    messageModel = new MessageModel(friendId, activity);
    }

    public void getDatatFromDB(List<Message> listOfMessages, MessageAdapter messageAdapter){

        messageModel.getDataFromDB(listOfMessages, messageAdapter);
    }
public void muteFriend(String status){
        messageModel.muteFriend(status);
}

public void getNotificationsStatus(MenuItem item){
        messageModel.getNotificationStatus(item);
}
public void sendMessage(String userId, ValueEventListener seenListener){
messageModel.sendMessage(userId);
}
    public void addMessage(Message message){
        messageModel.addMessage(message);
    }
    public void removeSeenListener(){
        messageModel.removeSeen();
    }
}