package dz.mradel.emploiinterim.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;

import dz.mradel.emploiinterim.databinding.ActivityNewJobOfferBinding;
import dz.mradel.emploiinterim.models.Emploi;
import dz.mradel.emploiinterim.models.Employeur;

public class NewJobOfferActivity extends AppCompatActivity {
    ActivityNewJobOfferBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNewJobOfferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });
    }

    public void uploadData(){
        String title = binding.uploadTopic.getText().toString();
        String desc = binding.uploadDesc.getText().toString();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is signed in
            String uid = currentUser.getUid().toString();

            // Retrieve additional user information from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("employeurs").document(uid);
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nomEntreprise = documentSnapshot.getString("nomEntreprise");
                    String adresse = documentSnapshot.getString("adresse");
                    String email= documentSnapshot.getString("email");
                    String logo = documentSnapshot.getString("logo");
                    Employeur employeur= new Employeur(nomEntreprise,adresse,email,logo);

                    Emploi emploi = new Emploi(title, desc, employeur);
                    String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                    FirebaseDatabase.getInstance().getReference("Jobs list").child(currentDate)
                            .setValue(emploi).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(NewJobOfferActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(NewJobOfferActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Toast.makeText(NewJobOfferActivity.this, "documentSnapshot does not exists() ", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> {
                // Handle failure
            });
        } else {
            Toast.makeText(NewJobOfferActivity.this, "user not exist", Toast.LENGTH_LONG).show();
        }

    }
}