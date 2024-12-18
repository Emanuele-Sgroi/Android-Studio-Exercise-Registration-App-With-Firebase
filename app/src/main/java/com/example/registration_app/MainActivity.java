package com.example.registration_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    // UI Elements
    private Button signUpButton, loginButton;
    private EditText inputPassword, inputUsernameEmail;
    private TextView  usernameEmailError, passwordError;

    // Database
    private FirebaseFirestore db;

    // States for tracking
    private boolean isPasswordVisible = false; // Toggle state for password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        signUpButton = findViewById(R.id.signup_btn);
        inputPassword = findViewById(R.id.password_input);
        inputUsernameEmail = findViewById(R.id.username_email_input);
        usernameEmailError = findViewById(R.id.username_email_error);
        passwordError = findViewById(R.id.password_error);
        loginButton = findViewById(R.id.login_btn);

        // Database
        db = FirebaseFirestore.getInstance();

        // Toggle for Password
        togglePasswordVisibility(inputPassword);

        // Listener
        signUpButton.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Listener
        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String usernameEmail = inputUsernameEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                loginUser(usernameEmail, password);
            }
        });

    }

    // Validation Method
    private boolean validateInputs() {
        boolean isValid = true;
        String usernameEmail = inputUsernameEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Validate Username
        if (usernameEmail.isEmpty()) {
            usernameEmailError.setText("Enter your username or email");
            usernameEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            usernameEmailError.setVisibility(View.GONE);
        }

        // Validate password
        if (password.isEmpty()) {
            passwordError.setText("Enter your password");
            passwordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            passwordError.setVisibility(View.GONE);
        }

        return isValid;
    }

    private void loginUser(String usernameEmail, String password) {
        db.collection("users")
                .whereEqualTo("username", usernameEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Match found in username
                        validatePassword(task.getResult().getDocuments().get(0), password);
                    } else {
                        // No match in username, check email
                        db.collection("users")
                                .whereEqualTo("email", usernameEmail)
                                .get()
                                .addOnCompleteListener(emailTask -> {
                                    if (emailTask.isSuccessful() && !emailTask.getResult().isEmpty()) {
                                        // Match found in email
                                        validatePassword(emailTask.getResult().getDocuments().get(0), password);
                                    } else {
                                        // No match found in either username or email
                                        usernameEmailError.setText("Username or Email not found");
                                        usernameEmailError.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                });
    }

    private void validatePassword(DocumentSnapshot userDoc, String password) {
        String storedPassword = userDoc.getString("password");

        if (storedPassword != null && storedPassword.equals(password)) {
            // Password matches, login successful
            String fullName = userDoc.getString("fullName");
            String userId = userDoc.getId();
            Toast.makeText(this, "Welcome " + fullName, Toast.LENGTH_SHORT).show();

            // Redirect to dashboard activity
            Intent intent = new Intent(this, DashboardActivity.class);
            // Pass user ID as intent
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        } else {
            // Password mismatch
            passwordError.setText("Incorrect password");
            passwordError.setVisibility(View.VISIBLE);
        }
    }


    private void togglePasswordVisibility(EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if the click was on the drawableRight
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width())) {
                    // Toggle visibility state
                    boolean isVisible = editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    if (isVisible) {
                        // Hide password
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pass_toggle_off, 0);
                    } else {
                        // Show password
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pass_toggle_on, 0);
                    }
                    // Move cursor to the end of the text
                    editText.setSelection(editText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }
}
