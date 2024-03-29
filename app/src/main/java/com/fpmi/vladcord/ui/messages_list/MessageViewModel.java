package com.fpmi.vladcord.ui.messages_list;

import android.app.Activity;
import android.net.Uri;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.ui.FirebaseChangeInterface;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewModel extends ViewModel implements FirebaseChangeInterface {

    private MessageModel messageModel;
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private MutableLiveData<Uri> _attachedUri;
    LiveData<Uri> attachedUri = _attachedUri;

    public MessageViewModel() {
        messageModel = new MessageModel();
    }

    public void setChat(String friendId, MessageAdapter adapter, String groupName, RecyclerView recycleView) {
        messageModel = new MessageModel(friendId, this, groupName);
        this.recyclerView = recycleView;
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

    public void getNotificationsStatus(Activity activity, MenuItem item) {
        messageModel.getNotificationStatus(activity, item);
    }

    public void getGroupNotificationsStatus(Activity activity, MenuItem item) {
        messageModel.getGroupNotificationStatus(activity, item);
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

    public void addMessage(String privateMessage, Message message, Uri attachedPic) {
        messageModel.addMessage(privateMessage, message, attachedPic);
    }

    public void editMessage(String privateMessage, String chatId) {
        messageModel.editMessage(privateMessage, chatId);
    }

    public void editGroupMessage(String privateMessage, String chatId) {
        messageModel.editGroupMessage(privateMessage, chatId);
    }

    public void addGroupMessage(String privateMessage, Message message, Uri attachedPic) {
        messageModel.addGroupMessage(privateMessage, message, attachedPic);
    }

    public void removeSeenListener() {
        messageModel.removeSeen();
    }

    public void deleteMessage(Message message) {
        messageModel.deleteMessage(message);
    }

    public void deleteGroupMessage(Message message) {
        messageModel.deleteGroupMessage(message);
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
       // recyclerView.scrollToPosition(adapter.messages.size() - 1);
    }

    public void getSender(TextView textView, String id) {
        messageModel.getSender(textView, id);
    }

    public void getSenderAvatar(ImageView imageView, String id) {
        messageModel.getSenderAvatar(imageView, id);
    }
}