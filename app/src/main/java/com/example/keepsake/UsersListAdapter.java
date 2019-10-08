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

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    public List<Items> itemsList;
    public ImageView urlView;

    public UsersListAdapter(List<Items> itemsList){

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
//        holder.urlText.setText(itemsList.get(position).getUrl());
        Picasso.get().load(itemsList.get(position).getUrl()).into(urlView);

//        Picasso.get().load(itemsList.get(position).getUrl()).into(urlView)
//        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);

//        holder.urlView.setImageDrawable(itemsList.get(position).getUrl());

//        holder.urlView.setImageResource(itemsList.get(position).getUrl());

        System.out.println(itemsList.get(position).getUrl());

//        holder.urlView(Picasso.get().load(itemsList.get(position).getUrl()).into(urlView));


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
            urlText = (TextView) mView.findViewById(R.id.url);
            urlView = (ImageView) mView.findViewById(R.id.urlView);


        }
    }
}
