package com.rahuldshetty.socialconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.modals.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter  extends  RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private ArrayList<Comment> comments;
    private Context context;

    public CommentAdapter(Context context,ArrayList<Comment> comments){
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment c = comments.get(position);
        holder.user.setText(c.getUser());
        holder.desc.setText(c.getMsg());

        Date date = new Date(c.getTimestamp());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH);
        sdf.applyPattern("EEE, d MMM yyyy");
        String sMyDate = sdf.format(date);
        holder.time.setText(sMyDate);

        if(c.getImage()!=null)
            Glide.with(context).load(c.getImage()).placeholder(R.drawable.profile).into(holder.userImage);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView user,desc,time;
        public CircleImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.commentName);
            time = itemView.findViewById(R.id.commentTime);
            desc = itemView.findViewById(R.id.commentDesc);
            userImage = itemView.findViewById(R.id.commentImage);
        }
    }

}
