package dz.mradel.emploiinterim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.databinding.ActivityJobDetailForEmployeurBinding;
import dz.mradel.emploiinterim.models.Condidature;
import dz.mradel.emploiinterim.models.Emploi;

public class JobDetailForEmployeurActivity extends AppCompatActivity {
    ActivityJobDetailForEmployeurBinding binding;
    String key = "";
    String imageUrl = "";
    private Emploi emploi;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityJobDetailForEmployeurBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Show progress dialog
        AlertDialog dialog = createProgressDialog();
        dialog.show();

        // Get the Emploi object from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            emploi = (Emploi) bundle.getSerializable("emploi");
            populateUIWithEmploiDetails(emploi);
            dialog.dismiss();
        }

        binding.cancelJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteJob();
            }
        });

        binding.listOfCandidates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(JobDetailForEmployeurActivity.this,ListOfCandidatesActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("emploi",emploi);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private AlertDialog createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        return builder.create();
    }
    private void populateUIWithEmploiDetails(Emploi emploi) {
        binding.jobTitleInDetail.setText(emploi.getJobTitle());
        binding.employeurTitle.setText(emploi.getEmployeur().getNomEntreprise());
        binding.ville.setText(emploi.getEmployeur().getAdresse());
        binding.detailJobDesc.setText(emploi.getJobDesc());
        key = emploi.getKey();
        imageUrl = emploi.getEmployeur().getLogo();
        Glide.with(this).load(imageUrl).into(binding.detailImage);
    }
    private void deleteJob() {
        deleteCandidature(key);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Jobs list");
        reference.child(key).removeValue().addOnSuccessListener(unused -> {
            Toast.makeText(JobDetailForEmployeurActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(JobDetailForEmployeurActivity.this, ListOfJobsActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            Log.e("DeletionError", "Error deleting data: " + e.getMessage());
            Toast.makeText(JobDetailForEmployeurActivity.this, "Failed to delete data", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteCandidature(final String emploiKey) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("candidatures");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Condidature condidature = itemSnapshot.getValue(Condidature.class);
                    if (condidature != null && condidature.getEmploi().getKey().equals(emploiKey)) {
                        databaseReference.child(itemSnapshot.getKey()).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(JobDetailForEmployeurActivity.this, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("DeletionError", "Error deleting data: " + e.getMessage());
                                    Toast.makeText(JobDetailForEmployeurActivity.this, "Failed to delete data", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseUpdate", "Database error: " + error.getMessage(), error.toException());
                Toast.makeText(JobDetailForEmployeurActivity.this, "Database error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}