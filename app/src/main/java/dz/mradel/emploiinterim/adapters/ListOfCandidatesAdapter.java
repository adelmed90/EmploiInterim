package dz.mradel.emploiinterim.adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.activities.DemandeurInfoActivity;
import dz.mradel.emploiinterim.models.Condidature;

public class ListOfCandidatesAdapter extends RecyclerView.Adapter<MyViewHolder2> {
    private final Context context;
    private List<Condidature> dataList;
    private final DatabaseReference databaseReference;

    public ListOfCandidatesAdapter(Context context, List<Condidature> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("candidatures");
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_candidates_item, parent, false);
        return new MyViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, @SuppressLint("RecyclerView") int position) {
        Condidature currentCondidature = dataList.get(position);

        Glide.with(context).load(currentCondidature.getDemandeur().getImageURL()).into(holder.profileImg);
        if(dataList.get(position).isAcceptByEmployeur()){
            Glide.with(context).load(R.drawable.refuse_black).into(holder.refuse);
            holder.refuse.setClickable(false);
            holder.accept.setClickable(false);
        }
        if(dataList.get(position).isRefuseByEmployeur()){
            Glide.with(context).load(R.drawable.accept_black).into(holder.accept);
            holder.refuse.setClickable(false);
            holder.accept.setClickable(false);
        }

        holder.resume.setOnClickListener(view -> openPdfFile(currentCondidature.getDemandeur().getPdfURL()));

        holder.accept.setOnClickListener(view -> {
            Glide.with(context).load(R.drawable.refuse_black).into(holder.refuse);
            holder.refuse.setClickable(false);
            holder.accept.setClickable(false);
            accept(currentCondidature);
        });

        holder.refuse.setOnClickListener(view -> {
            Glide.with(context).load(R.drawable.accept_black).into(holder.accept);
            holder.refuse.setClickable(false);
            holder.accept.setClickable(false);
            refuse(currentCondidature);
        });

        holder.profileImg.setOnClickListener(view -> {
            Intent intent = new Intent(context, DemandeurInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("condidature", currentCondidature);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<Condidature> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }

    private void openPdfFile(String pdfURL) {
        Uri pdfUri = Uri.parse(pdfURL);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application to open PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void accept(Condidature condidaturePar) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Condidature condidature = itemSnapshot.getValue(Condidature.class);
                    if (condidature != null && condidature.getEmploi().getKey().equals(condidaturePar.getEmploi().getKey())&&condidature.getDemandeur().getEmail().equals(condidaturePar.getDemandeur().getEmail())) {
                        condidature.setEnCours(false);
                        condidature.setAcceptByEmployeur(true);

                        itemSnapshot.getRef().setValue(condidature)
                                .addOnSuccessListener(aVoid -> Log.d("FirebaseUpdate", "Condidature updated successfully."))
                                .addOnFailureListener(e -> Log.e("FirebaseUpdate", "Failed to update condidature", e));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseUpdate", "Database error: " + error.getMessage(), error.toException());
            }
        });
    }

    private void refuse(Condidature condidaturePar) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Condidature condidature = itemSnapshot.getValue(Condidature.class);
                    if (condidature != null && condidature.getEmploi().getKey().equals(condidaturePar.getEmploi().getKey())&&condidature.getDemandeur().getEmail().equals(condidaturePar.getDemandeur().getEmail())) {
                        condidature.setEnCours(false);
                        condidature.setRefuseByEmployeur(true);

                        itemSnapshot.getRef().setValue(condidature)
                                .addOnSuccessListener(aVoid -> Log.d("FirebaseUpdate", "Condidature updated successfully."))
                                .addOnFailureListener(e -> Log.e("FirebaseUpdate", "Failed to update condidature", e));
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseUpdate", "Database error: " + error.getMessage(), error.toException());
            }
        });
    }
}

class MyViewHolder2 extends RecyclerView.ViewHolder {
    ImageView profileImg, resume, accept, refuse;
    CardView recCard;

    public MyViewHolder2(@NonNull View itemView) {
        super(itemView);
        profileImg = itemView.findViewById(R.id.profileImg);
        recCard = itemView.findViewById(R.id.recCard);
        resume = itemView.findViewById(R.id.resumeBtn);
        accept = itemView.findViewById(R.id.acceptBtn);
        refuse = itemView.findViewById(R.id.refuseBtn);
    }
}
