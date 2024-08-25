package com.in_sync.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;

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
    private TextView tvDeviceName;

    private DrawerLayout drawerLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = setInformation(inflater, container);

        drawerLayout = view.findViewById(R.id.drawer_layout);
        setupToolbar(view);

        return view;
    }

    public View setInformation(LayoutInflater inflater, ViewGroup container) {
        // Inflate the layout for this

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String userInfo = sharedPreferences.getString("UserInfo", "User");

//        TextView test = view.findViewById(R.id.test_info);
//        test.setText(userInfo);
        // User data
        tvFullName = view.findViewById(R.id.full_name);
        tvUserName  = view.findViewById(R.id.user_name);
        tvUserEmail = view.findViewById(R.id.user_email);
        tvUserPhone = view.findViewById(R.id.user_phone);
        tvDeviceName = view.findViewById(R.id.device_info);

        try {
            // Parse the JSON string
            JSONArray userArray = new JSONArray(userInfo);
            if (userArray.length() > 0) {
                JSONObject user = userArray.getJSONObject(0);

                // Extract full name
                String firstName = user.getString("first_name");
                if (firstName.equals("null")) {
                    firstName = "";
                }
                String lastName = user.getString("last_name");
                if (lastName.equals("null")) {
                    lastName = "";
                }
                String fullName = "Your full name";
                if (!(firstName.isEmpty() && !lastName.isEmpty())) {
                    fullName = firstName + " " + lastName;
                }

                // Extract user name
                String userName = user.optString("username", "user_name");
                if (userName.equals("null")) {
                    userName = "user_name";
                }

                // Extract email address
                JSONArray emailAddresses = user.getJSONArray("email_addresses");
                String emailAddress = emailAddresses.getJSONObject(0).getString("email_address");

                // Extract avatar URL if has_image is true
                String avatarUrl = null;
                if (user.getBoolean("has_image")) {
                    avatarUrl = user.getString("image_url");
                }

                // Extract device name
                String deviceName = Build.MANUFACTURER + Build.MODEL;

                // Set the extracted values to TextViews
                tvFullName.setText(fullName);
                tvUserName.setText(userName);
                tvUserEmail.setText(emailAddress);
                tvUserPhone.setText("retrieved_phone_number");
                tvDeviceName.setText(deviceName);
                ImageView avatarImageView = view.findViewById(R.id.avatar);
                Glide.with(context)
                        .load(avatarUrl)
                        .error(R.drawable.profile_avatar_placeholder)
                        .placeholder(R.drawable.profile_avatar_placeholder)
                        .into(avatarImageView);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set onClick listener for device_info_section
        LinearLayout deviceInfoSection = view.findViewById(R.id.device_info_section);
        deviceInfoSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to DeviceInfoFragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.cv_fragment_device_info, new DeviceInfoFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_profile);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.settings_icon) {
                    openRightDrawer();
                    return true;
                }
                return false;
            }
        });
    }

    private void openRightDrawer() {
        if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }
}