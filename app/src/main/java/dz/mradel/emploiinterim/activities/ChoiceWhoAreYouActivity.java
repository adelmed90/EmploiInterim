package dz.mradel.emploiinterim.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dz.mradel.emploiinterim.databinding.ActivityChoiceWhoAreYouBinding;

public class ChoiceWhoAreYouActivity extends AppCompatActivity {
    private ActivityChoiceWhoAreYouBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChoiceWhoAreYouBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.emloyeur.setOnClickListener(view -> {
            Intent intent = new Intent(ChoiceWhoAreYouActivity.this, SignUpEmployeurActivity.class);
            startActivity(intent);
        });

        binding.jobSeeker.setOnClickListener(view -> {
            Intent intent = new Intent(ChoiceWhoAreYouActivity.this, SignUpDemandeurActivity.class);
            startActivity(intent);
        });
    }
}
