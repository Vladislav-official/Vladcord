package com.fpmi.vladcord.ui.friends_request_list;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.fahmisdk6.avatarview.AvatarView;

public class FriendsReqAdapter extends RecyclerView.Adapter {


    private final List<User> friends;
    private final Context context;
    private final FriendReqModel friendReqModel;

    public FriendsReqAdapter(Context context, List<User> friends, FriendReqModel friendReqModel) {
        this.friends = friends;
        this.context = context;
        this.friendReqModel = friendReqModel;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ava;
        TextView name;
        TextView email;
        TextView id;
        Button addFriend;
        Button notAddFriend;

        ViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friend_name);
            email = itemView.findViewById(R.id.friend_email);
            id = itemView.findViewById(R.id.friend_id);
            ava = itemView.findViewById(R.id.friend_avatar);
            addFriend = itemView.findViewById(R.id.add_friend);
            notAddFriend = itemView.findViewById(R.id.not_add_friend);
        }

        void bind(User friend) {

            this.name.setText(friend.getName());
            this.email.setText(friend.getEmail());
            this.id.setText(friend.getuID());
            Picasso.get().load(friend.getUrlAva()).into(this.ava);
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendReqModel.addFriend(friend.getuID());
                }
            });
            notAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendReqModel.deleteFriend(friend.getuID());
                }
            });
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_request_friend, parent, false);

        return new ViewHolder(view);
    }

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
