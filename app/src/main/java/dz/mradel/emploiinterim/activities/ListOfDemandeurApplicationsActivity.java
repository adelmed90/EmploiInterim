package dz.mradel.emploiinterim.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import dz.mradel.emploiinterim.adapters.ListOfApplicationsAdapter;
import dz.mradel.emploiinterim.adapters.ListOfJobsAdapter;
import dz.mradel.emploiinterim.databinding.ActivityListOfDemandeurApplicationsBinding;
import dz.mradel.emploiinterim.models.Condidature;
import dz.mradel.emploiinterim.models.Emploi;

public class ListOfDemandeurApplicationsActivity extends AppCompatActivity {
    ActivityListOfDemandeurApplicationsBinding binding;

    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    List<Condidature> dataList;
    ListOfApplicationsAdapter adapter;
    String userMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityListOfDemandeurApplicationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userMail=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ListOfDemandeurApplicationsActivity.this, 1);
        binding.recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListOfDemandeurApplicationsActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();


        adapter = new ListOfApplicationsAdapter(ListOfDemandeurApplicationsActivity.this, dataList);
        binding.recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("candidatures");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Condidature condidature = itemSnapshot.getValue(Condidature.class);
                    if(userMail.equals(condidature.getDemandeur().getEmail())){
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