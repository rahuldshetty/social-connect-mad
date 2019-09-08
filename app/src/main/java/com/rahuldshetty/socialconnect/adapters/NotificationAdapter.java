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
import com.rahuldshetty.socialconnect.modals.NotificationModal;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NotificationModal> users;
    private ViewHolder viewHolder;

    public NotificationAdapter(Context context,ArrayList<NotificationModal> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            viewHolder = new ViewHolder();

            convertView = (LayoutInflater.from(parent.getContext()).inflate(R.layout.search_single_item,null));
            viewHolder.desc = convertView.findViewById(R.id.search_city);
            viewHolder.name = convertView.findViewById(R.id.search_name);
            viewHolder.imageView = convertView.findViewById(R.id.search_single_profile_img);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final NotificationModal user = users.get(position);
        viewHolder.desc.setText(user.getStatus());
        viewHolder.name.setText(user.getName());
        Glide.with(context).load(user.getImage()).placeholder(R.drawable.profile).into(viewHolder.imageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.otherUserID = user.getUid();
                MainActivity.loadUserFragment();
            }
        });

        return convertView;
    }


    public static class ViewHolder{
        CircleImageView imageView;
        TextView name,desc;
    }

}
