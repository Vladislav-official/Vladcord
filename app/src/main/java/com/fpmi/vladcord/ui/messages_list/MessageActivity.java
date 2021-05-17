package com.fpmi.vladcord.ui.messages_list;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MessageActivity extends AppCompatActivity implements
        Application.ActivityLifecycleCallbacks {
    private String friendId;

    private String privateMessage;

    private MessageViewModel messageViewModel;
    private ImageView submitButton, emojiButton;
    private EmojIconActions emojIconActions;
    private EmojiconEditText emojiconEditText;
    private List<Message> listOfMessages;
    private MessageAdapter messageAdapter;
    private RecyclerView vListOfMessages;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        String friendId = getIntent().getStringExtra("friendId");
        String friendName = getIntent().getStringExtra("friendName");
        privateMessage = getIntent().getStringExtra("privateMessage");


        this.friendId = friendId;
        if (privateMessage.equals("true")) {
            setTitle(friendName);
        } else {
            setTitle(getIntent().getStringExtra("groupName"));
        }
        setupActionBar();

        init(friendId);
        messageViewModel.setStatusOnline();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojiconEditText.getText() != null) {
                    if (privateMessage.equals("true")) {
                        messageViewModel.addMessage(new Message(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid(), friendId, FirebaseAuth.getInstance().getCurrentUser()
                                .getDisplayName(), emojiconEditText.getText().toString(), false));
                    } else {
                        messageViewModel.addGroupMessage(new Message(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid(), null, FirebaseAuth.getInstance().getCurrentUser()
                                .getDisplayName(), emojiconEditText.getText().toString(), false));
                    }
                    emojiconEditText.setText("");
                }
            }
        });
    }

    private void init(String friendId) {
        messageViewModel = new MessageViewModel();


        submitButton = findViewById(R.id.submit_button);
        emojiconEditText = findViewById(R.id.message_text);
        emojiButton = findViewById(R.id.emoji_button);
        emojIconActions = new EmojIconActions(getApplicationContext(), findViewById(R.id.private_message),
                emojiconEditText, emojiButton);
        emojIconActions.ShowEmojIcon();

        listOfMessages = new ArrayList<>();
        vListOfMessages = findViewById(R.id.message_list);
        messageAdapter = new MessageAdapter(this, listOfMessages);
        vListOfMessages.setAdapter(messageAdapter);
        vListOfMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        vListOfMessages.setLayoutManager(linearLayoutManager);
        messageViewModel.setChat(friendId, messageAdapter, getIntent().getStringExtra("groupName"));
        if (privateMessage.equals("true")) {
            messageViewModel.getDatatFromDB(listOfMessages);
        } else {
            messageViewModel.getGroupChatDataFromDB(listOfMessages);
        }
        sendMessage(friendId);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void sendMessage(String userId) {
        if (privateMessage.equals("true")) {
            messageViewModel.sendMessage(userId, seenListener);
        } else {
            messageViewModel.sendGroupMessage(userId, seenListener);
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("current_user", userid);
        editor.apply();
    }

    private void currentNotificationsStatus(String status) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFSSTATUS", MODE_PRIVATE).edit();
        editor.putString("current_status", status);
        editor.apply();
    }


    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser(friendId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            messageViewModel.setStatusOffline(getString(R.string.last_seen));
        }
        messageViewModel.removeSeenListener();
        currentUser("none");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (privateMessage.equals("true")) {
            getMenuInflater().inflate(R.menu.message_main, menu);
        } else {
            getMenuInflater().inflate(R.menu.message_main_group, menu);
        }
        if (privateMessage.equals("true")) {
            messageViewModel.getNotificationsStatus(this, menu.findItem(R.id.muteNotifications));
        } else {
            messageViewModel.getGroupNotificationsStatus(this, menu.findItem(R.id.muteNotifications));
        }
        currentNotificationsStatus(menu.findItem(R.id.muteNotifications).getTitle().toString());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int id = item.getItemId();
        switch (id) {
            case R.id.muteNotifications:
                if (item.getTitle().equals(getString(R.string.mute_friend))) {
                    if (privateMessage.equals("true")) {
                        messageViewModel.muteFriend(getString(R.string.unmute_friend));
                    } else {
                        messageViewModel.muteGroup(getString(R.string.unmute_friend));
                    }
                    item.setTitle(R.string.unmute_friend);
                    currentNotificationsStatus(getString(R.string.unmute_friend));
                } else {
                    if (privateMessage.equals("true")) {
                        messageViewModel.muteFriend(getString(R.string.mute_friend));
                    } else {
                        messageViewModel.muteGroup(getString(R.string.mute_friend));
                    }
                    currentNotificationsStatus(getString(R.string.mute_friend));
                    item.setTitle(R.string.mute_friend);

                }
                break;
            case R.id.action_leave:
                if (privateMessage.equals("true")) {
                    messageViewModel.deleteChat();
                } else {
                    messageViewModel.leaveGroupChat();
                }
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


}