package com.example.keepsake.memberRequest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;

import java.util.List;

public class MemberRequestListAdapter extends RecyclerView.Adapter<MemberRequestListAdapter.ViewHolder> {

    public List<MemberRequests> requestList;
    private OnNoteListener mOnNoteListener;
    public MemberRequestListAdapter(List<MemberRequests> requestList, OnNoteListener onNoteListener){
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
        holder.nameText.setText(requestList.get(position).getName());
    }

    @Override
    public int getItemCount() {

        return requestList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;

        public TextView nameText;
        public Button acceptButton;
        public Button declineButton;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            mView = itemView;
            this.onNoteListener = onNoteListener;
            nameText = mView.findViewById(R.id.name);
            acceptButton = mView.findViewById(R.id.acceptMemberButton);
            acceptButton.setOnClickListener(this);
            declineButton = mView.findViewById(R.id.declineMemberButton);
            declineButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.acceptMemberButton:
                    onNoteListener.onAcceptClick(getAdapterPosition());
                    break;
                case R.id.declineMemberButton:
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



