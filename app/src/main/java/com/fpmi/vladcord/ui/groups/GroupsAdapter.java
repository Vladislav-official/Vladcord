package com.fpmi.vladcord.ui.groups;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.FirebaseChangeInterface;
import com.fpmi.vladcord.ui.friends_list.RecycleFriendClick;
import com.fpmi.vladcord.ui.messages_list.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter {
    private final LayoutInflater inflater;
    private final List<String> users;
    private final Context context;
    private final GroupsModel groupsModel = new GroupsModel();
    private RecycleFriendClick mClickListener;


    private String lastMessageText = "Default";
    private String lastMessageTimeText;
    private String lastMessageSender;
    private String lastMessageNameSender;
    private boolean lastMessageStatus;

    private String myId;

    public GroupsAdapter(RecycleFriendClick recycleUserClick, Context context, List<String> users) {
        this.users = users;
        mClickListener = recycleUserClick;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            myId = null;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements FirebaseChangeInterface {
        TextView name;
        TextView nameOfLastMessage;
        TextView lastMessage;
        TextView timeOfLastMessage;
        TextView dots;

        ViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.group_name);
            dots = itemView.findViewById(R.id.dots);
            nameOfLastMessage = itemView.findViewById(R.id.name_of_last_message);
            lastMessage = itemView.findViewById(R.id.last_message);
            timeOfLastMessage = itemView.findViewById(R.id.time_of_last_message);
        }

        void bind(String group) {
            this.name.setText(group);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                groupsModel.getLastMessage(group, this);
            }
        }

        @Override
        public void DataChanged() {

        }

        @Override
        public void DataChanged(Message message) {
            if (message != null) {
                if(message.getTextMessage().length()>= 35){
                    dots.setVisibility(View.VISIBLE);
                }
                lastMessageText = message.getTextMessage();
                lastMessageTimeText = DateFormat.format("HH:mm", message.getMessageTime().getTime()).toString();
                lastMessageSender = message.getSender();
                lastMessageStatus = message.isIsseen();
                lastMessageNameSender = message.getUserName();

                if (!lastMessageText.equals("Default")) {
                    if (myId.equals(lastMessageSender)) {
                        if (lastMessageStatus) {
                            lastMessage.setText(lastMessageText);
                            timeOfLastMessage.setText(context.getString(R.string.galochka) + "  "
                                    + lastMessageTimeText);
                            lastMessageText = "Default";
                        } else {
                            lastMessage.setText(lastMessageText);
                            timeOfLastMessage.setText(lastMessageTimeText);
                            lastMessageText = "Default";
                        }
                    } else {
                        lastMessage.setText(lastMessageText);
                        timeOfLastMessage.setText(lastMessageTimeText);
                        nameOfLastMessage.setText(lastMessageNameSender + ":  ");
                        lastMessageText = "Default";
                    }
                } else {
                    lastMessage.setText("");
                    timeOfLastMessage.setText("");
                    lastMessageText = "Default";
                }
            }
        }

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_group, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView name = v.findViewById(R.id.group_name);
                if (mClickListener != null) {
                    mClickListener.onClick("", "", name.getText().toString());
                }
            }
        });
        return new GroupsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String group = users.get(position);
        ((GroupsAdapter.ViewHolder) holder).bind(group);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}
