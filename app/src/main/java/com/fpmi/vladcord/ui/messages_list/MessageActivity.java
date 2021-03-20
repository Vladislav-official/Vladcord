package com.fpmi.vladcord.ui.messages_list;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageActivity extends AppCompatActivity {
    private RelativeLayout private_message;
    private FirebaseListAdapter<Message> adapter;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_message);

        private_message = findViewById(R.id.private_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        String friend = intent.getStringExtra("friend");
        DatabaseReference friendsReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).child("Friends").child(friend);
        displayAllMessages();
        ImageView submit_button = findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView text = findViewById(R.id.textField);
                friendsReference.child("Messages/".concat(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                        .setValue(new Message(FirebaseAuth.getInstance().getCurrentUser()
                        .getEmail(), text.getText().toString()));
            }
        });
    }

    private void displayAllMessages() {
        ListView listOfMessages = findViewById(R.id.listOfMessages);
        String friend = intent.getStringExtra("friend");
        DatabaseReference friendsReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid()).child("Friends").child(friend);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.list_item,
                friendsReference) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView mess_user, mess_time;
                BubbleTextView mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);

                mess_user.setText(model.getUserName());
                mess_text.setText(model.getTextMessage());
                mess_time.setText(DateFormat.format("dd-mm-yyyy HH:mm:ss", model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
    }

}
