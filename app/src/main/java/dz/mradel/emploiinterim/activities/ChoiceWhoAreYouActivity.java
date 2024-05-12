package dz.mradel.emploiinterim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.databinding.ActivityChoiceWhoAreYouBinding;

public class ChoiceWhoAreYouActivity extends AppCompatActivity {
    ActivityChoiceWhoAreYouBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChoiceWhoAreYouBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.emloyeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChoiceWhoAreYouActivity.this, SignUpEmployeurActivity.class);
                startActivity(intent);
            }
        });

        binding.jobSeeker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}