package com.example.keepsake.memberList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    public List<User> userList;
    public ImageView urlView;
    public UserListAdapter(List<User> userList){

        this.userList = userList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.firstNameText.setText(userList.get(position).getFirstName());
        holder.lastNameText.setText(userList.get(position).getLastName());

        Picasso.get().load(userList.get(position).getUrl()).into(urlView);

    }

    @Override
    public int getItemCount() {

        return userList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView firstNameText;
        public TextView lastNameText;
        public TextView urlText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            firstNameText = (TextView) mView.findViewById(R.id.firstName);
            lastNameText = (TextView) mView.findViewById(R.id.lastName);
            urlText =  mView.findViewById(R.id.url);
            urlView = mView.findViewById(R.id.urlView);
        }
    }





}
