package com.example.keepsake.memberRequest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.User;

import java.util.List;

public class MemberRequestListAdapter extends RecyclerView.Adapter<MemberRequestListAdapter.ViewHolder> {


    public List<User> requestList;
    private OnNoteListener mOnNoteListener;
    public MemberRequestListAdapter(List<User> requestList, OnNoteListener onNoteListener){
        this.requestList = requestList;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MemberRequestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row, parent, false);
        return new MemberRequestListAdapter.ViewHolder(view, mOnNoteListener);

    }

    @Override
    public void onBindViewHolder(@NonNull final MemberRequestListAdapter.ViewHolder holder, final int position) {
        Log.d("BINDING VIEW HOLDER", " " + String.valueOf(position));
        String name = requestList.get(position).getFirstName() + " " + requestList.get(position).getLastName();
        holder.nameText.setText(name);
        holder.usernameText.setText(requestList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {

        return requestList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;

        public TextView nameText;
        public TextView usernameText;
        public Button acceptButton;
        public Button declineButton;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            mView = itemView;
            this.onNoteListener = onNoteListener;
            nameText = mView.findViewById(R.id.textViewName);
            usernameText = mView.findViewById(R.id.textViewUsername);
            acceptButton = mView.findViewById(R.id.buttonAccept);
            acceptButton.setOnClickListener(this);
            declineButton = mView.findViewById(R.id.buttonDelete);
            declineButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.buttonAccept:
                    onNoteListener.onAcceptClick(getAdapterPosition());
                    break;
                case R.id.buttonDelete:
                    onNoteListener.onDeclineClick(getAdapterPosition());
                    break;
                default:
                    break;
            }
        }
    }
    public interface OnNoteListener{
        void onAcceptClick(int position);
        void onDeclineClick(int position);

    }
}



