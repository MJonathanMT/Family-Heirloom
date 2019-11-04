package com.example.keepsake.utils.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseSnapshot.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    public List<User> userList;
    public ImageView urlView;
    public UserListAdapter(List<User> userList){

        this.userList = userList;

    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items of
     * the given type. You can either create a new View manually or inflate it from an XML layout file.
     *
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be re-used to display different items
     * in the data set, it is a good idea to cache references to sub views of the View to avoid
     * unnecessary findViewById(int) calls.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_layout, parent, false);

        return new ViewHolder(view);

    }

    /**
     * This method internally calls to update the RecyclerView.ViewHolder contents with the item at
     * the given position and also sets up some private fields to be used by RecyclerView.
     * @param holder A ViewHolder that holds a View of the given view type.
     * @param position item at the given position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = userList.get(position).getFirstName() + " " + userList.get(position).getLastName();
        holder.textViewName.setText(name);
        holder.textViewUsername.setText(userList.get(position).getUsername());

        if (userList.get(position).getUrl() != null){
            Picasso.get().load(userList.get(position).getUrl()).into(urlView);
        }

    }

    /**
     * Gets the count of an user list
     * @return the number of users in a list
     */
    @Override
    public int getItemCount() {

        return userList.size();

    }

    /**
     * Constructor of an user list class adapter.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView textViewName;
        public TextView textViewUsername;
        public TextView urlText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            textViewName =  mView.findViewById(R.id.textViewName);
            textViewUsername =  mView.findViewById(R.id.textViewUsername);
            urlText =  mView.findViewById(R.id.url);
            urlView = mView.findViewById(R.id.imageSearchProfile);
        }
    }
}
