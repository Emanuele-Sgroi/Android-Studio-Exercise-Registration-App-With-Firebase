package com.example.registration_app;


import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.widget.TextView;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    // UI Elements
    private Button goBackButton, buttonDob;
    private EditText inputPassword, inputRepeatPassword, inputFullName, inputUsername;
    private TextView fullnameLimit, usernameLimit;

    // States for tracking
    private boolean isPasswordVisible = false; // Toggle state for password
    private boolean isRepeatPasswordVisible = false; // Toggle state for repeat password
    private final int MAX_CHAR_LIMIT = 20;
    private final int MAX_CHAR_LIMIT_BIG = 50;


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
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    buttonDob.setText(selectedDate);
                },
                year, month, day
        );

        // Show the DatePickerDialog
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
