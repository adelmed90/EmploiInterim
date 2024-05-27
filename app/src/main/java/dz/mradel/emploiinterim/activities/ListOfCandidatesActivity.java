package dz.mradel.emploiinterim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.adapters.ListOfCandidatesAdapter;
import dz.mradel.emploiinterim.adapters.ListOfJobsAdapter;
import dz.mradel.emploiinterim.databinding.ActivityListOfCandidatesBinding;
import dz.mradel.emploiinterim.databinding.ActivityListOfJobsBinding;
import dz.mradel.emploiinterim.models.Condidature;
import dz.mradel.emploiinterim.models.Emploi;

public class ListOfCandidatesActivity extends AppCompatActivity {
    ActivityListOfCandidatesBinding binding;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    List<Condidature> dataList;
    ListOfCandidatesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityListOfCandidatesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ListOfCandidatesActivity.this, 1);
        binding.recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListOfCandidatesActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new ListOfCandidatesAdapter(ListOfCandidatesActivity.this, dataList);
        binding.recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("candidatures");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Condidature condidature = itemSnapshot.getValue(Condidature.class);

                    Bundle bundle = getIntent().getExtras();
                    Emploi emploi= (Emploi) bundle.getSerializable("emploi");
                    if(condidature.getEmploi().getKey().equals(emploi.getKey())){
                        dataList.add(condidature);
                    }

                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

    }
}