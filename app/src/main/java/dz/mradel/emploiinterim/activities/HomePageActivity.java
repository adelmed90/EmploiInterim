package dz.mradel.emploiinterim.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dz.mradel.emploiinterim.R;
import dz.mradel.emploiinterim.databinding.ActivityHomePageBinding;

public class HomePageActivity extends AppCompatActivity {
    private ActivityHomePageBinding binding;
    private String user;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private boolean locationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        AlertDialog dialog = createProgressDialog();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            dialog.show();
            String uid = currentUser.getUid();
            checkUserCollection(uid, dialog::dismiss);
        } else {
            getLocationPermission();
        }

        setupListeners(dialog);
    }

    private AlertDialog createProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        return builder.create();
    }

    private void setupListeners(AlertDialog dialog) {
        binding.searchBtn.setOnClickListener(view -> {
            String motCle = binding.motCleTxt.getText().toString();
            String ville = binding.villeTxt.getText().toString();

            if (validateData(motCle, ville)) {
                Intent intent = new Intent(HomePageActivity.this, ListOfJobsActivity.class);
                intent.putExtra("motCle", motCle);
                intent.putExtra("ville", ville);
                startActivity(intent);
            }
        });

        binding.connexionBtn.setOnClickListener(view -> {
            Intent intent;
            dialog.show();
            if (user == null) {
                intent = new Intent(HomePageActivity.this, LoginActivity.class);
            } else {
                intent = new Intent(HomePageActivity.this, MenuActivity.class);
                intent.putExtra("user", user);
            }
            startActivity(intent);
            finish();
        });

        binding.logoutBtn.setOnClickListener(view -> {
            dialog.show();

            FirebaseAuth.getInstance().signOut();

            getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            binding.connexionTxt.setText("Connexion");
            Glide.with(HomePageActivity.this).load(R.drawable.administrator_male).into(binding.profileImg);
            binding.logoutBtn.setVisibility(View.GONE);
            user = null;

            // Dismiss dialog after a short delay
            new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 500);
        });
    }

    private boolean validateData(String motCle, String ville) {
        boolean isValid = true;
        if (motCle.isEmpty()) {
            binding.motCleTxt.setError("Veuillez saisir un mot clé");
            isValid = false;
        }
        if (ville.isEmpty()) {
            binding.villeTxt.setError("Veuillez saisir lieu de recherche");
            isValid = false;
        }
        return isValid;
    }

    private void checkUserCollection(String userId, CheckUserCollectionCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("employeurs").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            user = "employeur";
                            navigateToMenuActivity();
                        } else {
                            checkDemandeursCollection(userId, callback);
                        }
                    } else {
                        handleFirestoreError(task.getException());
                        callback.onComplete();
                    }
                });
    }

    private void checkDemandeursCollection(String userId, CheckUserCollectionCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("demandeurs").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            user = "demandeur";
                            getLocationPermission();
                            updateUIWithDemandeurData(document);
                        }
                    } else {
                        handleFirestoreError(task.getException());
                    }
                    callback.onComplete();
                });
    }

    private void navigateToMenuActivity() {
        Intent intent = new Intent(HomePageActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUIWithDemandeurData(DocumentSnapshot document) {
        binding.logoutBtn.setVisibility(View.VISIBLE);
        String nom = document.getString("nomPrenom");
        String pic = document.getString("imageURL");
        binding.connexionTxt.setText(nom);
        Glide.with(this).load(pic).into(binding.profileImg);
    }

    private void handleFirestoreError(Exception e) {
        if (e != null) {
            System.err.println("Firestore error: " + e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        getDeviceLocation();
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        if (locationPermissionGranted) {
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location location = task.getResult();
                                fetchAddressFromLatLong(location.getLatitude(), location.getLongitude());
                            } else {
                                Toast.makeText(HomePageActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void fetchAddressFromLatLong(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();
                binding.villeTxt.setText(cityName);
            } else {
                binding.villeTxt.setText("Ville introuvable");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ville introuvable", Toast.LENGTH_SHORT).show();
        }
    }

    interface CheckUserCollectionCallback {
        void onComplete();
    }
}
