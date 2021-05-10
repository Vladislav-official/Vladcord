package com.fpmi.vladcord.ui;

import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.messages_list.Message;

public interface FireChangeInterface {
    void DataChanged();

    default void DataChanged(User user) {

    }
    default void DataChanged(Message message){}
}
