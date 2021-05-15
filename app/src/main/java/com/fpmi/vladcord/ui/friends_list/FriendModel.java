package com.fpmi.vladcord.ui.friends_list;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.ui.User.User;
import com.fpmi.vladcord.ui.messages_list.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendModel {
    //Путь в базе данных к нужным нам данным
    private final DatabaseReference friendsRef;
    //Экземпляр ViewModel для оповещения об изменении
    private final FriendsViewModel friendsViewModel;

    public FriendModel() {
        friendsRef = null;
        friendsViewModel = null;
    }

    public FriendModel(FriendsViewModel friendsViewModel) {
        this.friendsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("Friends");
        this.friendsViewModel = friendsViewModel;

    }

    //Создание слушателя, onDataChange будет срабатывать каждый раз, как база будет меняться
    //Тут из базы берутся id пользователй, являющихся друзьями текущему пользователю
    public void getDataFromDB(List<User> friendList) {
        List<String> friendsIds = new ArrayList<>();
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (friendsIds.size() != 0) friendsIds.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Friend friend = ds.getValue(Friend.class);
                    friendsIds.add(friend.getFriendId());
                }
                getDataFromDBS2(friendList, friendsIds);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        friendsRef.addValueEventListener(vListener);
    }

    //Тут через полученный выше список id пользователй, происходит считывание их данных
    public void getDataFromDBS2(List<User> friendList, List<String> friendIds) {
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (friendList.size() != 0) friendList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    assert user != null;
                    if (friendIds.contains(user.getuID())) {
                        friendList.add(new User(user));
                    }
                }
                //Сообщаем модели, что список изменился
                friendsViewModel.DataChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Чтение последнего написаннного сообщения из базы
    public void getLastMessage(String friendId, FriendsAdapter.ViewHolder viewHolder) {
        FirebaseDatabase.getInstance().getReference("Chats").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Message messageR = new Message();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    if ((message.getReceiver().equals(friendId) &&
                            message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) ||
                            (message.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    && message.getSender().equals(friendId))) {
                        messageR = message;
                    }
                }
                if (messageR.getSender() != null) {
                    viewHolder.DataChanged(messageR);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
