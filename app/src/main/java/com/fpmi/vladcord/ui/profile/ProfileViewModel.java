package com.fpmi.vladcord.ui.profile;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;

import com.fpmi.vladcord.ui.FirebaseChangeInterface;
import com.fpmi.vladcord.ui.User.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileViewModel extends ViewModel implements FirebaseChangeInterface {

    private final String id;
    private final TextView profileEmail;
    private final TextView profileName;
    private final TextView profileBio;
    private final TextView bioDiscription;
    private final CircleImageView user_avatar;
    private final androidx.appcompat.widget.Toolbar toolbar;
    private final ProfielModel profielModel;

    public String getId() {
        return id;
    }

    public ProfileViewModel(String id, TextView profileEmail, TextView profileName, TextView profileBio,
                            TextView bioDiscription, CircleImageView user_avatar, androidx.appcompat.widget.Toolbar toolbar) {
        this.id = id;
        this.profileEmail = profileEmail;
        this.profileName = profileName;
        this.profileBio = profileBio;
        this.bioDiscription = bioDiscription;
        this.user_avatar = user_avatar;
        this.toolbar = toolbar;
        profielModel = new ProfielModel(this);
    }

    public void getCurUser() {
        profielModel.getCurUser();
    }


    @Override
    public void DataChanged() {

    }

    @Override
    public void DataChanged(User user) {
        profileName.setText(user.getName());
        profileEmail.setText(user.getEmail());
        Picasso.get()
                .load(user.getUrlAva())
                .into(user_avatar);
        if (!user.getBio().equals("")) {
            profileBio.setText(user.getBio());
            bioDiscription.setText("Bio");
        } else {
            profileBio.setText("");
            profileBio.setHint("Bio");
            bioDiscription.setText("Add a few words about yourself");
        }
        toolbar.setTitle(user.getName());
    }

    public void setStatusOnline() {
        profielModel.setStatusOnline();
    }

    public void setStatusOffline(String lastSeen) {
        profielModel.setStatusOffline(lastSeen);
    }
}
