package com.fpmi.vladcord.ui.profile;

import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.Toolbar;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.FireChangeInterface;
import com.fpmi.vladcord.ui.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileViewModel implements FireChangeInterface {

    private String id;
    private TextView profileEmail;
    private TextView profileName;
    private TextView profileBio;
    private TextView bioDiscription;
    private CircleImageView user_avatar;
    private androidx.appcompat.widget.Toolbar toolbar;
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

    public void getCurUser(){
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
        if(!user.getBio().equals("")) {
            profileBio.setText(user.getBio());
            bioDiscription.setText("Bio");
        }
        else{
            profileBio.setText("");
            profileBio.setHint("Bio");
            bioDiscription.setText("Add a few words about yourself");
        }
        toolbar.setTitle(user.getName());
    }
    public void setStatusOnline(){
        profielModel.setStatusOnline();
    }
    public void setStatusOffline(String lastSeen){
        profielModel.setStatusOffline(lastSeen);
    }
}
