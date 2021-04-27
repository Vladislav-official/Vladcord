package com.fpmi.vladcord.ui.User;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.fpmi.vladcord.ui.friends_list.RecycleFriendClick;

import java.io.IOException;
import java.util.List;

import me.fahmisdk6.avatarview.AvatarView;

public class UsersAdapter extends RecyclerView.Adapter {


    private final LayoutInflater inflater;
    private final List<User> users;
    private final Context context;
    private RecycleUserClick mClickListener;
    private UsersViewModel usersViewModel;

    UsersAdapter(RecycleUserClick recycleUserClick,Context context, List<User> users) {
        this.users = users;
        mClickListener = recycleUserClick;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        usersViewModel = new UsersViewModel();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        AvatarView ava;
        TextView name;
        TextView email;
        TextView id;

        ViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            email = itemView.findViewById(R.id.user_email);
            id = itemView.findViewById(R.id.user_id);
            ava = itemView.findViewById(R.id.user_avatar);
        }

        void bind(User user) {
            this.ava.bind("user_ava",user.getUrlAva());
            this.name.setText(user.getName());
            this.email.setText(user.getEmail());
            this.id.setText(user.getuID());
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_user, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView name = (TextView) v.findViewById(R.id.user_name);
                TextView email = (TextView) v.findViewById(R.id.user_email);
                TextView uID = (TextView) v.findViewById(R.id.user_id);
                AvatarView avatarView = v.findViewById(R.id.user_avatar);
                if (mClickListener != null) {
                    mClickListener.onClick(new Friend(name.getText().toString(),
                            email.getText().toString(), uID.getText().toString(),
                            avatarView.toString()), v);
                }
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);
        ((ViewHolder)holder).bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}
