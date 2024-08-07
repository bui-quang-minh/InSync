package com.in_sync;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
//Created By: Bui Quang Minh
//Created Date: 07-08-2024
//This activity is used to authenticate the user
public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    TextInputEditText passwordEditText;
    TextInputLayout passwordLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        onAppStart();
        eventHandling();
        // Ensure the password is hidden by default

    }

    private void onAppStart() {
       loginButton = findViewById(R.id.loginButton);
        passwordLayout = findViewById(R.id.txtPassword);
        passwordEditText = findViewById(R.id.inputPassword);
    }

    private void eventHandling() {
        loginButton.setOnClickListener(this::loginButtonClicked);
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    passwordEditText.setTransformationMethod(null);
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                // Move the cursor to the end of the text
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });
    }

    private void loginButtonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}