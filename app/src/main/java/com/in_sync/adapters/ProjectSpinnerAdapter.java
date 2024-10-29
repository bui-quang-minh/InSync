package com.in_sync.adapters;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.in_sync.models.Project;

import java.util.ArrayList;

public class ProjectSpinnerAdapter extends ArrayAdapter<Project> {

    public ProjectSpinnerAdapter(Context context, ArrayList<Project> projects) {
        super(context, android.R.layout.simple_spinner_item, projects);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the current project
        Project project = getItem(position);

        // Inflate a simple layout for the spinner item
        TextView textView = (TextView) super.getView(position, convertView, parent);
        if (project != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textView.setText(project.getProjectName()); // Display the project name
            }
        }
        return textView;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
