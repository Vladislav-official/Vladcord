package com.fpmi.vladcord.ui.friends_list;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.User.UsersViewModel;
import com.fpmi.vladcord.ui.messages_list.MessageFragment;
import com.google.android.gms.dynamic.IFragmentWrapper;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter {


    private final List<Friend> friends;
    private RecycleItemClick mClickListener;
    private final Context context;

    FriendsAdapter(RecycleItemClick recycleItemClick, Context context, List<Friend> friends) {
        this.friends = friends;
        this.mClickListener = recycleItemClick;
        this.context = context;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ava;
        TextView name;
        TextView email;
        TextView id;

        ViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friend_name);
            email = itemView.findViewById(R.id.friend_email);
            id = itemView.findViewById(R.id.friend_id);
            ava = itemView.findViewById(R.id.friend_avatar);
        }

        void bind(Friend friend) {
            this.name.setText(friend.getName());
            this.email.setText(friend.getEmail());
            this.id.setText(friend.getuID());
            this.ava.setImageURI(Uri.parse(friend.getUrlAva()));
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_friend, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView uID = (TextView) v.findViewById(R.id.friend_id);
                TextView name = v.findViewById(R.id.friend_name);
                TextView email = v.findViewById(R.id.friend_email);
                if (mClickListener != null) {
                    mClickListener.onClick(uID.getText().toString(), name.getText().toString());
                }
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Friend friend = friends.get(position);
        ((ViewHolder)holder).bind(friend);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


}
