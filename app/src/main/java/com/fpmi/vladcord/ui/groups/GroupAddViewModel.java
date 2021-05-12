package com.fpmi.vladcord.ui.groups;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.FirebaseChangeInterface;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.User.UsersAdapter;

import java.util.List;

public class GroupAddViewModel extends ViewModel implements FirebaseChangeInterface {
    //экземпляр модели
    private GroupAddModel groupAddModel;
    //View которые будут меняться при изменении данных
    private ProgressBar progressBar;
    private UsersAdapter adapter;

    public GroupAddViewModel(UsersAdapter adapter, ProgressBar progressBar) {
        groupAddModel = new GroupAddModel(this);
        this.adapter = adapter;
        this.progressBar = progressBar;
    }

    public void addGroup(Activity activity, List<String> list, String title) {
        groupAddModel.addGroup(activity, list, title);
    }

    public void getDataFromDB(List<User> list) {
        groupAddModel.getDataFromDB(list);
    }


    public void setStatusOnline() {
        groupAddModel.setStatusOnline();
    }

    public void setStatusOffline(String status) {
        groupAddModel.setStatusOffline(status);
    }

    @Override
    public void DataChanged() {
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

}
