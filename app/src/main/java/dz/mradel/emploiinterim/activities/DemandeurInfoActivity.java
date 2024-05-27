package dz.mradel.emploiinterim.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import dz.mradel.emploiinterim.databinding.ActivityDemandeurInfoBinding;
import dz.mradel.emploiinterim.models.Condidature;

public class DemandeurInfoActivity extends AppCompatActivity {

    ActivityDemandeurInfoBinding binding;
    private String pdfURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDemandeurInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            Condidature condidature = (Condidature) bundle.getSerializable("condidature");
            // Display user details
            binding.profileTxt.setText(condidature.getDemandeur().getNomPrenom());
            binding.phoneTxt.setText(condidature.getDemandeur().getTelephone());
            binding.adresseTxt.setText(condidature.getDemandeur().getAdresse());
            binding.commentTxt.setText(condidature.getDemandeur().getCommentaire());
            binding.nationaliteTxt.setText(condidature.getDemandeur().getNationalite());
            binding.birthdayTxt.setText(condidature.getDemandeur().getDateDeNaissance());
            binding.emailTxt.setText(condidature.getDemandeur().getEmail());
            Glide.with(this).load(condidature.getDemandeur().getImageURL()).into(binding.detailImage);
            if (condidature.getDemandeur().getPdfURL() != null && !condidature.getDemandeur().getPdfURL().isEmpty()) {
                binding.resume.setText("Curriculum vitae");
                binding.resume.setOnClickListener(view -> {
                    openPdfFile(condidature.getDemandeur().getPdfURL());
                });
            }
            binding.inscrireBtn.setVisibility(View.INVISIBLE);

        }else {
            // Get current user
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                String uid = user.getUid();

                // Retrieve additional user information from Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userDocRef = db.collection("demandeurs").document(uid);
                userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nomPrenom = documentSnapshot.getString("nomPrenom");
                        String adresse = documentSnapshot.getString("adresse");
                        String telephone = documentSnapshot.getString("telephone");
                        String commentaire = documentSnapshot.getString("commentaire");
                        String nationalite = documentSnapshot.getString("nationalite");
                        String dateDeNaissance = documentSnapshot.getString("dateDeNaissance");
                        String email = documentSnapshot.getString("email");
                        String imageURL = documentSnapshot.getString("imageURL");
                        pdfURL = documentSnapshot.getString("pdfURL");

                        // Display user details
                        binding.profileTxt.setText(nomPrenom);
                        binding.phoneTxt.setText(telephone);
                        binding.adresseTxt.setText(adresse);
                        binding.commentTxt.setText(commentaire);
                        binding.nationaliteTxt.setText(nationalite);
                        binding.birthdayTxt.setText(dateDeNaissance);
                        binding.emailTxt.setText(email);
                        Glide.with(this).load(imageURL).into(binding.detailImage);
                        if (pdfURL != null && !pdfURL.isEmpty()) {
                            binding.resume.setText("Curriculum vitae");
                            binding.resume.setOnClickListener(view -> {
                                openPdfFile(pdfURL);
                            });
                        }

                    } else {
                        Toast.makeText(DemandeurInfoActivity.this, "Document does not exist.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(DemandeurInfoActivity.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
                });
            }
        }


    }

    private void openPdfFile(String pdfURL) {
        Uri pdfUri = Uri.parse(pdfURL);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application to open PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
