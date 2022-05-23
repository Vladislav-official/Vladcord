package com.fpmi.vladcord.ui.messages_list;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.health.PackageHealthStats;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MessageActivity extends AppCompatActivity implements
        Application.ActivityLifecycleCallbacks, MessageAdapter.OnMyMessageClickListener {
    private String friendId;
    public static final int PICK_IMAGE = 1;
    private boolean check = false;
    private String editing = null;
    private boolean editingGroup = false;
    private String privateMessage;
    private final AppVoiceRecorder appVoiceRecorder = new AppVoiceRecorder();
    private MessageViewModel messageViewModel;
    private ImageView submitButton, emojiButton;
    private ImageView micButton;
    private ImageView selectedPhoto;
    private EmojIconActions emojIconActions;
    private EmojiconEditText emojiconEditText;
    private ImageView galleryChooser;
    private List<Message> listOfMessages;
    private MessageAdapter messageAdapter;
    private RecyclerView vListOfMessages;
    private FirebaseDatabase firebaseDatabase;
    private String key;
    private StorageReference ref;
    private Uri selectedPhotoUri;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Toolbar mActionBarToolbar;
    private String friendName;

    private final MessageActivity messageActivity = this;
    ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        String friendId = getIntent().getStringExtra("friendId");
        this.friendName = getIntent().getStringExtra("friendName");
        privateMessage = getIntent().getStringExtra("privateMessage");
        setupActionBar();

        this.friendId = friendId;
        if (privateMessage.equals("true")) {
            setTitle(friendName);
        } else {
            setTitle(getIntent().getStringExtra("groupName"));
        }

        init(friendId);
        messageViewModel.setStatusOnline();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editing != null) {
                    if (editingGroup) {
                        messageViewModel.editGroupMessage(emojiconEditText.getText().toString(), editing);
                    }
                    else {
                        messageViewModel.editMessage(emojiconEditText.getText().toString(), editing);
                    }
                    editing = null;
                    editingGroup = false;
                    submitButton.setImageDrawable(getDrawable(R.drawable.ic_menu_send));
                    emojiconEditText.setText("");
                    return;
                }
                if (emojiconEditText.getText() != null) {
                    if (privateMessage.equals("true")) {
                        messageViewModel.addMessage(privateMessage, new Message(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid(), friendId, FirebaseAuth.getInstance().getCurrentUser()
                                .getDisplayName(), "textMessage",
                                emojiconEditText.getText().toString(), false, null, null), selectedPhotoUri);
                    } else {
                        messageViewModel.addGroupMessage(privateMessage, new Message(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid(), null, FirebaseAuth.getInstance().getCurrentUser()
                                .getDisplayName(), "textMessage",
                                emojiconEditText.getText().toString(), false, null, null), selectedPhotoUri);
                    }
                    emojiconEditText.setText("");
                    selectedPhoto.setImageResource(0);
                    selectedPhotoUri = null;
                }

            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        key = firebaseDatabase.getReference().push().getKey();
        ref = storageReference.child("Voices").child("UsersVoices").child(key);
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check) {
                    if (requestStoragePermission()) {
                        check = true;

                        try {
                            appVoiceRecorder.startRecord(openFileOutput("voices." + key, MODE_PRIVATE));
                            micButton.setBackground(getDrawable(R.drawable.circle_shape));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    appVoiceRecorder.stopRecord();
                    micButton.setBackgroundColor(getColor(R.color.mic_back));
                    ref.putFile(Uri.fromFile(getFileStreamPath("voices." + key)));
                    if (privateMessage.equals("true")) {
                        messageViewModel.addMessage(privateMessage, new Message(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid(), friendId, FirebaseAuth.getInstance().getCurrentUser()
                                .getDisplayName(), "voiceMessage", key,
                                false, null, null), selectedPhotoUri);
                    } else {
                        messageViewModel.addGroupMessage(privateMessage, new Message(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid(), null, FirebaseAuth.getInstance().getCurrentUser()
                                .getDisplayName(), "voiceMessage", key,
                                false, null, null), selectedPhotoUri);
                    }
                    check = false;
                    key = firebaseDatabase.getReference().push().getKey();
                    ref = storageReference.child("Voices").child("UsersVoices").child(key);
                }
            }
        });
        galleryChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            Picasso.get()
                    .load(data.getData().toString())
                    .resize(400, 400)
                    .centerCrop()
                    .into(selectedPhoto);
            selectedPhotoUri = data.getData();
        }
    }

    private void init(String friendId) {
        messageViewModel = new MessageViewModel();

        getDir("voices", MODE_PRIVATE);
        submitButton = findViewById(R.id.submit_button);
        emojiconEditText = findViewById(R.id.message_text);
        galleryChooser = findViewById(R.id.gallery_chooser);
        emojiButton = findViewById(R.id.emoji_button);
        micButton = findViewById(R.id.mic_button);
        selectedPhoto = findViewById(R.id.selected_photos);

        emojIconActions = new EmojIconActions(getApplicationContext(), findViewById(R.id.private_message),
                emojiconEditText, emojiButton);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setIconsIds(R.drawable.ic_baseline_emoji_emotions_24, R.drawable.ic_baseline_emoji_emotions_24);

        listOfMessages = new ArrayList<>();
        vListOfMessages = findViewById(R.id.message_list);
        messageAdapter = new MessageAdapter(this, listOfMessages, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        vListOfMessages.setLayoutManager(linearLayoutManager);
        vListOfMessages.setAdapter(messageAdapter);
        vListOfMessages.setHasFixedSize(true);

        messageViewModel.setChat(friendId, messageAdapter, getIntent().getStringExtra("groupName"), vListOfMessages);
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

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 200);
            return true;
        } else {
            return false;
        }
    }

    private boolean requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, getString(R.string.hasnt_rights_for_reading), Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 911);
        return false;
    }

    @Override
    protected void onDestroy() {
        appVoiceRecorder.releaseRecorder();
        super.onDestroy();
    }

    @Override
    public void onMyMessageClick(int position, View v) {
        Message message = listOfMessages.get(position);
        if (!message.getUserName().equals(friendName)) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            this.getMenuInflater().inflate(R.menu.menu_message_actions, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.action_edit_message:
                            emojiconEditText.setText(message.getTextMessage());
                            editing = message.getChatId();
                            submitButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_edit_24));
                            if (message.getReceiver() == null) {
                                editingGroup = true;
                            }
                            break;
                        case R.id.action_delete_message:
                            if (message.getReceiver() == null) {
                                messageViewModel.deleteGroupMessage(message);
                            }
                            else {
                                messageViewModel.deleteMessage(message);
                            }
                            break;
                    }
                    return true;
                }
            });
        }
    }
}