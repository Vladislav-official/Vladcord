package com.fpmi.vladcord.ui.groups;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.friends_list.RecycleFriendClick;
import com.fpmi.vladcord.ui.messages_list.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {

    //Список пользователей, его адаптер, а также экзэмпляр ViewModel
    private GroupsViewModel groupsViewModel;
    private List<String> listOfGroups;
    private GroupsAdapter groupAdapter;
    //Views
    private RecyclerView vListOfGroups;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(hasConnection(getApplicationContext())) {
            toolbar.setTitle(R.string.groups_title);
        }
        else{
            toolbar.setTitle(R.string.waiting_for_network);
        }
        setSupportActionBar(toolbar);
        init(this);


    }

    //Инициализация Views, toolbar and others
    private void init(Activity friendsActivity) {
        //Инициализация View из toolbar

        vListOfGroups = findViewById(R.id.groups_list);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipe_container);

        listOfGroups = new ArrayList<>();
        //Делаем так, чтобы спинер был виден(будет скрыт при загрузке данных из базы)
        progressBar.setVisibility(View.VISIBLE);

        //Обработка клика на элемент из списка друзей, переопределена через интерфейс RecycleFriendClick
        //В FriendsAdapter описан метод заполнения списка
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!hasConnection(getApplicationContext())){
                    getSupportActionBar().setTitle(R.string.waiting_for_network);
                }
                else{
                    getSupportActionBar().setTitle(R.string.groups_title);
                    vListOfGroups.setAdapter(groupAdapter);
                    vListOfGroups.setLayoutManager(new LinearLayoutManager(friendsActivity));
                    groupsViewModel = new GroupsViewModel(groupAdapter, progressBar);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        groupAdapter = new GroupsAdapter(new RecycleFriendClick() {
            @Override
            public void onClick(String friendId, String friendName, String groupName) {
                Intent intent = new Intent(friendsActivity, MessageActivity.class);
                intent.putExtra("friendId", friendId);
                intent.putExtra("friendName", friendName);
                intent.putExtra("privateMessage", "false");
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        }, friendsActivity, listOfGroups);
        vListOfGroups.setAdapter(groupAdapter);
        vListOfGroups.setLayoutManager(new LinearLayoutManager(friendsActivity));
        groupsViewModel = new GroupsViewModel(groupAdapter, progressBar);
        //Инициализация события изменения списка друзей, а также получения их списка
        groupsViewModel.getDataFromDB(listOfGroups);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        groupsViewModel.setStatusOnline();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            groupsViewModel.setStatusOffline(getString(R.string.last_seen));
        }
    }
    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }
}