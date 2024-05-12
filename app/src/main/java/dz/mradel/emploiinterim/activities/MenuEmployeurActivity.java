package dz.mradel.emploiinterim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import dz.mradel.emploiinterim.databinding.ActivityMenuEmployeurBinding;

public class MenuEmployeurActivity extends AppCompatActivity {
    ActivityMenuEmployeurBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMenuEmployeurBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.listOfJobOffres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MenuEmployeurActivity.this, ListOfJobsActivity.class);
                startActivity(intent);
            }
        });

        binding.newJobOffre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MenuEmployeurActivity.this, NewJobOfferActivity.class);
                startActivity(intent);
            }
        });
        binding.emloyeurInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MenuEmployeurActivity.this, EmployeurInfoActivity.class);
                startActivity(intent);
            }
        });

    }
}