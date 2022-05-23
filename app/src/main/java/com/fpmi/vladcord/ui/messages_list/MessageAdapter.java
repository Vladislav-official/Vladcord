package com.fpmi.vladcord.ui.messages_list;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.daasuu.bl.BubbleLayout;
import com.fpmi.vladcord.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
public class MessageAdapter extends RecyclerView.Adapter {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_RIGHT_VOICE = 2;
    public static final int MSG_TYPE_LEFT_VOICE = 3;


    private final LayoutInflater inflater;
    private final AppVoicePlayer appVoicePlayer = new AppVoicePlayer();
    final List<Message> messages;
    private final Context context;
    private MessageViewModel messageViewModel;
    int friendColor;
    boolean checkOfVoice = false;
    int mineColor;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private OnMyMessageClickListener onMyMessageClickListener;


    MessageAdapter(Context context, List<Message> messages, OnMyMessageClickListener onMyMessageClickListener) {
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        messageViewModel = new MessageViewModel();
        friendColor = context.getResources().getColor(R.color.friend_message);
        mineColor = context.getResources().getColor(R.color.mine_message);
        this.onMyMessageClickListener = onMyMessageClickListener;
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text;
        BubbleLayout bubbleLayout;
        TextView messageDate;
        TextView txtSeen;
        ImageView attachedPic;
        ImageView avatar;
        TextView messageSender;
        OnMyMessageClickListener onMyMessageClickListener;


        ViewHolder(final View itemView, OnMyMessageClickListener onMyMessageClickListener) {
            super(itemView);
            messageSender = itemView.findViewById(R.id.message_sender);
            text = itemView.findViewById(R.id.message_text_friend);
            bubbleLayout = itemView.findViewById(R.id.bubble_layout);
            messageDate = itemView.findViewById(R.id.message_date);
            txtSeen = itemView.findViewById(R.id.txt_seen);
            avatar = itemView.findViewById(R.id.friend_avatar);
            attachedPic = itemView.findViewById(R.id.attached_photo);

            this.onMyMessageClickListener = onMyMessageClickListener;
            itemView.setOnClickListener(this);
        }

        void bind(Message message) {
            this.text.setText(message.getTextMessage());
            this.messageDate.setText(DateFormat.format("HH:mm", message.getMessageTime()));
            messageViewModel.getSender(messageSender, message.getSender());
            messageViewModel.getSenderAvatar(avatar, message.getSender());
            if (message.getAttachedPic() != null) {
                Picasso.get()
                        .load(message.getAttachedPic())
                        .resize(400, 400)
                        .centerCrop()
                        .into(attachedPic);

            }
            else {
                Picasso.get().cancelRequest(attachedPic);
            }
        }

        @Override
        public void onClick(View v) {
            onMyMessageClickListener.onMyMessageClick(getAdapterPosition(), v);
        }
    }

    private class ViewHolderVoice extends RecyclerView.ViewHolder {
        TextView text;
        ImageView voiceOff;
        ImageView voiceOn;
        TextView messageDate;
        TextView txtSeen;
        ImageView avatar;
        TextView messageSender;
        ImageView attachedPic;


        ViewHolderVoice(final View itemView) {
            super(itemView);

            messageSender = itemView.findViewById(R.id.message_sender);
            voiceOff = itemView.findViewById(R.id.audio_stop);
            voiceOn = itemView.findViewById(R.id.audio_play);
            messageDate = itemView.findViewById(R.id.message_date);
            txtSeen = itemView.findViewById(R.id.txt_seen);
            text = itemView.findViewById(R.id.message_text_friend);
            avatar = itemView.findViewById(R.id.friend_avatar);
            attachedPic = itemView.findViewById(R.id.attached_photo);
        }

        void bind(Message message) {
            this.text.setText(message.getTextMessage());
            this.messageDate.setText(DateFormat.format("HH:mm", message.getMessageTime()));
            messageViewModel.getSender(messageSender, message.getSender());
            messageViewModel.getSenderAvatar(avatar, message.getSender());
            if (message.getAttachedPic() != null) {
                Picasso.get()
                        .load(message.getAttachedPic())
                        .resize(400, 400)
                        .centerCrop()
                        .into(attachedPic);
            }
            else {
                Picasso.get().cancelRequest(attachedPic);
            }
            voiceOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appVoicePlayer.stop();
                    voiceOn.setVisibility(View.GONE);
                    voiceOff.setVisibility(View.VISIBLE);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    String key = database.getReference().push().getKey();
                    File file = context.getFileStreamPath("voices." + message.getTextMessage());
                    file.delete();
                    if (!file.exists()){
                        StorageReference ref = storageReference.child("Voices").child("UsersVoices").child(message.getTextMessage());
                        ref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                appVoicePlayer.play(file, voiceOn, voiceOff);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                    else{
                        appVoicePlayer.play(file, voiceOn, voiceOff);
                    }

                }
            });


        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;
        switch(viewType){
            case MSG_TYPE_RIGHT:
                view = LayoutInflater.from(context).inflate(R.layout.list_message_right, parent, false);
                return new ViewHolder(view, onMyMessageClickListener);
            case MSG_TYPE_LEFT:
                view = LayoutInflater.from(context).inflate(R.layout.list_message_left, parent, false);
                return new ViewHolder(view, onMyMessageClickListener);
            case MSG_TYPE_RIGHT_VOICE:
                view = LayoutInflater.from(context).inflate(R.layout.list_message_right_voice, parent, false);
                return new ViewHolderVoice(view);
            case MSG_TYPE_LEFT_VOICE:
                view = LayoutInflater.from(context).inflate(R.layout.list_message_left_voice, parent, false);
                return new ViewHolderVoice(view);
            default:
                return new ViewHolder(null, null);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.getType().equals("textMessage")) {
            ((ViewHolder) holder).bind(message);
            if (position == messages.size() - 1) {
                if (message.isIsseen()) {
                    ((ViewHolder) holder).txtSeen.setText(R.string.seen_message);
                } else {
                    ((ViewHolder) holder).txtSeen.setText(R.string.delivering);
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
        else{
            ((ViewHolderVoice) holder).bind(message);
            if (position == messages.size() - 1) {
                if (message.isIsseen()) {
                    ((ViewHolderVoice) holder).txtSeen.setText(R.string.seen_message);
                } else {
                    ((ViewHolderVoice) holder).txtSeen.setText(R.string.delivering);
                    FirebaseDatabase.getInstance().getReference(".info/connected")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean connected = snapshot.getValue(Boolean.class);
                                    if (connected) {
                                        ((ViewHolderVoice) holder).txtSeen.setText(R.string.delivered);
                                    } else {
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            } else {
                ((ViewHolderVoice) holder).txtSeen.setText("");
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            if (messages.get(position).getType().equals("textMessage")) {
                return MSG_TYPE_RIGHT;
            }
            else{
                return MSG_TYPE_RIGHT_VOICE;
            }
        } else {
            if (messages.get(position).getType().equals("textMessage")) {
                return MSG_TYPE_LEFT;
            }
            else{
                return MSG_TYPE_LEFT_VOICE;
            }
        }
    }

    public interface OnMyMessageClickListener {
        void onMyMessageClick(int position, View v);
    }
}
