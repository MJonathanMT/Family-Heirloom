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

public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ViewHolder> {

    private OnNoteListener mOnNoteListener;

    private List<Item> itemList;
    private ImageView urlView;

    /**
     * It takes an item layout and pushing it into the recycle view to be updated
     * @param itemList list of items
     * @param mOnNoteListener interface where activity with recycler view are updated
     */
    public ItemsListAdapter(List<Item> itemList, OnNoteListener mOnNoteListener) {
        this.mOnNoteListener = mOnNoteListener;
        this.itemList = itemList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view, mOnNoteListener);
    }

    /**
     * This method internally calls to update the RecyclerView.ViewHolder contents with the item at
     * the given position and also sets up some private fields to be used by RecyclerView.
     * @param holder A ViewHolder that holds a View of the given view type.
     * @param position item at the given position
     */
    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder,final int position){

        holder.nameText.setText(itemList.get(position).getName());
        holder.descriptionText.setText(itemList.get(position).getDescription());

        if (itemList.get(position).getUrl() != ""){
            Picasso.get().load(itemList.get(position).getUrl()).into(urlView);
        } else {
            //todo(naverill) replace with standard failure image
        }
    }

    /**
     * Gets the count of an item list
     * @return the number of items in a list
     */
    @Override
    public int getItemCount () {

        return itemList.size();

    }

    /**
     * Constructor of an item list class adapter.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;
        View wholeView;
        TextView nameText;
        TextView descriptionText;
        TextView urlText;
        OnNoteListener onNoteListener;

        ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            mView = itemView;
            nameText = mView.findViewById(R.id.timelineFamilyName);
            descriptionText = mView.findViewById(R.id.description);
            urlText = mView.findViewById(R.id.url);
            urlView = mView.findViewById(R.id.urlView);
            wholeView = mView.findViewById(R.id.wholeItemView);

            wholeView.setOnClickListener(this);
            this.onNoteListener = onNoteListener;
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }


    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}