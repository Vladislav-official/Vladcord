package com.fpmi.vladcord.ui.messages_list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.Layout;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.User.UsersViewModel;
import com.github.library.bubbleview.BubbleTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {


    private final LayoutInflater inflater;
    private final List<Message> messages;
    private final Context context;
    private MessageViewModel messageViewModel;

    MessageAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        messageViewModel = new MessageViewModel();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        BubbleLayout bubbleLayout;
        TextView messageDate;

        ViewHolder(final View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message_text_friend);
            bubbleLayout = itemView.findViewById(R.id.bubble_layout);
            messageDate = itemView.findViewById(R.id.message_date);
        }

        void bind(Message message) {
            RelativeLayout.LayoutParams bubbleLayoutParams =
                    (RelativeLayout.LayoutParams)bubbleLayout.getLayoutParams();
            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(message.userName)) {
                bubbleLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                bubbleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                text.setBackgroundColor(0xB0C4DE);
                messageDate.setBackgroundColor(0xB0C4DE);
                bubbleLayout.setArrowDirection(ArrowDirection.RIGHT);

            } else {
                bubbleLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                bubbleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                text.setBackgroundColor(0x778899);
                messageDate.setBackgroundColor(0x778899);
                bubbleLayout.setArrowDirection(ArrowDirection.LEFT);
            }
            bubbleLayout.setLayoutParams(bubbleLayoutParams);
            this.text.setText(message.textMessage);
            this.messageDate.setText(DateFormat.format("HH:mm", message.getMessageTime()));
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        ((ViewHolder)holder).bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


}
