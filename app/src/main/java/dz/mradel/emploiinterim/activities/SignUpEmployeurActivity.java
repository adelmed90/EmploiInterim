package dz.mradel.emploiinterim.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.databinding.ActivitySignUpEmployeurBinding;
import dz.mradel.emploiinterim.models.Employeur;

public class SignUpEmployeurActivity extends AppCompatActivity {
    private ActivitySignUpEmployeurBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Uri uri;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpEmployeurBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            binding.uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(SignUpEmployeurActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        binding.inscrireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomEntreprise = binding.profileTxt.getText().toString();
                String email = binding.emailTxt.getText().toString();
                String password = binding.passwordTxt.getText().toString();
                String adresse = binding.adresseTxt.getText().toString();
                String telephone = binding.phoneTxt.getText().toString();
                String siteweb = binding.websiteTxt.getText().toString();
                String linkedin = binding.linkedinTxt.getText().toString();
                String facebook = binding.linkedinTxt.getText().toString();

                if (validateData(nomEntreprise, email, password, adresse)) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                saveData(nomEntreprise, email, password, adresse, telephone, siteweb, linkedin, facebook);
                            } else {
                                Toast.makeText(SignUpEmployeurActivity.this, "Failed to register: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        binding.connexionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpEmployeurActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private boolean validateData(String nomEntreprise, String email, String password, String adresse) {
        boolean isValid = true;
        if (email.isEmpty()) {
            binding.emailTxt.setError("Veuillez saisir votre email");
            isValid = false;
        } else if (password.isEmpty()) {
            binding.passwordTxt.setError("Veuillez saisir votre mot de passe");
            isValid = false;
        } else if (nomEntreprise.isEmpty()) {
            binding.profileTxt.setError("Veuillez saisir le nom de l'entreprise");
            isValid = false;
        } else if (adresse.isEmpty()) {
            binding.adresseTxt.setError("Veuillez saisir l'adresse");
            isValid = false;
        }
        return isValid;
    }

    public void saveData(String nomEntreprise, String email, String password, String adresse, String telephone, String siteweb, String linkedin, String facebook) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpEmployeurActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri urlImage = task.getResult();
                            imageURL = urlImage.toString();
                            uploadData(nomEntreprise, email, password, adresse, telephone, siteweb, linkedin, facebook, imageURL);
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(SignUpEmployeurActivity.this, "Failed to upload image: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(SignUpEmployeurActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String nomEntreprise, String email, String password, String adresse, String telephone, String siteweb, String linkedin, String facebook, String imageURL) {
        Employeur employeur = new Employeur(nomEntreprise, adresse, telephone, siteweb, linkedin, facebook, email, password, imageURL);

        firestore.collection("employeurs").document(auth.getCurrentUser().getUid()).set(employeur).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(SignUpEmployeurActivity.this, MenuActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpEmployeurActivity.this, "Failed to register: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
