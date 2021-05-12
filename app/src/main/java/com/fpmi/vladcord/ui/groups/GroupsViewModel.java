package com.fpmi.vladcord.ui.groups;

import android.view.View;
import android.widget.ProgressBar;

import com.fpmi.vladcord.ui.FireChangeInterface;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.friends_list.FriendModel;
import com.fpmi.vladcord.ui.friends_list.FriendsAdapter;

import java.util.List;

public class GroupsViewModel implements FireChangeInterface {

    //экземпляр модели
    private final GroupsModel groupsModel;
    //View которые будут меняться при изменении данных
    private ProgressBar progressBar;
    private GroupsAdapter adapter;

    public GroupsViewModel( GroupsAdapter adapter, ProgressBar progressBar) {
        groupsModel = new GroupsModel(this);
        this.adapter = adapter;
        this.progressBar = progressBar;
    }

    public void getDataFromDB(List<String> list){
        //Здесь посылаем запрос в модель на инициализацию слушателя изменений в базе
        groupsModel.getDataFromDB(list);
    }



    //Переопределение интерфейса слушателя, срабатывает при изменении данных в базе пользователей
    @Override
    public void DataChanged() {
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }
}
