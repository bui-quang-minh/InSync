package com.in_sync.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.in_sync.R;
import com.in_sync.interfaces.VolleyArrayRequestCallback;
import com.in_sync.interfaces.VolleyObjectRequestCallback;
import com.in_sync.request.GetAllUserRequest;
import com.in_sync.request.GetUserListRequest;
import com.in_sync.request.ValidatePasswordRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//Created By: Bui Quang Minh
//Created Date: 07-08-2024
//This activity is used to authenticate the user
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private Button loginButton;
    private GoogleSignInClient mGoogleSignInClient;
    private Button loginWithGoogleButton;
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
    }

    private void onAppStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginButton              = findViewById(R.id.loginButton);
        usernameLayout           = findViewById(R.id.txtUsername);
        passwordLayout           = findViewById(R.id.txtPassword);
        usernameEditText         = findViewById(R.id.inputUsername);
        passwordEditText         = findViewById(R.id.inputPassword);
        loginWithGoogleButton    = findViewById(R.id.loginWithGoogleButton);
    }

    private void eventHandling() {
        loginWithGoogleButton   .setOnClickListener(this::loginWithGoogleButtonClicked);
        loginButton             .setOnClickListener(this::loginButtonClicked);
        usernameEditText        .setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Handle the Enter key press here
                    passwordEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        passwordEditText        .setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Handle the Enter key press here
                    loginButtonClicked(v);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hide the keyboard
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
        passwordEditText        .setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordLayout          .setEndIconOnClickListener(new View.OnClickListener() {
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

    private void loginWithGoogleButtonClicked(View view) {
        Toast.makeText(this, "Logging in with Google...", Toast.LENGTH_SHORT).show();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Now, start the sign-in intent
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if(account != null){
                GetAllUserRequest.GetUsersList(this, new VolleyArrayRequestCallback() {
                    @Override
                    public void onSuccess(JSONArray result){
                        List<JSONObject> list = new ArrayList<>();
                        try{
                            for (int i = 0; i < result.length(); i++) {
                                if(result.getJSONObject(i).toString().contains(account.getEmail())){
                                    list.add(result.getJSONObject(i));
                                    JSONArray jsonArray = new JSONArray();
                                    for (JSONObject jsonObject : list) {
                                        jsonArray.put(jsonObject);
                                    }
                                    Log.e("onSuccess: ", jsonArray.toString());
                                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("UserInfo", jsonArray.toString());
                                    editor.apply();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                            }
                            if (list.isEmpty())
                                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loginButtonClicked(View view) {
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(false);
        loginWithGoogleButton.setEnabled(false);
        usernameLayout.setError(null);
        passwordLayout.setError(null);
        //catch empty
        if (usernameEditText.getText().toString().isEmpty()&&passwordEditText.getText().toString().isEmpty()) {
            usernameLayout.setError("Username is required");
            passwordLayout.setError("Password is required");
            loginButton.setEnabled(true);
            loginWithGoogleButton.setEnabled(true);
            return;
        }
        if (usernameEditText.getText().toString().isEmpty()) {
            usernameLayout.setError("Username is required");
            loginButton.setEnabled(true);
            loginWithGoogleButton.setEnabled(true);
            return;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordLayout.setError("Password is required");
            loginButton.setEnabled(true);
            loginWithGoogleButton.setEnabled(true);
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
                    loginButton.setEnabled(true);
                    loginWithGoogleButton.setEnabled(true);
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
                                loginButton.setEnabled(true);
                                loginWithGoogleButton.setEnabled(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        passwordLayout.setError("Invalid password");
                        loginButton.setEnabled(true);
                        loginWithGoogleButton.setEnabled(true);
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