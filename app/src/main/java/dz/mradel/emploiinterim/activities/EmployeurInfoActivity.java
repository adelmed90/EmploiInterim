package dz.mradel.emploiinterim.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.databinding.ActivityEmployeurInfoBinding;

public class EmployeurInfoActivity extends AppCompatActivity {
    ActivityEmployeurInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEmployeurInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User is signed in
            //String email = user.getEmail().toString();
            String uid = user.getUid().toString();


            // Retrieve additional user information from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("employeurs").document(uid);
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nomEntreprise=documentSnapshot.getString("nomEntreprise");
                    String adresse=documentSnapshot.getString("adresse");
                    String telephone=documentSnapshot.getString("telephone");
                    String siteweb=documentSnapshot.getString("siteweb");
                    String linkedin=documentSnapshot.getString("linkedin");
                    String facebook=documentSnapshot.getString("facebook");
                    String email=documentSnapshot.getString("email");
                    String logo=documentSnapshot.getString("logo");

                    // Display user email and name
                    binding.profileTxt.setText(nomEntreprise);
                    binding.phoneTxt.setText(telephone);
                    binding.adresseTxt.setText(adresse);
                    binding.websiteTxt.setText(siteweb);
                    binding.linkedinTxt.setText(linkedin);
                    binding.facebookTxt.setText(facebook);
                    binding.emailTxt.setText(email);
                    Glide.with(this).load(logo).into(binding.detailImage);
                }else {
                    Toast.makeText(EmployeurInfoActivity.this, "documentSnapshot does not exists() ", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> {
                // Handle failure
            });
        }

    }
}