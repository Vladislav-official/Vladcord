package com.fpmi.vladcord.ui.friends_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.messages_list.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {


    final private FriendsFragment friendsFragment = this;
    //Список пользователей, его адаптер, а также экзэмпляр ViewModel
    private FriendsViewModel friendsViewModel;
    private List<User> listOfFriends;
    private FriendsAdapter friendsAdapter;
    //Views
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView vListOfFriends;
    private TextView titleToolbar;
    private EditText friendSearch;
    private ProgressBar progressBar;
    private ImageView search_view;


    //Создание фрагмента в MainActivity
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        //Если пользователь не зарегестрирован, показываем пустой фрагмент
        if (FirebaseAuth.getInstance().getCurrentUser() != null && this.getActivity() != null) {
            init(root, this.getActivity());
            initEventListeners(root, getActivity());
        }
        return root;

    }


    //Инициализация Views, toolbar and others
    private void init(View root, Activity friendsActivity) {
        //Инициализация View из toolbar
        search_view = friendsActivity.findViewById(R.id.search_view);
        friendSearch = friendsActivity.findViewById(R.id.search_input);
        titleToolbar = friendsActivity.findViewById(R.id.title_toolbar);
        titleToolbar.setTextAppearance(R.style.RobotoBoldTextAppearance);
        if (getContext() != null && hasConnection(getContext())) {
            titleToolbar.setText(R.string.waiting_for_network);
        } else {
            titleToolbar.setText(R.string.friends_title);
        }
        vListOfFriends = root.findViewById(R.id.friends_list);
        progressBar = root.findViewById(R.id.progress_bar);
        swipeRefreshLayout = root.findViewById(R.id.swipe_container);
        listOfFriends = new ArrayList<>();
        //Делаем так, чтобы спинер был виден(будет скрыт при загрузке данных из базы)
        progressBar.setVisibility(View.VISIBLE);

        //Обработка клика на элемент из списка друзей, переопределена через интерфейс RecycleFriendClick
        //В FriendsAdapter описан метод заполнения списка
        friendsAdapter = new FriendsAdapter(new RecycleFriendClick() {
            @Override
            public void onClick(String friendId, String friendName, String groupName) {
                Intent intent = new Intent(friendsActivity, MessageActivity.class);
                intent.putExtra("friendId", friendId);
                intent.putExtra("friendName", friendName);
                intent.putExtra("privateMessage", "true");
                intent.putExtra("groupName", groupName);
                startActivity(intent);
            }
        }, friendsActivity, listOfFriends);
        vListOfFriends.setAdapter(friendsAdapter);
        vListOfFriends.setLayoutManager(new LinearLayoutManager(friendsActivity));
        friendsViewModel = new FriendsViewModel(friendsFragment, friendsAdapter, progressBar);
        //Инициализация события изменения списка друзей, а также получения их списка
        friendsViewModel.getDataFromDB(listOfFriends);
    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return false;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return false;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return false;
        }
        return true;
    }

    private void initEventListeners(View root, Activity friendsActivity) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (hasConnection(getContext())) {
                    titleToolbar.setTextSize(25);
                    titleToolbar.setText(R.string.waiting_for_network);
                } else {
                    titleToolbar.setText(R.string.friends_title);
                    vListOfFriends.setAdapter(friendsAdapter);
                    vListOfFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
                    friendsViewModel = new FriendsViewModel(friendsFragment, friendsAdapter, progressBar);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //обработка событий нажатия на кнопку поиска, а также обработка ввода в поле поиска текста
        search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_view.setVisibility(View.GONE);
                friendSearch.setVisibility(View.VISIBLE);
                titleToolbar.setVisibility(View.GONE);
                friendSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) friendsActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(friendSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        friendSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            //При изменении текста пользователь по сути проводит сортировку по текущему списку
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    vListOfFriends.setAdapter(new FriendsAdapter(new RecycleFriendClick() {
                        @Override
                        public void onClick(String friendId, String friendName, String groupName) {
                            Intent intent = new Intent(friendsActivity, MessageActivity.class);
                            intent.putExtra("friendId", friendId);
                            intent.putExtra("friendName", friendName);
                            intent.putExtra("privateMessage", "true");
                            startActivity(intent);
                        }
                    }, friendsActivity, friendsViewModel.sortFriends(listOfFriends, s.toString())));
                } else {
                    vListOfFriends.setAdapter(new FriendsAdapter(new RecycleFriendClick() {
                        @Override
                        public void onClick(String friendId, String friendName, String groupName) {
                            Intent intent = new Intent(friendsActivity, MessageActivity.class);
                            intent.putExtra("friendId", friendId);
                            intent.putExtra("friendName", friendName);
                            intent.putExtra("privateMessage", "true");
                            intent.putExtra("groupName", groupName);
                            startActivity(intent);
                        }
                    }, friendsActivity, friendsViewModel.sortFriends(listOfFriends, "")));
                }
            }
        });
    }


    public void offProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
