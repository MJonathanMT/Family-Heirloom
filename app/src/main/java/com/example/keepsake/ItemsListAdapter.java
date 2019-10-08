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

    public List<Items> itemsList;
    public ImageView urlView;
    public ItemsListAdapter(List<Items> itemsList){

        this.itemsList = itemsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.nameText.setText(itemsList.get(position).getName());
        holder.descriptionText.setText(itemsList.get(position).getDescription());

        Picasso.get().load(itemsList.get(position).getUrl()).into(urlView);

    }

    @Override
    public int getItemCount() {

        return itemsList.size();

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
