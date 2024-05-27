package dz.mradel.emploiinterim.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import dz.mradel.emploiinterim.databinding.ActivityLoginBinding;
import dz.mradel.emploiinterim.models.Condidature;
import dz.mradel.emploiinterim.models.Demandeur;
import dz.mradel.emploiinterim.models.Emploi;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    Emploi emploi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();

        // Get the Emploi object from the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            emploi = (Emploi) bundle.getSerializable("emploi");
        }

        binding.connecterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailTxt.getText().toString();
                String password = binding.passwordTxt.getText().toString();

                if (validateData(email, password)) {
                    dialog.show();
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser != null) {
                                    String uid = currentUser.getUid();
                                    checkUserCollection(uid,emploi);
                                }
                            } else {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        binding.forgetPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetActivity.class));
                finish();
            }
        });

        binding.inscriptionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ChoiceWhoAreYouActivity.class));
                finish();
            }
        });
    }

    private boolean validateData(String email, String password) {
        boolean isValid = true;
        if (email.isEmpty()) {
            binding.emailTxt.setError("Veuillez saisir votre email");
            isValid = false;
        } else if (password.isEmpty()) {
            binding.passwordTxt.setError("Veuillez saisir votre mot de passe");
            isValid = false;
        }
        return isValid;
    }

    private void checkUserCollection(String userId, Emploi emploi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("employeurs").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                            finish();
                        } else {
                            checkDemandeursCollection(userId, emploi);
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            System.err.println("Error checking 'employeurs' collection: " + e.getMessage());
                        }
                    }
                });
    }

    private void checkDemandeursCollection(String userId, Emploi emploi) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("demandeurs").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Demandeur demandeur=document.toObject(Demandeur.class);
                            if(emploi!=null){
                                sendApplication();
                            }else {
                                startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                                finish();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            System.err.println("Error checking 'demandeurs' collection: " + e.getMessage());
                        }
                    }
                });
    }

    private AlertDialog createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        return builder.create();
    }

    private void sendApplication() {
        AlertDialog dialog = createProgressDialog();
        dialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Jobs list");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference userDocRef = db.collection("demandeurs").document(uid);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Demandeur demandeur = documentSnapshot.toObject(Demandeur.class);
                if (demandeur != null) {
                    processApplication(databaseReference, demandeur, dialog);
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to parse Demandeur", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Document does not exist", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(LoginActivity.this, "Failed to retrieve document: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                    Emploi emploi = itemSnapshot.getValue(Emploi.class);
                    if (emploi != null) {
                        //emploi.setKey(itemSnapshot.getKey());

                        Condidature condidature = new Condidature(emploi, demandeur, true, false, false, false, false);
                        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                        FirebaseDatabase.getInstance().getReference("condidatures").child(currentDate)
                                .setValue(condidature).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Condidature envoyée", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(LoginActivity.this, MenuActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        //Bundle bundle=new Bundle();
                                        //bundle.putSerializable("emploi",emploi);
                                        //intent.putExtras(bundle);
                                        intent.putExtra("user", "demandeur");
                                        startActivity(intent);
                                        finish();
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(LoginActivity.this, "Failed to send condidature: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Failed to retrieve emploi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}
