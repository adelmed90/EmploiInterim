package dz.mradel.emploiinterim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.databinding.ActivityMenuBinding;

public class MenuActivity extends AppCompatActivity {
    private ActivityMenuBinding binding;
    private String userType = "employeur";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userType = bundle.getString("user", "employeur");
            if (userType.equals("demandeur")) {
                setDemandeurUI();
            }
        }

        setClickListeners();
    }

    private void setClickListeners() {
        binding.listOfJobOffres.setOnClickListener(view -> {
            //startActivity(new Intent(MenuActivity.this, ListOfJobsActivity.class));
            Intent intent = new Intent(MenuActivity.this,
                    userType.equals("employeur") ? ListOfJobsActivity.class : ListOfDemandeurApplicationsActivity.class);
            startActivity(intent);
        });

        binding.newJobOffre.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this,
                    userType.equals("employeur") ? NewJobOfferActivity.class : HomePageActivity.class);
            startActivity(intent);
            //finish();
        });

        binding.emloyeurInfo.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this,
                    userType.equals("employeur") ? EmployeurInfoActivity.class : DemandeurInfoActivity.class);
            startActivity(intent);
        });

        binding.logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(MenuActivity.this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setDemandeurUI() {
        binding.newJobTxt.setText("Recherche un emploi");
        binding.listJobsTxt.setText("mes candidatures");
        binding.infoImg.setImageResource(R.drawable.profile);
        binding.newJobImg.setImageResource(R.drawable.jobseeker);
    }
}
