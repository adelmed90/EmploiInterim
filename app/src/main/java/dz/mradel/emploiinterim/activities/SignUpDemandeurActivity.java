package dz.mradel.emploiinterim.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
import dz.mradel.emploiinterim.databinding.ActivitySignUpDemandeurBinding;
import dz.mradel.emploiinterim.models.Demandeur;

public class SignUpDemandeurActivity extends AppCompatActivity {
    private ActivitySignUpDemandeurBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Uri imageUri, pdfUri;
    private String imageURL, pdfURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpDemandeurBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize ActivityResultLaunchers for image and PDF selection
        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            imageUri = data != null ? data.getData() : null;
                            if (imageUri != null) {
                                binding.uploadImage.setImageURI(imageUri);
                            } else {
                                Toast.makeText(SignUpDemandeurActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        ActivityResultLauncher<Intent> pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            pdfUri = data != null ? data.getData() : null;
                            if (pdfUri != null) {
                                String pdfName = getFileNameFromUri(pdfUri);
                                binding.resumeTxt.setText(pdfName);
                                Toast.makeText(SignUpDemandeurActivity.this, "Selected PDF: " + pdfName, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpDemandeurActivity.this, "No PDF Selected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                imagePickerLauncher.launch(photoPicker);
            }
        });

        binding.resumeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                pdfPickerLauncher.launch(Intent.createChooser(intent, "Select PDF"));
            }
        });

        binding.inscrireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomPrenom = binding.profileTxt.getText().toString();
                String email = binding.emailTxt.getText().toString();
                String password = binding.passwordTxt.getText().toString();
                String adresse = binding.adresseTxt.getText().toString();
                String telephone = binding.phoneTxt.getText().toString();
                String nationalite = binding.nationalityTxt.getText().toString();
                String dateDeNaissance = binding.birthDayTxt.getText().toString();
                String commentaire = binding.commentTxt.getText().toString();

                if (validateData(nomPrenom, email, password)) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                saveData(nomPrenom, email, password, adresse, telephone, nationalite, dateDeNaissance, commentaire);
                            } else {
                                Toast.makeText(SignUpDemandeurActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        binding.connexionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpDemandeurActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private boolean validateData(String nomPrenom, String email, String password) {
        boolean isValid = true;
        if (email.isEmpty()) {
            binding.emailTxt.setError("Veuillez saisir votre email");
            isValid = false;
        } else if (password.isEmpty()) {
            binding.passwordTxt.setError("Veuillez saisir votre mot de passe");
            isValid = false;
        } else if (nomPrenom.isEmpty()) {
            binding.profileTxt.setError("Veuillez saisir votre nom et prénom SVP");
            isValid = false;
        }
        return isValid;
    }

    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void saveData(String nomPrenom, String email, String password, String adresse, String telephone, String nationalite, String dateDeNaissance, String commentaire) {
        StorageReference storageReferenceIMG = FirebaseStorage.getInstance().getReference().child("Android Images").child(imageUri.getLastPathSegment());
        StorageReference storageReferencePDF = FirebaseStorage.getInstance().getReference().child("Android PDF").child(pdfUri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpDemandeurActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReferenceIMG.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri urlImage = task.getResult();
                            imageURL = urlImage.toString();
                            if (imageURL != null && pdfURL != null) {
                                uploadData(nomPrenom, email, password, adresse, telephone, nationalite, dateDeNaissance, commentaire, imageURL, pdfURL);
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(SignUpDemandeurActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        storageReferencePDF.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri urlPDF = task.getResult();
                            pdfURL = urlPDF.toString();
                            if (imageURL != null && pdfURL != null) {
                                uploadData(nomPrenom, email, password, adresse, telephone, nationalite, dateDeNaissance, commentaire, imageURL, pdfURL);
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(SignUpDemandeurActivity.this, "Failed to upload PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String nomPrenom, String email, String password, String adresse, String telephone, String nationalite, String dateDeNaissance, String commentaire, String imageURL, String pdfURL) {
        Demandeur demandeur = new Demandeur(nomPrenom, email, password, adresse, telephone, nationalite, dateDeNaissance, commentaire, imageURL, pdfURL);
        firestore.collection("demandeurs").document(auth.getCurrentUser().getUid()).set(demandeur).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(SignUpDemandeurActivity.this, HomePageActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpDemandeurActivity.this, "Failed to register: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

