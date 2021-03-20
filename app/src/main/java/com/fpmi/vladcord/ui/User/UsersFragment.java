package com.fpmi.vladcord.ui.User;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.google.firebase.database.annotations.Nullable;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UsersFragment extends Fragment {
    private UsersViewModel usersViewModel;
    private EditText userSearch;
    private ListView vListOfUsers;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_users, container, false);

        Activity usersActivity = this.getActivity();
        init(root, usersActivity);
        initEventListeners(root, usersActivity);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int MESSAGE_LIST = 2;
        if (requestCode == MESSAGE_LIST) {
            if (resultCode == RESULT_OK) {
                System.out.println("NICE");
            }
        }
    }

    private void init(View root, Activity usersActivity)
    {
        usersViewModel = ViewModelProviders.of(this,
                new UserFactory(usersActivity, root))
                .get(UsersViewModel.class);
        userSearch = root.findViewById(R.id.search_input);
        vListOfUsers = (ListView) root.findViewById(R.id.users_list);
        usersViewModel.displayAllUsers(vListOfUsers);
    }

    private void initEventListeners(View root, Activity usersActivity)
    {

        userSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    usersViewModel.sortUsers(vListOfUsers, s.toString());
                }
                else{
                    usersViewModel.displayAllUsers(vListOfUsers);
                }
            }
        });
        vListOfUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {

                TextView name = (TextView) itemClicked.findViewById(R.id.name_user);
                TextView email = (TextView) itemClicked.findViewById(R.id.mail_user);
                TextView uID = (TextView) itemClicked.findViewById(R.id.id_user);
                    usersViewModel.addFriend(new Friend(name.getText().toString(),
                            email.getText().toString(), uID.getText().toString(),
                            usersViewModel.getUserAva(uID.getText().toString())));
            }});


    }

}
class UserFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Activity activity;

    private final View root;

    public UserFactory(@NonNull Activity activity, View root) {
        this.activity = activity;
        this.root = root;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == UsersViewModel.class) {
            return (T) new UsersViewModel(root, activity);
        }
        return null;
    }
}
