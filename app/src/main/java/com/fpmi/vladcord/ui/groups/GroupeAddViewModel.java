package com.fpmi.vladcord.ui.groups;

import android.view.View;
import android.widget.ProgressBar;

import com.fpmi.vladcord.ui.FirebaseChangeInterface;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.User.UsersAdapter;

import java.util.List;

public class GroupeAddViewModel implements FirebaseChangeInterface {
    //экземпляр модели
    private GroupeAddModel groupeAddModel;
    //View которые будут меняться при изменении данных
    private ProgressBar progressBar;
    private UsersAdapter adapter;

    public GroupeAddViewModel(UsersAdapter adapter, ProgressBar progressBar) {
        groupeAddModel = new GroupeAddModel(this);
        this.adapter = adapter;
        this.progressBar = progressBar;
    }

    public void addGroup(List<String> list, String title){
        groupeAddModel.addGroup(list, title);
    }
    public void getDataFromDB(List<User> list){
        groupeAddModel.getDataFromDB(list);
    }

    @Override
    public void DataChanged() {
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
}
