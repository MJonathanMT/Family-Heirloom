package com.example.keepsake;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class OwnershipRecordViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private FirebaseFirestore db;
    private Context ctx;

    public OwnershipRecordViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        initialiseDB();
    }

    public void bind(Context ctx, OwnershipRecord record){
        this.ctx = ctx;
        TextView textViewName = mView.findViewById(R.id.textViewName);
        TextView textViewUsername = mView.findViewById(R.id.textViewUsername);
        TextView textViewFamilyName = mView.findViewById(R.id.textViewFamilyName);
        TextView textViewDescription = mView.findViewById(R.id.textViewDescription);
        TextView textViewStartDate = mView.findViewById(R.id.textViewStartDate);
        TextView textViewEndDate = mView.findViewById(R.id.textViewEndDate);

        final SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyyMMddhhmmss");
        final SimpleDateFormat finalTimeFormatter = new SimpleDateFormat("dd/MM/yyyy");
        String startDateString, endDateString;
        try {
            Date startDate = timeFormatter.parse(record.getStartDate());
            startDateString = finalTimeFormatter.format(startDate);

            Log.d("End date", "Test " + record.getEndDate());

            Date endDate = timeFormatter.parse(record.getEndDate());
            endDateString = finalTimeFormatter.format(endDate);
        } catch (Exception e){
            startDateString = record.getStartDate();
            endDateString = record.getEndDate();
        }

        textViewStartDate.setText(startDateString);
        textViewEndDate.setText(endDateString);
        textViewDescription.setText(record.getMemory());

        if (record.getOwnerID() != null){
            db.collection("user")
                    .document(record.getOwnerID())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                String name  = documentSnapshot.get("firstName", String.class) + " " + documentSnapshot.get("lastName", String.class);
                                textViewName.setText(name);
                                textViewUsername.setText(documentSnapshot.get("username", String.class));
                                textViewDescription.setText(record.getMemory());

                            }
                        }
                    });
        }

        if (record.getFamilyID() != null){
            db.collection("family_group")
                    .document(record.getFamilyID())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                textViewFamilyName.setText(documentSnapshot.get("familyName", String.class));
                            }
                        }
                    });
        }
    }

    public void setOnClickListener(View.OnClickListener listener){
        mView.setOnClickListener(listener);
    }

    public View getView(){
        return mView;
    }

    public void initialiseDB() {
        // TODO (naverill) put this function in a public utils class
        FirebaseApp.initializeApp(ctx);
        db = FirebaseFirestore.getInstance();
    }
}