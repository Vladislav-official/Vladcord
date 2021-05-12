package com.fpmi.vladcord.ui.friends_list;
//Интерфейс нужный для реализации нажатия на эдемент списка во фрагменте
public interface RecycleFriendClick {
    void onClick(String friendId, String friendName, String groupName);
}
