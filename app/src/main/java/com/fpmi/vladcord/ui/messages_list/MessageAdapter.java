package com.fpmi.vladcord.ui.messages_list;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.bl.BubbleLayout;
import com.fpmi.vladcord.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final LayoutInflater inflater;
    private final List<Message> messages;
    private final Context context;
    private MessageViewModel messageViewModel;
    int friendColor;
    int mineColor;

    MessageAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        messageViewModel = new MessageViewModel();
        friendColor = context.getResources().getColor(R.color.friend_message);
        mineColor = context.getResources().getColor(R.color.mine_message);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        BubbleLayout bubbleLayout;
        TextView messageDate;
        TextView txtSeen;
        CircleImageView avatar;
        TextView messageSender;


        ViewHolder(final View itemView) {
            super(itemView);
            messageSender = itemView.findViewById(R.id.message_sender);
            text = itemView.findViewById(R.id.message_text_friend);
            bubbleLayout = itemView.findViewById(R.id.bubble_layout);
            messageDate = itemView.findViewById(R.id.message_date);
            txtSeen = itemView.findViewById(R.id.txt_seen);
            avatar = itemView.findViewById(R.id.friend_avatar);
        }

        void bind(Message message) {
            this.text.setText(message.getTextMessage());
            this.messageDate.setText(DateFormat.format("HH:mm", message.getMessageTime()));
            messageViewModel.getSender(messageSender, message.getSender());
            messageViewModel.getSenderAvatar(avatar, message.getSender());
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.list_message_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_message_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        ((ViewHolder) holder).bind(message);
        if (position == messages.size() - 1) {
            if (message.isIsseen()) {
                ((ViewHolder) holder).txtSeen.setText(R.string.seen_message);
            } else {
                ((ViewHolder) holder).txtSeen.setText("No connection\nDelivering...");
                FirebaseDatabase.getInstance().getReference(".info/connected")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean connected = snapshot.getValue(Boolean.class);
                                if (connected) {
                                    ((ViewHolder) holder).txtSeen.setText(R.string.delivered);
                                } else {
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        } else {
            ((ViewHolder) holder).txtSeen.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
