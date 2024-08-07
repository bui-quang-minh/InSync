package com.in_sync;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.in_sync.request.GetUserListRequest;

//Created By: Bui Quang Minh
//Created Date: 07-08-2024
//This activity is used to authenticate the user
public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;

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
       usernameLayout = findViewById(R.id.txtUsername);
       passwordLayout = findViewById(R.id.txtPassword);
       usernameEditText = findViewById(R.id.inputUsername);
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
        if (GetUserListRequest.GetLoginParameter(usernameEditText.getText().toString(), this, usernameLayout, passwordLayout)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
        }
    }
}