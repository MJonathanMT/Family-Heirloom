package com.example.keepsake.utils.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseSnapshot.Item;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemsListProfileAdapter extends RecyclerView.Adapter<ItemsListProfileAdapter.ViewHolder> {

    public List<Item> itemsList;
    public ImageView urlView;

    public ItemsListProfileAdapter(List<Item> itemsList) {

        this.itemsList = itemsList;
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
    public ItemsListProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This method internally calls to update the RecyclerView.ViewHolder contents with the item at
     * the given position and also sets up some private fields to be used by RecyclerView.
     * @param holder A ViewHolder that holds a View of the given view type.
     * @param position item at the given position
     */
    @Override
    public void onBindViewHolder(@NonNull ItemsListProfileAdapter.ViewHolder holder, int position) {

        // Loads the name from database into the viewer
        holder.nameText.setText(itemsList.get(position).getName());
        // Loads the description from database into the viewer
        holder.descriptionText.setText(itemsList.get(position).getDescription());
        // Loads the image from database into the viewer
        Picasso.get().load(itemsList.get(position).getUrl()).into(urlView);
    }

    /**
     * Gets the count of an item list
     * @return the number of items in a list
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * Constructor of an item list class adapter.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView nameText;
        public TextView descriptionText;
        public TextView urlText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            nameText = (TextView) mView.findViewById(R.id.timelineFamilyName);
            descriptionText = (TextView) mView.findViewById(R.id.description);
            urlText =  mView.findViewById(R.id.url);
            urlView = mView.findViewById(R.id.urlView);
        }
    }
}
