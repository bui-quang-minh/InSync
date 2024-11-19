package com.in_sync.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.in_sync.R;
import com.in_sync.models.LogSession;
import com.in_sync.models.Project;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private List<Project> projects;
    private Context context;
    private ProjectAdapter.OnItemClickProjectListener onItemClickProjectListener;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public ProjectAdapter(Context context, List<Project> projects, ProjectAdapter.OnItemClickProjectListener onItemClickProjectListener) {
        this.context = context;
        this.projects = projects;
        this.onItemClickProjectListener = onItemClickProjectListener;
    }

    @NonNull
    @Override
    public ProjectAdapter.ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_projects_layout, parent, false);
        return new ProjectAdapter.ProjectViewHolder(view);
    }

    public Project getItem(int position) {
        if (projects == null || projects.size() == 0) {
            return null;
        }
        return projects.get(position);
    }


    @Override
    public void onBindViewHolder(@NonNull ProjectAdapter.ProjectViewHolder holder, int position) {
        Project project = projects.get(position);

        holder.projectNameTextView.setText(project.getProjectName());
        holder.deviceNameTextView.setText("Iphone 19 promax");
        holder.projectDesciptionTextView.setText(project.getDescription());
        if (project.getDateUpdated() == null) {
            holder.dateUpdatedTextView.setText("Project not updated");
        } else {
            holder.dateUpdatedTextView.setText(project.getDateUpdated().format(dateFormatter));
        }
        if (project.getIsPublish()) {
            holder.projectStatus.setImageResource(R.drawable.publishing);
        } else {
            holder.projectStatus.setImageResource(R.drawable.privacy);
        }
        String updateTime = calculateTimeDifference(LocalDateTime.now(), project.getDateUpdated());
        holder.updatedTimeTextView.setText(updateTime);

    }

    public String calculateTimeDifference(LocalDateTime now, LocalDateTime specificDateTime) {
        // Tính khoảng thời gian giữa hai thời điểm
        if (specificDateTime == null) {
            return "not updated";
        }
        Duration duration = Duration.between(specificDateTime, now);
        long second = duration.getSeconds();
        if (second < 60) {
            return second + "seconds" + "ago";
        }
        long minutes = second / 60;

        if (minutes < 60) {
            return minutes + "minutes " + "ago";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "hours " + "ago";
        }
        long days = hours / 24;
        if (days < 30) {
            return days + "days " + "ago";
        }
        long months = days / 30;
        if (months < 12) {
            return months + "months " + "ago";
        }
        long years = months / 12;
        if (years < 12) {
            return years + "years " + "ago";
        }


        return "";
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView projectNameTextView;
        TextView deviceNameTextView;
        TextView dateUpdatedTextView;
        TextView projectDesciptionTextView;
        TextView updatedTimeTextView;
        ImageView projectStatus;
        Button viewScenario,updateProject, deleteProject;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            projectDesciptionTextView = itemView.findViewById(R.id.project_description_tv);
            projectNameTextView = itemView.findViewById(R.id.project_name_tv);
            deviceNameTextView = itemView.findViewById(R.id.device_name_tv);
            dateUpdatedTextView = itemView.findViewById(R.id.date_updated_tv);
            viewScenario = itemView.findViewById(R.id.view_scenario_btn);
            deleteProject = itemView.findViewById(R.id.delete_project_btn);
            updateProject = itemView.findViewById(R.id.update_project_btn);
            projectStatus = itemView.findViewById(R.id.status_project);
            updatedTimeTextView = itemView.findViewById(R.id.updated_time_tv);
            viewScenario.setOnClickListener(v -> onItemClickProjectListener.onViewClick(v, getAdapterPosition()));
            deleteProject.setOnClickListener(v -> onItemClickProjectListener.onDeleteClick(v, getAdapterPosition()));
            updateProject.setOnClickListener(v -> onItemClickProjectListener.onUpdateClick(v, getAdapterPosition()));
        }
    }

    public interface OnItemClickProjectListener {
        void onViewClick(View view, int position);

        void onDeleteClick(View view, int position);
        void onUpdateClick(View view, int position);
    }

}
