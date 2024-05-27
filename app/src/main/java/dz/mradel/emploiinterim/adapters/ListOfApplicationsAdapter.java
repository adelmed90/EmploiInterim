package dz.mradel.emploiinterim.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.activities.DetailActivity;
import dz.mradel.emploiinterim.models.Condidature;

public class ListOfApplicationsAdapter extends RecyclerView.Adapter<MyViewHolder3> {
    private Context context;
    private List<Condidature> dataList;


    public ListOfApplicationsAdapter(Context context, List<Condidature> dataList) {
        this.context = context;
        this.dataList = dataList;

    }

    @NonNull
    @Override
    public MyViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_applications_item, parent, false);
        return new MyViewHolder3(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder3 holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(dataList.get(position).getEmploi().getEmployeur().getLogo()).into(holder.recImage);
        holder.recVille.setText(dataList.get(position).getEmploi().getEmployeur().getAdresse());
        holder.recJobTitle.setText(dataList.get(position).getEmploi().getJobTitle());
        holder.recEmpTitle.setText(dataList.get(position).getEmploi().getEmployeur().getNomEntreprise());
        if(dataList.get(position).isEnCours()){
            holder.recState.setText("En cours...");

        } else if (dataList.get(position).isAcceptByEmployeur()) {
            holder.recState.setText("accepté");
            holder.recState.setTextColor(Color.GREEN);

        } else if (dataList.get(position).isRefuseByEmployeur()) {
            holder.recState.setText("refusé");
            holder.recState.setTextColor(Color.RED);

        }
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("emploi",dataList.get(position).getEmploi());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
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
}

class MyViewHolder3 extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recJobTitle, recEmpTitle, recVille, recState;
    CardView recCard;

    public MyViewHolder3(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recEmpTitle = itemView.findViewById(R.id.recEmpTitle);
        recVille = itemView.findViewById(R.id.recVille);
        recState=itemView.findViewById(R.id.recState);
        recJobTitle = itemView.findViewById(R.id.recJobTitle);
    }
}
