package com.fpmi.vladcord.ui.friends_list;

import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.FirebaseChangeInterface;
import com.fpmi.vladcord.ui.User.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsViewModel extends ViewModel implements FirebaseChangeInterface {
    //экземпляр модели
    private final FriendModel friendModel;
    //View которые будут меняться при изменении данных

    final private FriendsFragment friendsFragment;
    final private FriendsAdapter adapter;

    public FriendsViewModel(FriendsFragment friendsFragment, FriendsAdapter adapter, ProgressBar progressBar) {
        friendModel = new FriendModel(this);
        this.friendsFragment = friendsFragment;
        this.adapter = adapter;
    }

    public void getDataFromDB(List<User> listOfFriends) {
        //Здесь посылаем запрос в модель на инициализацию слушателя изменений в базе
        friendModel.getDataFromDB(listOfFriends);
    }

    //обычная сортировка по принципу включения части текста в имя пользователя
    public List<User> sortFriends(List<User> listOfFriends, String string) {
        List<User> resultListOfFriends = new ArrayList<>();
        for (User r : listOfFriends) {
            if (r.getName().toLowerCase().contains(string.toLowerCase())) {
                resultListOfFriends.add(r);
            }
        }
        return resultListOfFriends;
    }

    //Переопределение интерфейса слушателя, срабатывает при изменении данных в базе пользователей
    @Override
    public void DataChanged() {
        adapter.notifyDataSetChanged();
        friendsFragment.offProgressBar();
    }
}
