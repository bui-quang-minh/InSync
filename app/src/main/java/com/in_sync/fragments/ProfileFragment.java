package com.in_sync.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.in_sync.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    private TextView textView;
    private Context context;
    private TextView tvFullName;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvUserPhone;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = setInformation(inflater, container);
        return view;
    }

    public View setInformation(LayoutInflater inflater, ViewGroup container) {
        // Inflate the layout for this

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userInfo = sharedPreferences.getString("UserInfo", "User");

        // User data
        tvFullName = view.findViewById(R.id.full_name);
        tvUserName  = view.findViewById(R.id.user_name);
        tvUserEmail = view.findViewById(R.id.user_email);
        tvUserPhone = view.findViewById(R.id.user_phone);

        try {
            // Parse the JSON string
            JSONArray userArray = new JSONArray(userInfo);
            if (userArray.length() > 0) {
                JSONObject user = userArray.getJSONObject(0);

                // Extract full name
                String firstName = user.getString("first_name");
                String lastName = user.getString("last_name");
                String fullName = firstName + " " + lastName;

                // Extract user name
                String userName = user.optString("username", "N/A");

                // Extract email address
                JSONArray emailAddresses = user.getJSONArray("email_addresses");
                String emailAddress = emailAddresses.getJSONObject(0).getString("email_address");

                // Extract avatar URL if has_image is true
                String avatarUrl = null;
                if (user.getBoolean("has_image")) {
                    avatarUrl = user.getString("image_url");
                }

                // Set the extracted values to TextViews
                tvFullName.setText(fullName);
                tvUserName.setText(userName);
                tvUserEmail.setText(emailAddress);
                ImageView avatarImageView = view.findViewById(R.id.avatar);
                Glide.with(context).load(avatarUrl).into(avatarImageView);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}