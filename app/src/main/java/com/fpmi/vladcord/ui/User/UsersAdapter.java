package com.fpmi.vladcord.ui.User;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fpmi.vladcord.R;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {

private final Context context;
private final List<User> userValues;

public UsersAdapter(Context context, List<User> userValues) {
        super(context, R.layout.list_user, userValues);
        this.context = context;
        this.userValues = userValues;
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.list_user, parent, false);
        TextView name = (TextView) root.findViewById(R.id.name_user);
        TextView email = (TextView) root.findViewById(R.id.mail_user);
        TextView id = (TextView) root.findViewById(R.id.id_user);
        ImageView imageView = (ImageView) root.findViewById(R.id.user_avatar);
        imageView.setImageURI(Uri.parse(userValues.get(position).getUrlAva()));
        name.setText(userValues.get(position).getName());
        email.setText(userValues.get(position).getEmail());
        id.setText(userValues.get(position).getuID());

        return root;
        }
}
