package com.in_sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.in_sync.interfaces.VolleyArrayRequestCallback;
import com.in_sync.interfaces.VolleyObjectRequestCallback;
import com.in_sync.request.GetUserListRequest;
import com.in_sync.request.ValidatePasswordRequest;

import org.json.JSONArray;
import org.json.JSONObject;

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
        usernameLayout.setError(null);
        passwordLayout.setError(null);
        //catch empty
        if (usernameEditText.getText().toString().isEmpty()&&passwordEditText.getText().toString().isEmpty()) {
            usernameLayout.setError("Username is required");
            passwordLayout.setError("Password is required");
            return;
        }
        if (usernameEditText.getText().toString().isEmpty()) {
            usernameLayout.setError("Username is required");
            return;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordLayout.setError("Password is required");
            return;
        }
        GetUserListRequest.GetLoginParameter(usernameEditText.getText().toString(), this, new VolleyArrayRequestCallback() {
            @Override
            public void onSuccess(JSONArray result) {
                if (result.length() > 0) {
                    try{
                        JSONObject user = result.getJSONObject(0);
                        String id = user.getString("id");
                        validatePassword(id,result, LoginActivity.this);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    usernameLayout.setError("Invalid username");
                }
            }

            private void validatePassword(String userId,JSONArray data, Context context) {
                String password = passwordEditText.getText().toString();
                Log.e("validatePassword: ", password );
                ValidatePasswordRequest.ValidatePassword(userId, password, data, context, new VolleyObjectRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject result, JSONArray data) {
                        try {
                            if (result.getBoolean("verified")) {
                                SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("UserInfo", data.toString());
                                editor.apply();
                                Toast.makeText(LoginActivity.this, "Login successful ", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                passwordLayout.setError("Invalid password");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        passwordLayout.setError("Invalid password");
                    }
                });
            }

            @Override
            public void onError(String error) {
                usernameLayout.setError("Invalid username");
            }
        });
    }
}