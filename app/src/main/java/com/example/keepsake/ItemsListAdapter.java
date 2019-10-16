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

    public List<Item> itemList;
    public ImageView urlView;
    public ItemsListAdapter(List<Item> itemList){

        this.itemList = itemList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.nameText.setText(itemList.get(position).getName());
        holder.descriptionText.setText(itemList.get(position).getDescription());

        Picasso.get().load(itemList.get(position).getUrl()).into(urlView);

    }

    @Override
    public int getItemCount() {

        return itemList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView nameText;
        public TextView descriptionText;
        public TextView urlText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            nameText = (TextView) mView.findViewById(R.id.name);
            descriptionText = (TextView) mView.findViewById(R.id.description);
            urlText =  mView.findViewById(R.id.url);
            urlView = mView.findViewById(R.id.urlView);
        }
    }





}
