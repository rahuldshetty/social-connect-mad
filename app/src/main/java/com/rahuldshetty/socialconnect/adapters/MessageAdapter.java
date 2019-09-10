package com.rahuldshetty.socialconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.modals.Message;
import com.rahuldshetty.socialconnect.modals.User;
import com.rahuldshetty.socialconnect.utils.TimePretty;

import java.util.ArrayList;


public class MessageAdapter  extends RecyclerView.Adapter{

    ArrayList<Message> messages;
    private Context context;
    String self,other;

    public MessageAdapter(Context context,ArrayList<Message> messages,String self,String other){
        this.context = context;
        this.messages = messages;
        this.self = self;
        this.other = other;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 1:// sent
                view = LayoutInflater.from(context).inflate(R.layout.single_message_sent,parent,false);
                return new RecvViewHolder(view);
            case 2: //recv
                view = LayoutInflater.from(context).inflate(R.layout.single_message_recv,parent,false);
                return new SenderViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (messages.get(position).getStatus()){
            case "sent":
                return 1;

            case "recv":
                return  2;
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);
        if(message!=null) {
            switch (holder.getItemViewType()) {
                case 1:
                    ((RecvViewHolder)holder).msg.setText(message.getMsg());
                    ((RecvViewHolder)holder).name.setText(self);
                    ((RecvViewHolder)holder).timestamp.setText(TimePretty.getTimeAgo(message.getTimestamp()));
                    break;

                case 2:
                    ((SenderViewHolder)holder).msg.setText(message.getMsg());
                    ((SenderViewHolder)holder).name.setText(other);
                    ((SenderViewHolder)holder).timestamp.setText(TimePretty.getTimeAgo(message.getTimestamp()));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class RecvViewHolder extends RecyclerView.ViewHolder{

        public TextView name,timestamp,msg;

        public RecvViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.single_msg_name_recv);
            msg = itemView.findViewById(R.id.single_msg_recv_text);
            timestamp = itemView.findViewById(R.id.single_message_time_recv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        public TextView name,timestamp,msg;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.single_msg_name_sender);
            msg = itemView.findViewById(R.id.single_msg_sender_text);
            timestamp = itemView.findViewById(R.id.single_message_time_sender);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

}
