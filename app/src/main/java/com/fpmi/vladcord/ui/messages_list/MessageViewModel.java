package com.fpmi.vladcord.ui.messages_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MessageViewModel extends ViewModel {

    private MessageModel messageModel;

    public MessageViewModel() {
    }
    public void setFriendId(String friendId){
    messageModel = new MessageModel(friendId);
    };

    public void getDatatFromDB(List<Message> listOfMessages, MessageAdapter messageAdapter){
        messageModel.getDataFromDB(listOfMessages, messageAdapter);
    }

    public void addMessage(Message message){
        messageModel.addMessage(message);
    }
}