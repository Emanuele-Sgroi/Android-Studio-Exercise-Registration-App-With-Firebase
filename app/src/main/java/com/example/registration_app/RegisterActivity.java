package com.example.registration_app;


import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    // UI Elements
    private Button goBackButton, buttonDob, signUpButton, resetButton;
    private EditText inputPassword, inputRepeatPassword, inputFullName, inputUsername, inputEmail;
    private TextView fullnameLimit, usernameLimit, fullnameError, usernameError, passwordError, repeatPasswordError, sexError, dobError, emailError;
    private RadioGroup sexGroup;

    // Database
   private FirebaseFirestore db;

    // States for tracking
    private boolean isPasswordVisible = false; // Toggle state for password
    private boolean isRepeatPasswordVisible = false; // Toggle state for repeat password
    private final int MAX_CHAR_LIMIT = 20;
    private final int MAX_CHAR_LIMIT_BIG = 50;
    private String selectedDob = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find views
        goBackButton = findViewById(R.id.button_go_back);
        buttonDob = findViewById(R.id.button_dob);
        inputPassword = findViewById(R.id.input_password);
        inputRepeatPassword = findViewById(R.id.input_repeat_password);
        inputFullName = findViewById(R.id.input_fullname);
        fullnameLimit = findViewById(R.id.fullname_limit);
        inputUsername = findViewById(R.id.input_username);
        usernameLimit = findViewById(R.id.username_limit);
        sexGroup = findViewById(R.id.sex_radio_group);
        fullnameError = findViewById(R.id.fullname_error);
        usernameError = findViewById(R.id.username_error);
        sexError = findViewById(R.id.sex_error);
        signUpButton = findViewById(R.id.button_signup);
        passwordError = findViewById(R.id.password_error);
        repeatPasswordError = findViewById(R.id.repeat_password_error);
        dobError = findViewById(R.id.dob_error);
        inputEmail =findViewById(R.id.input_email);
        emailError = findViewById(R.id.email_error);
        resetButton = findViewById(R.id.button_reset);

        // Database
        db = FirebaseFirestore.getInstance();

        // Toggle for Password
        togglePasswordVisibility(inputPassword);

        // Toggle for Repeat Password
        togglePasswordVisibility(inputRepeatPassword);

        // TextWatchers
        setupCharacterCounter(inputFullName, fullnameLimit, MAX_CHAR_LIMIT_BIG);
        setupCharacterCounter(inputUsername, usernameLimit, MAX_CHAR_LIMIT);

        //Listeners
        goBackButton.setOnClickListener(v -> {
            // Navigate back to Login Activity
            finish();
        });

        buttonDob.setOnClickListener(v -> showDatePicker());

        signUpButton.setOnClickListener(v -> {
            if (validateInputs()) {
                // Extract the validated input fields
                String fullName = inputFullName.getText().toString().trim();
                String username = inputUsername.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String dob = buttonDob.getText().toString().trim();
                String sex = ((RadioButton) findViewById(sexGroup.getCheckedRadioButtonId())).getText().toString();

                // Register the user in Firestore
                registerUser(fullName, username, email, password, dob, sex);
            }
        });
        resetButton.setOnClickListener(v -> {
            resetForm();
        });
    }

    // Validation Method
    private boolean validateInputs() {
        boolean isValid = true;
        String fullName = inputFullName.getText().toString().trim();
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String repeatPassword = inputRepeatPassword.getText().toString().trim();
        String dob = buttonDob.getText().toString().trim();
        StringBuilder passwordErrorMessage = new StringBuilder();

        // Validate Full Name
        if (fullName.isEmpty()) {
            fullnameError.setText("Full Name is required");
            fullnameError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!fullName.contains(" ") || fullName.split("\\s+").length < 2) {
            fullnameError.setText("Please enter first and last name");
            fullnameError.setVisibility(View.VISIBLE);
            isValid = false;
        }  else {
            fullnameError.setVisibility(View.GONE);
        }

        // Validate Username
        if (username.isEmpty()) {
            usernameError.setText("Username is required");
            usernameError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            usernameError.setVisibility(View.GONE);
        }

        // Validate Sex
        if (sexGroup.getCheckedRadioButtonId() == -1) {
            sexError.setText("Sex is required");
            sexError.setVisibility(View.VISIBLE);
            isValid = false;
        }  else {
           sexError.setVisibility(View.GONE);
        }

        // Validate Dob
        if (selectedDob.isEmpty() || dob.equals("Select your date of birth")) {
            dobError.setText("Date of Birth is required");
            dobError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            // Check if user is 18+
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

                if (age < 18) {
                    dobError.setText("You must be at least 18 years old");
                    dobError.setVisibility(View.VISIBLE);
                    isValid = false;
                } else {
                    dobError.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                dobError.setText("Invalid date format");
                dobError.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        // Validate Email
        if (email.isEmpty()) {
            emailError.setText("Email is required");
            emailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.setText("Invalid email address");
            emailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            emailError.setVisibility(View.GONE);
        }

        // Validate password
        if (password.isEmpty()) {
            passwordError.setText("Password is required");
            passwordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            // Min 6 characters
            if (password.length() < 6) {
                passwordErrorMessage.append("- Min 6 characters\n");
            }
            // at least one uppercase letters
            if (!password.matches(".*[A-Z].*")) {
                passwordErrorMessage.append("- An uppercase letter\n");
            }
            // at least one number
            if (!password.matches(".*\\d.*")) {
                passwordErrorMessage.append("- A number\n");
            }
            // at least one special character
            if (!password.matches(".*[!@#_$%^&+=].*")) {
                passwordErrorMessage.append("- A special character (e.g. ! @ # _)\n");
            }

            if (passwordErrorMessage.length() > 0) {
                passwordError.setText("Your password must contain:\n" + passwordErrorMessage);
                passwordError.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                passwordError.setVisibility(View.GONE);
            }
        }

        // Validate repeat password
        if (repeatPassword.isEmpty()) {
            repeatPasswordError.setText("Password is required");
            repeatPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if(!repeatPassword.matches(password)){
            repeatPasswordError.setText("Passwords don't match");
            repeatPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            repeatPasswordError.setVisibility(View.GONE);
        }

        return isValid;
    }

    private void registerUser(String fullName, String username, String email, String password, String dob, String sex) {
       // FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create user data
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("username", username);
        user.put("email", email);
        user.put("password", password);
        user.put("dob", dob);
        user.put("sex", sex);

        // Check if email or username already exists
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Email already registered
                        emailError.setText("Email already registered");
                        emailError.setVisibility(View.VISIBLE);
                    } else {
                        // Proceed to check username
                        db.collection("users")
                                .whereEqualTo("username", username)
                                .get()
                                .addOnSuccessListener(usernameQuery -> {
                                    if (!usernameQuery.isEmpty()) {
                                        // Username already exists
                                        usernameError.setText("Username already taken");
                                        usernameError.setVisibility(View.VISIBLE);
                                    } else {
                                        // Add user to Firestore
                                        db.collection("users")
                                                .add(user)
                                                .addOnSuccessListener(docRef -> {
                                                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                    resetForm();
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error checking username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void resetForm() {
        // Reset full name input and error
        inputFullName.setText("");
        fullnameError.setVisibility(View.GONE);

        // Reset username input and error
        inputUsername.setText("");
        usernameError.setVisibility(View.GONE);

        // Reset email input and error
        inputEmail.setText("");
        emailError.setVisibility(View.GONE);

        // Reset password inputs and errors
        inputPassword.setText("");
        passwordError.setVisibility(View.GONE);
        inputRepeatPassword.setText("");
        repeatPasswordError.setVisibility(View.GONE);

        // Reset date of birth button and error
        buttonDob.setText("Select your date of birth");
        dobError.setVisibility(View.GONE);

        // Reset radio group selection and error
        sexGroup.clearCheck();
        sexError.setVisibility(View.GONE);

        // Reset character limit counters for full name and username
        fullnameLimit.setText("0/20");
        usernameLimit.setText("0/20");
    }


    // Method to set up character counter logic
    private void setupCharacterCounter(EditText editText, TextView limitTextView, int maxLimit) {
        editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(maxLimit) });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();
                limitTextView.setText(currentLength + "/" + maxLimit);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    private void showDatePicker() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDob = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    buttonDob.setText(selectedDob);
                    dobError.setVisibility(View.GONE);
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
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
