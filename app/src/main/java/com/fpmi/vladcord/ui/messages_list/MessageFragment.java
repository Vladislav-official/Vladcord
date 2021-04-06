package com.fpmi.vladcord.ui.messages_list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.UsersAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MessageFragment extends Fragment {

    private MessageViewModel messageViewModel;
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
                if(!emojiconEditText.getText().equals(null)) {
                    messageViewModel.addMessage(new Message(FirebaseAuth.getInstance().getCurrentUser()
                            .getEmail(), emojiconEditText.getText().toString()));
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
        messageViewModel.setFriendId(friendId);

        submitButton = root.findViewById(R.id.submit_button);
        emojiconEditText = root.findViewById(R.id.message_text);
        emojiButton = root.findViewById(R.id.emoji_button);

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

}