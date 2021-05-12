package com.fpmi.vladcord.ui.messages_list;

import android.view.MenuItem;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.FirebaseChangeInterface;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageViewModel extends ViewModel implements FirebaseChangeInterface {

    private MessageModel messageModel;
    private MessageAdapter adapter;

    public MessageViewModel() {
        messageModel = new MessageModel();
    }

    public void setChat(String friendId, MessageAdapter adapter, String groupName) {
        messageModel = new MessageModel(friendId, this, groupName);
        this.adapter = adapter;
    }

    public void getDatatFromDB(List<Message> listOfMessages) {
        messageModel.getDataFromDB(listOfMessages);
    }

    public void getGroupChatDataFromDB(List<Message> list) {
        messageModel.getGroupChatDataFromDB(list);
    }

    public void muteFriend(String status) {
        messageModel.muteFriend(status);
    }

    public void muteGroup(String status) {
        messageModel.muteGroup(status);
    }

    public void getNotificationsStatus(MenuItem item) {
        messageModel.getNotificationStatus(item);
    }

    public void getGroupNotificationsStatus(MenuItem item) {
        messageModel.getGroupNotificationStatus(item);
    }

    public void sendMessage(String userId, ValueEventListener seenListener) {
        messageModel.sendMessage(userId);
    }

    public void sendGroupMessage(String userId, ValueEventListener seenListener) {
        messageModel.sendGroupMessage(userId);
    }

    public void setStatusOnline() {
        messageModel.setStatusOnline();
    }

    public void setStatusOffline(String status) {
        messageModel.setStatusOffline(status);
    }

    public void addMessage(Message message) {
        messageModel.addMessage(message);
    }

    public void addGroupMessage(Message message) {
        messageModel.addGroupMessage(message);
    }

    public void removeSeenListener() {
        messageModel.removeSeen();

    }

    public void deleteChat() {
        messageModel.deleteChat();
    }

    public void leaveGroupChat() {
        messageModel.leaveGroupChat();
    }

    @Override
    public void DataChanged() {
        adapter.notifyDataSetChanged();
    }

    public void getSender(TextView textView, String id) {
        messageModel.getSender(textView, id);
    }
}