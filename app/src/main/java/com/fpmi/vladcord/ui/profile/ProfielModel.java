package com.fpmi.vladcord.ui.profile;

import android.text.format.DateFormat;

import androidx.annotation.NonNull;

import com.fpmi.vladcord.R;
import com.fpmi.vladcord.ui.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ProfielModel {
    private final ProfileViewModel profileViewModel;

    public ProfielModel() {
        profileViewModel = null;
    }

    public ProfielModel(ProfileViewModel profileViewModel) {
        this.profileViewModel = profileViewModel;
    }

    public void getCurUser() {
        FirebaseDatabase.getInstance().getReference("Users").child(profileViewModel.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userD = snapshot.getValue(User.class);
                        if (userD != null) {
                            profileViewModel.DataChanged(userD);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void setStatusOnline() {
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status").setValue("Online");
    }

    public void setStatusOffline(String lastSeen) {
        FirebaseDatabase.getInstance().getReference("Users/".concat
                (FirebaseAuth.getInstance().getCurrentUser().getUid())).child("status")
                .setValue(lastSeen + " " + (DateFormat.format("HH:mm", (new Date().getTime())))
                        + " " + DateFormat.format("dd:MM", (new Date().getTime())));
    }

    public void changeName(String name) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("name").setValue(name);
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(name);
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(builder.build());

    }

    public void changeBio(String name) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("bio").setValue(name);
    }
}
