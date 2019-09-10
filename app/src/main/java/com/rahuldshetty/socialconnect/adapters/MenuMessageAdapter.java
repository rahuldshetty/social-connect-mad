package com.rahuldshetty.socialconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.modals.UserMessage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MenuMessageAdapter extends RecyclerView.Adapter{

    private ArrayList<UserMessage> userMessages;
    private Context context;

    public MenuMessageAdapter(Context context,ArrayList<UserMessage> messages){
        this.context = context;
        userMessages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sinlge_message_menu,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserMessage message = userMessages.get(position);
        ((ViewHolder)holder).name.setText(message.getName());
        ((ViewHolder)holder).timestamp.setText(message.getTimestamp());
        ((ViewHolder)holder).desc.setText(message.getDesc());
        Glide.with(context).load(message.getImage()).placeholder(R.drawable.profile).into( ((ViewHolder)holder).imageView );
    }

    @Override
    public int getItemCount() {
        return userMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,timestamp,desc;
        CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.menu_msg_name);
            timestamp = itemView.findViewById(R.id.menu_msg_time);
            desc = itemView.findViewById(R.id.menu_msg_desc);
            imageView = itemView.findViewById(R.id.menu_msg_image);
        }
    }

}
