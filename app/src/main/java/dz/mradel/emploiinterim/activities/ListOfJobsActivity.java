package dz.mradel.emploiinterim.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dz.mradel.emploiinterim.databinding.ActivityListOfJobsBinding;
import dz.mradel.emploiinterim.models.Emploi;
import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.adapters.EmployeurAdapter;

public class ListOfJobsActivity extends AppCompatActivity {

    ActivityListOfJobsBinding binding;

    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    List<Emploi> dataList;
    EmployeurAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityListOfJobsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.search.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ListOfJobsActivity.this, 1);
        binding.recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListOfJobsActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new EmployeurAdapter(ListOfJobsActivity.this, dataList);
        binding.recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Jobs list");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Emploi emploi = itemSnapshot.getValue(Emploi.class);

                    emploi.setKey(itemSnapshot.getKey());
                    if(emploi.getEmployeur().getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        dataList.add(emploi);
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

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListOfJobsActivity.this, NewJobOfferActivity.class);
                startActivity(intent);
            }
        });

    }
    public void searchList(String text){
        ArrayList<Emploi> searchList = new ArrayList<>();
        for (Emploi emploi : dataList){
            if (emploi.getDataTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(emploi);
            }
        }
        adapter.searchDataList(searchList);
    }
}