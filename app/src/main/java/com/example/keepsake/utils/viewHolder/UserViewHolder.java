package com.example.keepsake.utils.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseSnapshot.User;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private View mView;

    /**
     * Describes an item view and metadata about user item view within the RecyclerView.
     * @param itemView
     */
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    /**
     * Bind UI components in layouts to data sources using a declarative format.
     * @param ctx a handler in the system, it provides access to resources, databases, preferences, etc.
     * @param user details of an user
     */
    public void bind(Context ctx, User user){
        String name = user.getFirstName() + " " + user.getLastName();
        String username = user.getUsername();
        TextView nametext = mView.findViewById(R.id.textViewName);
        TextView usernameText = mView.findViewById(R.id.textViewUsername);
        //ImageView userImage = mView.findViewById(R.id.imageViewProfile);

        nametext.setText(name);
        usernameText.setText(username);
        //userImage
    }

    public void setOnClickListener(View.OnClickListener listener){
        mView.setOnClickListener(listener);
    }

    public View getView(){
        return mView;
    }
}