package com.rahuldshetty.socialconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.modals.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendGridAdapter extends BaseAdapter {

    Context context;
    List<User> usersList;

    public FriendGridAdapter(Context context,ArrayList<User> usersList)
    {
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public User getItem(int position) {
        return usersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.friend_grid_single,null);

        CircleImageView circleImageView = convertView.findViewById(R.id.friend_grid_image);
        TextView name = convertView.findViewById(R.id.friend_grid_name);

        User user = usersList.get(position);

        Glide.with(context).load(user.getImage()).placeholder(R.drawable.profile).into(circleImageView);
        name.setText(user.getName());

        return convertView;
    }
}
