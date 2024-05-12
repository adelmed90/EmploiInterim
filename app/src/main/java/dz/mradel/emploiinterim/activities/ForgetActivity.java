package dz.mradel.emploiinterim.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.databinding.ActivityForgetBinding;

public class ForgetActivity extends AppCompatActivity {
    ActivityForgetBinding binding;
    FirebaseAuth auth;
    //ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth= FirebaseAuth.getInstance();

        //progressDialog=new ProgressDialog(this);
        //progressDialog.setTitle("create your account");
        //progressDialog.setMessage("PLease wait");

        AlertDialog.Builder builder = new AlertDialog.Builder(ForgetActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();

        binding.recPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=binding.emailTxt.getText().toString();
                dialog.dismiss();
                if(email.isEmpty()){
                    binding.emailTxt.setError("Veuillez saisir votre email");
                }else {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(ForgetActivity.this,"please check your email",Toast.LENGTH_SHORT).show();
                            }else {
                                dialog.dismiss();
                                Toast.makeText(ForgetActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgetActivity.this,LoginActivity.class));
                                finish();
                            }
                        }
                    });
                }

            }
        });

        binding.connexionTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgetActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}