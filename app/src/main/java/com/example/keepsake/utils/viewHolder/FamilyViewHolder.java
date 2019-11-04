package com.example.keepsake.utils.viewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseSnapshot.Family;

public class FamilyViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private TextView familyNameText;
    private TextView familyIDText;
    private Button joinButton;

    /**
     * Describes an item view and metadata about family item view within the RecyclerView.
     * @param itemView The view type of this View
     */
    public FamilyViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    /**
     * Bind UI components in layouts to data sources using a declarative format.
     * @param family information on a family group
     */
    public void bind(Family family){
        familyNameText = mView.findViewById(R.id.textViewFamilyName);
        familyIDText = mView.findViewById(R.id.textViewFamilyID);
        joinButton = mView.findViewById(R.id.buttonJoin);

        familyNameText.setText(family.getFamilyName());
        familyIDText.setText(family.getFamilyID());
    }

    public void setOnClickListener(View.OnClickListener listener){
        //mView.setOnClickListener(listener);
    }

    public void setButtonOnClickListener(View.OnClickListener listener){
        joinButton.setOnClickListener(listener);
    }

    public View getView(){
        return mView;
    }

    public void setButtonVisibility(int v){
        joinButton.setVisibility(v);
    }

}