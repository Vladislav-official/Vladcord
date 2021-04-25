package com.fpmi.vladcord.ui.messages_list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.fpmi.vladcord.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MessageActivity extends AppCompatActivity implements
        Application.ActivityLifecycleCallbacks {
private String friendId;
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
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("Status").setValue("Online");
        String friendId = getIntent().getStringExtra("friendId");
        String friendName = getIntent().getStringExtra("friendName");
        this.friendId = friendId;
        setTitle(friendName);
        setupActionBar();

        init(friendId);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emojiconEditText.getText() != null) {
                    messageViewModel.addMessage(new Message(FirebaseAuth.getInstance().getCurrentUser()
                            .getUid(), friendId, FirebaseAuth.getInstance().getCurrentUser()
                            .getDisplayName(), emojiconEditText.getText().toString(), false));
                    emojiconEditText.setText("");
                }
            }
        });
    }

    private void init(String friendId) {
        messageViewModel = new MessageViewModel();
        messageViewModel.setFriendId(friendId, this);

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

        messageViewModel.getDatatFromDB(listOfMessages, messageAdapter);
        sendMessage(friendId);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    private void sendMessage(String userId){
        messageViewModel.sendMessage(userId, seenListener);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("current_user", userid);
        editor.apply();
    }
private void currentNotificationsStatus(String status){
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
        getMenuInflater().inflate(R.menu.massage_main, menu);
        messageViewModel.getNotificationsStatus(menu.findItem(R.id.muteNotifications));
        currentNotificationsStatus(menu.findItem(R.id.muteNotifications).getTitle().toString());
        return true;
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
                    currentNotificationsStatus("Unmute");
                }
                else{
                    messageViewModel.muteFriend("Mute");
                    currentNotificationsStatus("Mute");
                    item.setTitle(R.string.mute_friend);

                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}