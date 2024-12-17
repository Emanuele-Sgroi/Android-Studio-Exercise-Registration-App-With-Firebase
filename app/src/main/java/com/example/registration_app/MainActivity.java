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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {
    // UI Elements
    private Button signUpButton;
    private EditText inputPassword, inputUsername;
    private TextView usernameLimit;

    // States for tracking
    private boolean isPasswordVisible = false; // Toggle state for password
    private final int MAX_CHAR_LIMIT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        signUpButton = findViewById(R.id.signup_btn);
        inputPassword = findViewById(R.id.password_input);
        inputUsername = findViewById(R.id.username_input);
        usernameLimit = findViewById(R.id.username_limit);

        // TextWatchers
        setupCharacterCounter(inputUsername, usernameLimit, MAX_CHAR_LIMIT);

        // Toggle for Password
        togglePasswordVisibility(inputPassword);

        // Listener
        signUpButton.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

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
