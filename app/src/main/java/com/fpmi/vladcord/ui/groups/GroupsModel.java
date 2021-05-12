package com.fpmi.vladcord.ui.groups;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.friends_list.Friend;
import com.fpmi.vladcord.ui.friends_list.FriendsAdapter;
import com.fpmi.vladcord.ui.friends_list.FriendsViewModel;
import com.fpmi.vladcord.ui.messages_list.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GroupsModel {
    private final DatabaseReference friendsRef;
    //Экземпляр ViewModel для оповещения об изменении
    private final GroupsViewModel groupsViewModel;

    public GroupsModel() {
        friendsRef = null;
        groupsViewModel = null;
    }

    public GroupsModel(GroupsViewModel groupsViewModel) {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .child("Groups");
        this.groupsViewModel = groupsViewModel;

    }
    //Создание слушателя, onDataChange будет срабатывать каждый раз, как база будет меняться
    //Тут из базы берутся id пользователй, являющихся друзьями текущему пользователю
    public void getDataFromDB(List<String> list)
    {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(list.size() != 0)list.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Group group = ds.getValue(Group.class);
                    list.add(group.getGroupName());
                }
                groupsViewModel.DataChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendsRef.addValueEventListener(vListener);
    }
    public void getLastMessage(String groupName, GroupsAdapter.ViewHolder viewHolder)
    {
        FirebaseDatabase.getInstance().getReference("Groups").child(groupName).child("Chat")
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Message messageR = new Message();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    messageR = message;
                    }
                System.out.println(messageR.getSender());
                viewHolder.DataChanged(messageR);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
}
