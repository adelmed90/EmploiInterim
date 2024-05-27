package dz.mradel.emploiinterim.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.databinding.ActivityDetailBinding;
import dz.mradel.emploiinterim.models.Condidature;
import dz.mradel.emploiinterim.models.Demandeur;
import dz.mradel.emploiinterim.models.Emploi;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private String key = "";
    private String imageUrl = "";
    private Emploi emploi;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
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
        }

        // Handle current user state
        if (currentUser != null) {
            checkDemandeursCollection(currentUser.getUid(), () -> dialog.dismiss());
        } else {
            setupUIForNonAuthenticatedUser();
            dialog.dismiss();
        }
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

    private void setupUIForNonAuthenticatedUser() {
        binding.applyBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("emploi", emploi);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        binding.cancelBtn.setVisibility(View.GONE);
    }

    private void checkDemandeursCollection(String userId, CheckUserCollectionCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("demandeurs").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            checkApplication();
                            setupDemandeurUI();
                        }
                    } else {
                        handleFirestoreError(task.getException());
                    }
                    callback.onComplete();
                });
    }

    private void handleFirestoreError(Exception e) {
        if (e != null) {
            Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("DetailActivity", "Firestore error", e);
        }
    }

    interface CheckUserCollectionCallback {
        void onComplete();
    }

    @SuppressLint("ResourceAsColor")
    private void setupDemandeurUI() {
        binding.applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendApplication();
                binding.cancelBtn.setVisibility(View.VISIBLE);
                binding.applyBtn.setVisibility(View.GONE);
            }
        });

        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCandidature(emploi.getKey());
                binding.applyBtn.setVisibility(View.VISIBLE);
                binding.cancelBtn.setVisibility(View.GONE);
            }
        });

    }

    private void deleteCandidature(final String emploiKey) {
        AlertDialog dialog = createProgressDialog();
        dialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("candidatures");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Condidature condidature = itemSnapshot.getValue(Condidature.class);
                    if (condidature != null && condidature.getEmploi().getKey().equals(emploiKey)) {
                        databaseReference.child(itemSnapshot.getKey()).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(DetailActivity.this, "Demande annulée", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("DeletionError", "Error deleting data: " + e.getMessage());
                                    Toast.makeText(DetailActivity.this, "Failed to delete data", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseUpdate", "Database error: " + error.getMessage(), error.toException());
                Toast.makeText(DetailActivity.this, "Database error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendApplication() {
        AlertDialog dialog = createProgressDialog();
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Jobs list");
        String uid = currentUser.getUid();

        DocumentReference userDocRef = db.collection("demandeurs").document(uid);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Demandeur demandeur = documentSnapshot.toObject(Demandeur.class);
                if (demandeur != null) {
                    processApplication(databaseReference, demandeur, dialog);
                } else {
                    Toast.makeText(DetailActivity.this, "Failed to parse Demandeur", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(DetailActivity.this, "Document does not exist", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(DetailActivity.this, "Failed to retrieve document: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("DetailActivity", "Error retrieving document", e);
            dialog.dismiss();
        });
    }

    private void processApplication(DatabaseReference databaseReference, Demandeur demandeur, AlertDialog dialog) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Emploi emploiThis = itemSnapshot.getValue(Emploi.class);
                    if (emploiThis != null && emploiThis.getKey().equals(emploi.getKey())) {
                        //emploi.setKey(itemSnapshot.getKey());

                        Condidature condidature = new Condidature(emploiThis, demandeur, true, false, false, false, false);
                        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                        FirebaseDatabase.getInstance().getReference("candidatures").child(currentDate)
                                .setValue(condidature).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(DetailActivity.this, "Candidature envoyée", Toast.LENGTH_SHORT).show();
                                        //binding.Btn.setText("Annuler ma candidature");
                                        //binding.Btn.setBackgroundColor(R.color.red);
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(DetailActivity.this, "Failed to send candidature: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Failed to retrieve emploi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void checkApplication() {
        AlertDialog dialog = createProgressDialog();
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference databaseReferenceJobs = FirebaseDatabase.getInstance().getReference("Jobs list");
        DatabaseReference databaseReferenceCandidature = FirebaseDatabase.getInstance().getReference("candidatures");
        String uid = currentUser.getUid();

        DocumentReference userDocRef = db.collection("demandeurs").document(uid);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Demandeur demandeur = documentSnapshot.toObject(Demandeur.class);
                if (demandeur != null) {
                    processApplicationCheck(databaseReferenceJobs, databaseReferenceCandidature, demandeur, dialog);
                } else {
                    Toast.makeText(DetailActivity.this, "Failed to parse Demandeur", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(DetailActivity.this, "Document does not exist", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(DetailActivity.this, "Failed to retrieve document: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("DetailActivity", "Error retrieving document", e);
            dialog.dismiss();
        });
    }

    private void processApplicationCheck(DatabaseReference databaseReferenceJobs, DatabaseReference databaseReferenceCandidature, Demandeur demandeur, AlertDialog dialog) {
        databaseReferenceJobs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Emploi emploiThis = itemSnapshot.getValue(Emploi.class);
                    if (emploiThis != null) {
                        //emploiThis.setKey(itemSnapshot.getKey());
                        if(emploiThis.getKey().equals(emploi.getKey())){
                            checkCandidatures(databaseReferenceCandidature, demandeur, emploiThis, dialog);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Failed to retrieve emploi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void checkCandidatures(DatabaseReference databaseReferenceCandidature, Demandeur demandeur, Emploi emploi, AlertDialog dialog) {
        databaseReferenceCandidature.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean applicationExists = false;
            @Override
            public void onDataChange(@NonNull DataSnapshot candidatureSnapshot) {
                for (DataSnapshot candidatureItemSnapshot : candidatureSnapshot.getChildren()) {
                    Condidature condidature = candidatureItemSnapshot.getValue(Condidature.class);
                    if (condidature != null
                            && condidature.getDemandeur().getEmail().equals(demandeur.getEmail())
                            && condidature.getEmploi().getKey().equals(emploi.getKey())) {
                        applicationExists = true;
                        break;
                    }
                }
                handleApplicationCheckResult(applicationExists);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Failed to retrieve candidatures: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void handleApplicationCheckResult(boolean applicationExists) {
        if (applicationExists) {
            Toast.makeText(DetailActivity.this, "Demande déjà faite", Toast.LENGTH_SHORT).show();
            binding.cancelBtn.setVisibility(View.VISIBLE);
            binding.applyBtn.setVisibility(View.GONE);
        }
        //setupDemandeurUI();
    }
}
