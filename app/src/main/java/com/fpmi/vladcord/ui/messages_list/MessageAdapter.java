package com.fpmi.vladcord.ui.messages_list;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.fpmi.vladcord.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

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


        ViewHolder(final View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message_text_friend);
            bubbleLayout = itemView.findViewById(R.id.bubble_layout);
            messageDate = itemView.findViewById(R.id.message_date);
            txtSeen = itemView.findViewById(R.id.txt_seen);
        }

        void bind(Message message) {
            this.text.setText(message.getTextMessage());
            this.messageDate.setText(DateFormat.format("HH:mm", message.getMessageTime()));
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.list_message_right, parent, false);
        }
        else{
            view = LayoutInflater.from(context).inflate(R.layout.list_message_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        ((ViewHolder)holder).bind(message);
        if(position == messages.size() - 1){
            if(message.isIsseen()){
                ((ViewHolder) holder).txtSeen.setText("Seen");
            }
            else{
                ((ViewHolder) holder).txtSeen.setText("Delivered");
            }
        }else{
            ((ViewHolder) holder).txtSeen.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}
