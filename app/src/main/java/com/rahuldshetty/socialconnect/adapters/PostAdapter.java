package com.rahuldshetty.socialconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.activities.PostActivity;
import com.rahuldshetty.socialconnect.modals.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> postList;
    private Context context;
    private View view;

    public PostAdapter(Context context, ArrayList<Post> postList)
    {
        this.context = context;
        this.postList = postList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_single,parent,false);
        view = itemView;
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Post post = postList.get(position);
        holder.title.setText(post.getTitle());
        holder.desc.setText(post.getDesc());
        holder.time.setText(post.getTimestamp());
        holder.userName.setText(post.getUserName());
        if(post.getImage()!=null)
        {
            Glide.with(context).load(post.getImage()).placeholder(R.drawable.profile).into(holder.imageView);
        }
        else{
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.imageView.setEnabled(false);
            holder.imageView.getLayoutParams().height = 0 ;
        }
        if(post.getUserImage()!=null)
        {
            Glide.with(context).load(post.getUserImage()).placeholder(R.drawable.profile).into(holder.userImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                Intent act = new Intent(context, PostActivity.class);
                act.putExtra("post",gson.toJson(post));
                context.startActivity(act);


            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title,desc,time,userName;
        public ImageView imageView;
        public CircleImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.postTitle);
            desc = itemView.findViewById(R.id.postDesc);
            imageView = itemView.findViewById(R.id.postImage);
            time = itemView.findViewById(R.id.postTime);
            userName = itemView.findViewById(R.id.postUser);
            userImage = itemView.findViewById(R.id.postProfileImage);

        }
    }

}
