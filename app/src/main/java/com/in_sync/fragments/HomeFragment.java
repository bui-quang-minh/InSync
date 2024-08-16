package com.in_sync.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;


import com.in_sync.MainActivity;
import com.in_sync.R;
import com.in_sync.WalkthoughNavigationActivity;

public class HomeFragment extends Fragment {
    private TextView textView;
    private Context context;

    private Button getStartedButton;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context= context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        onAppStart(view);
        eventHandler();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
        setHasOptionsMenu(true); // To let the fragment handle the menu
    }
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home_menu_info) {
            // Handle the action here
            showInformationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInformationDialog() {
        // Example: Show a dialog or perform an action
        new AlertDialog.Builder(requireContext())
                .setTitle("Information")
                .setMessage("Build version: 1.0.0\nCopyright © 2024 InSync.\nAll rights reserved.")
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    private void onAppStart(View v) {
        getStartedButton = v.findViewById(R.id.getStartedButton);

    }
    private void eventHandler() {
        getStartedButton.setOnClickListener(this::walkthrough);
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press or do nothing to prevent back navigation
            }
        });
    }
    private void walkthrough(View view) {
        Intent intent = new Intent(context, WalkthoughNavigationActivity.class);
        startActivity(intent);
    }
}