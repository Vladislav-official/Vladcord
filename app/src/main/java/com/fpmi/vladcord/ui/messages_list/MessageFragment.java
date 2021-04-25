package com.fpmi.vladcord.ui.messages_list;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MessageFragment extends Fragment {

    private boolean isNotificationsOn;

    private MessageViewModel messageViewModel;
    private String notificationsStatus;
    private ImageView submitButton, emojiButton;
    private EmojIconActions emojIconActions;
    private EmojiconEditText emojiconEditText;
    private List<Message> listOfMessages;
    private MessageAdapter messageAdapter;
    private RecyclerView vListOfMessages;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.private_message, container, false);
        init(root, this.getActivity(), getArguments().getString("friendId"));
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emojiconEditText.getText() != null) {
                    messageViewModel.addMessage(new Message(FirebaseAuth.getInstance().getCurrentUser()
                            .getDisplayName(), emojiconEditText.getText().toString()));
                    vListOfMessages.smoothScrollToPosition(messageAdapter.getItemCount());
                    emojiconEditText.setText("");
                }
            }
        });

        return root;
    }

    public static MessageFragment newInstance(String friendId) {
        MessageFragment messageFragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString("friendId", friendId);
        messageFragment.setArguments(args);
        return messageFragment;
    }


    private void init(View root, Activity activity, String friendId) {
        messageViewModel = new MessageViewModel();
        isNotificationsOn = true;
        messageViewModel.setFriendId(friendId, activity, true);

        submitButton = root.findViewById(R.id.submit_button);
        emojiconEditText = root.findViewById(R.id.message_text);
        emojiButton = root.findViewById(R.id.emoji_button);
        setHasOptionsMenu(true);
        emojIconActions = new EmojIconActions(activity.getApplicationContext(), root,
                emojiconEditText, emojiButton);
        emojIconActions.ShowEmojIcon();

        listOfMessages = new ArrayList<>();
        vListOfMessages = root.findViewById(R.id.message_list);
        messageAdapter = new MessageAdapter(activity, listOfMessages);
        vListOfMessages.setAdapter(messageAdapter);
        vListOfMessages.setLayoutManager(new LinearLayoutManager(activity));
        vListOfMessages.post(new Runnable() {
            @Override
            public void run() {
                vListOfMessages.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
        messageViewModel.getDatatFromDB(listOfMessages, messageAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.massage_main, menu);
        messageViewModel.getNotificationsStatus(menu.findItem(R.id.muteNotifications));
        notificationsStatus = menu.findItem(R.id.muteNotifications).getTitle().toString();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int id = item.getItemId();
        switch (id){
            case R.id.muteNotifications:
                if(item.getTitle().equals("Mute")) {
                    messageViewModel.muteFriend("Unmute");
                    item.setTitle(R.string.unmute_friend);
                }
                else{
                    messageViewModel.muteFriend("Mute");
                    item.setTitle(R.string.mute_friend);

                }
                notificationsStatus = item.getTitle().toString();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}