package com.example.registration_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DashboardActivity extends AppCompatActivity {
    // UI elements
    private TextView userFullName, userUsername, userEmail, userSex, userDob, userAge;
    private Button logoutButton;

    // Database
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Database
        db = FirebaseFirestore.getInstance();

        // Find Views
        userFullName = findViewById(R.id.user_fullname);
        userUsername = findViewById(R.id.user_username);
        userEmail = findViewById(R.id.user_email);
        userSex = findViewById(R.id.user_sex);
        userDob = findViewById(R.id.user_dob);
        userAge = findViewById(R.id.user_age);
        logoutButton = findViewById(R.id.button_logout);

        // Fetch user data
        fetchUserData();

        // Log Out Listener
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchUserData() {
        String userId = getIntent().getStringExtra("userId");

        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Extract Data
                String fullName = documentSnapshot.getString("fullName");
                String username = documentSnapshot.getString("username");
                String email = documentSnapshot.getString("email");
                String sex = documentSnapshot.getString("sex");
                String dob = documentSnapshot.getString("dob");

                // Set Data
                userFullName.setText(fullName);
                userUsername.setText(username);
                userEmail.setText(email);
                userSex.setText(sex);
                userDob.setText(dob);

                // Calculate and Set Age
                int age = calculateAge(dob);
                userAge.setText(age + " years old");
            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
    }

    private int calculateAge(String dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dobDate = sdf.parse(dob);
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dobDate);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
