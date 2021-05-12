package com.fpmi.vladcord.ui.friends_list;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.FireChangeInterface;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.messages_list.Message;
import com.fpmi.vladcord.ui.messages_list.MessageModel;
import com.fpmi.vladcord.ui.messages_list.MessageViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.schwaab.avvylib.AvatarView;

public class FriendsAdapter extends RecyclerView.Adapter {

    //Текущий список друзей
    private final List<User> friends;
    //Интерфейс, позволяющий обрабытывать клик непосредственно во фрагменте(нужно для открытия экрана сообщений)
    private RecycleFriendClick mClickListener;
    private final Context context;
    private final FriendModel friendModel = new FriendModel();
    //Данный блок нужен для отображения во фрагменте друзей, последнего написанного сообщения
    private String lastMessageText = "Default";
    private String lastMessageTimeText;
    private String lastMessageSender;
    private boolean lastMessageStatus;
    //
    private String myId;

    public FriendsAdapter(RecycleFriendClick recycleFriendClick, Context context, List<User> friends) {
        this.friends = friends;
        this.mClickListener = recycleFriendClick;
        this.context = context;
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        else{
            myId = null;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements FireChangeInterface {
        CircleImageView ava;
        CircleImageView status;
        TextView name;
        TextView email;
        TextView id;
        TextView lastMessage;
        TextView timeLastMessage;
        //Инициализация View, которые содержит элемент списка
        ViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friend_name);
            email = itemView.findViewById(R.id.friend_email);
            id = itemView.findViewById(R.id.friend_id);
            ava = itemView.findViewById(R.id.friend_avatar);
            status = itemView.findViewById(R.id.status_circle);
            lastMessage = itemView.findViewById(R.id.last_message);
            timeLastMessage = itemView.findViewById(R.id.time_of_last_message);
        }
        //Изменение элементов списка, согласно базе
        void bind(User friend) {
            this.name.setText(friend.getName());
            this.email.setText(friend.getEmail());
            this.id.setText(friend.getuID());
            Picasso.get().load(friend.getUrlAva()).into(this.ava);
            if(friend.getStatus().equals("Online")){
                status.setVisibility(View.VISIBLE);
            }
            else{
                status.setVisibility(View.INVISIBLE);
            }
            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                friendModel.getLastMessage(friend.getuID(), this);
            }
        }

        @Override
        public void DataChanged() {

        }

        @Override
        public void DataChanged(Message message) {
            if (message != null) {
                lastMessageText = message.getTextMessage();
                lastMessageTimeText = DateFormat.format("HH:mm", message.getMessageTime().getTime()).toString();
                lastMessageSender = message.getSender();
                lastMessageStatus = message.isIsseen();
                if (!lastMessageText.equals("Default")) {
                    if (myId.equals(lastMessageSender)) {
                        if (lastMessageStatus) {
                            lastMessage.setText(lastMessageText);
                            timeLastMessage.setText("✔" + "   "
                                    + lastMessageTimeText);
                            lastMessageText = "Default";
                        } else {
                            lastMessage.setText(lastMessageText);
                            timeLastMessage.setText(lastMessageTimeText);
                            lastMessageText = "Default";
                        }
                    } else {
                        lastMessage.setText(lastMessageText);
                        timeLastMessage.setText(lastMessageTimeText);
                        lastMessageText = "Default";
                    }
                } else {
                    lastMessage.setText("");
                    timeLastMessage.setText("");
                    lastMessageText = "Default";
                }
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_friend, parent, false);
        //Реализация обработки клика непосредственно во фрагмента
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView uID = (TextView) v.findViewById(R.id.friend_id);
                TextView name = v.findViewById(R.id.friend_name);
                if (mClickListener != null) {
                    mClickListener.onClick(uID.getText().toString(), name.getText().toString(), "");
                }
            }
        });
        return new ViewHolder(view);
    }
//Берется позиция внутри списка и инициализируется согласно списку из базы
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User friend = friends.get(position);
        ((ViewHolder)holder).bind(friend);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

}


