package dz.mradel.emploiinterim.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import dz.mradel.emploiinterim.activities.JobDetailForEmployeurActivity;
import dz.mradel.emploiinterim.models.Emploi;
import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.activities.DetailActivity;

public class ListOfJobsAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<Emploi> dataList;
    private String user;

    public ListOfJobsAdapter(Context context, List<Emploi> dataList, String user) {
        this.context = context;
        this.dataList = dataList;
        this.user=user;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_jobs_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(dataList.get(position).getEmployeur().getLogo()).into(holder.recImage);
        holder.recVille.setText(dataList.get(position).getEmployeur().getAdresse());
        holder.recJobTitle.setText(dataList.get(position).getJobTitle());
        holder.recEmpTitle.setText(dataList.get(position).getEmployeur().getNomEntreprise());
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.equals("employeur")){
                    Intent intent = new Intent(context, JobDetailForEmployeurActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("emploi",dataList.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, DetailActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("emploi",dataList.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<Emploi> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recJobTitle, recEmpTitle, recVille;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recEmpTitle = itemView.findViewById(R.id.recEmpTitle);
        recVille = itemView.findViewById(R.id.recVille);
        recJobTitle = itemView.findViewById(R.id.recJobTitle);
    }
}
