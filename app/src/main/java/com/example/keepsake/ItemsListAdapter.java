package com.example.keepsake;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ViewHolder> {

    private List<Item> itemList;
    private ImageView urlView;
    private Context context;
    private Boolean onClickBehaviour = false;

    public ItemsListAdapter(List<Item> itemList){

        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    void enableOnClickBehaviour(){
        onClickBehaviour = true;
    }

    void disableOnClickBehaviour(){
        onClickBehaviour = false;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.nameText.setText(itemList.get(position).getName());
        holder.descriptionText.setText(itemList.get(position).getDescription());
        Picasso.get().load(itemList.get(position).getUrl()).into(urlView);


        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openViewItemActivity(itemList.get(position));
            }
        });
    }

    void openViewItemActivity(Item item){
        Intent intent = new Intent(context, ViewItemActivity.class);
        //(this, ViewItemActivity.class);
        intent.putExtra("itemID", item.getUUID());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {

        return itemList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;

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

        void setOnClickListener(View.OnClickListener listener){
            mView.setOnClickListener(listener);
        }

    }
}
