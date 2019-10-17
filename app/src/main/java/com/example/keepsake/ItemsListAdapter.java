package com.example.keepsake;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ViewHolder> {

    private OnNoteListener mOnNoteListener;

    private List<Item> itemList;
    private ImageView urlView;

    public ItemsListAdapter(List<Item> itemList, OnNoteListener mOnNoteListener) {
        this.mOnNoteListener = mOnNoteListener;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder,final int position){

        holder.nameText.setText(itemList.get(position).getName());
        holder.descriptionText.setText(itemList.get(position).getDescription());

        Picasso.get().load(itemList.get(position).getUrl()).into(urlView);
    }


    @Override
    public int getItemCount () {

        return itemList.size();

    }

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
            nameText = mView.findViewById(R.id.name);
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
