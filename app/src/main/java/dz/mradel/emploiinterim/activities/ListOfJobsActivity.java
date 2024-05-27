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
import dz.mradel.emploiinterim.adapters.ListOfJobsAdapter;

public class ListOfJobsActivity extends AppCompatActivity {

    ActivityListOfJobsBinding binding;

    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    List<Emploi> dataList;
    ListOfJobsAdapter adapter;
    String user="anonyme";

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

        Bundle bundle = getIntent().getExtras();


        if (bundle != null){
            // from HomePage
            binding.fab.setVisibility(View.GONE);
            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                user="demandeur";
            }

        }else {
            user="employeur";
        }

        dataList = new ArrayList<>();


        adapter = new ListOfJobsAdapter(ListOfJobsActivity.this, dataList, user);
        binding.recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Jobs list");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Emploi emploi = itemSnapshot.getValue(Emploi.class);
                    //emploi.setKey(itemSnapshot.getKey());

                    Bundle bundle = getIntent().getExtras();
                    String motCle, ville;

                    if (bundle != null){// from HomePage
                        motCle=bundle.getString("motCle");
                        ville=bundle.getString("ville");
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            if(emploi.getJobTitle().contains(motCle)&&emploi.getEmployeur().getAdresse().contains(ville)){
                                dataList.add(emploi);
                            }else {
                                //Toast.makeText(ListOfJobsActivity.this, "no jobs", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            user="demandeur";
                            if(emploi.getJobTitle().contains(motCle)&&emploi.getEmployeur().getAdresse().contains(ville)){
                                dataList.add(emploi);
                            }else {
                                //Toast.makeText(ListOfJobsActivity.this, "no jobs", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }else { //from Menu
                        user="employeur";
                        if(emploi.getEmployeur().getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                            dataList.add(emploi);
                        }else {
                            //Toast.makeText(ListOfJobsActivity.this, "no jobs", Toast.LENGTH_SHORT).show();
                        }
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
            if (emploi.getJobTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(emploi);
            }
        }
        adapter.searchDataList(searchList);
    }
}