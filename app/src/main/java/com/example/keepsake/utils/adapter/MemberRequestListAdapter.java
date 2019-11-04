package com.example.keepsake.utils.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keepsake.R;
import com.example.keepsake.database.firebaseSnapshot.User;

import java.util.List;

public class MemberRequestListAdapter extends RecyclerView.Adapter<MemberRequestListAdapter.ViewHolder> {


    public List<User> requestList;
    private OnNoteListener mOnNoteListener;
    public MemberRequestListAdapter(List<User> requestList, OnNoteListener onNoteListener){
        this.requestList = requestList;
        this.mOnNoteListener = onNoteListener;
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
    public MemberRequestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_row, parent, false);
        return new MemberRequestListAdapter.ViewHolder(view, mOnNoteListener);

    }

    /**
     * This method internally calls to update the RecyclerView.ViewHolder contents with the item at
     * the given position and also sets up some private fields to be used by RecyclerView.
     * @param holder A ViewHolder that holds a View of the given view type.
     * @param position item at the given position
     */
    @Override
    public void onBindViewHolder(@NonNull final MemberRequestListAdapter.ViewHolder holder, final int position) {
        Log.d("BINDING VIEW HOLDER", " " + String.valueOf(position));
        String name = requestList.get(position).getFirstName() + " " + requestList.get(position).getLastName();
        holder.nameText.setText(name);
        holder.usernameText.setText(requestList.get(position).getUsername());
    }

    /**
     * Gets the count of a member's request list
     * @return the number of member request in a list
     */
    @Override
    public int getItemCount() {

        return requestList.size();

    }

    /**
     * Constructor of a member request list class adapter.
     */
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



