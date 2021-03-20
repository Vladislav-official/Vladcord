package com.fpmi.vladcord.ui.friends_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.UsersViewModel;
import com.fpmi.vladcord.ui.messages_list.MessageActivity;
import com.google.firebase.database.annotations.Nullable;

import static android.app.Activity.RESULT_OK;

public class FriendsFragment extends Fragment {

    private FriendsViewModel friendsViewModel;
    private ListView listOfFriends;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        init(root, this.getActivity());

        /*FriendsFragment fragmentActivity = this;
        listOfFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                TextView t;
                t = itemClicked.findViewById(R.id.id_friend);
                intent.putExtra("friend", t.getText());
                startActivity(intent);
            }});*/

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
    private void init(View root, Activity friendsActivity)
    {
        friendsViewModel = ViewModelProviders.of(this,
                new FriendFactory(friendsActivity, root))
                .get(FriendsViewModel.class);
        listOfFriends = (ListView) root.findViewById(R.id.listOfFriends);
        friendsViewModel.displayAllFriends(listOfFriends);
    }

    private void initEventListeners(View root, Activity usersActivity)
    {
        /*UsersFragment fragmentActivity = this;

        listOfUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                TextView textView = (TextView)itemClicked.findViewById(R.id.name_user);
                System.out.println(textView.getText());

            }});*/
    }


}
class FriendFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Activity activity;

    private final View root;

    public FriendFactory(@NonNull Activity activity, View root) {
        this.activity = activity;
        this.root = root;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == FriendsViewModel.class) {
            return (T) new FriendsViewModel(activity, root);
        }
        return null;
    }
}