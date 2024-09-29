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
import com.in_sync.models.Scenario;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.O)
public class ScenarioAdapter extends  RecyclerView.Adapter< com.in_sync.adapters.ScenarioAdapter.ScenarioViewHolder>{



        private List<Scenario> scenarios;
        private Context context;
        private com.in_sync.adapters.ScenarioAdapter.OnItemClickScenarioListener onItemClickScenarioListener;
        private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        public ScenarioAdapter(Context context, List<Scenario> scenarios, com.in_sync.adapters.ScenarioAdapter.OnItemClickScenarioListener onItemClickScenarioListener) {
            this.context = context;
            this.scenarios = scenarios;
            this.onItemClickScenarioListener = onItemClickScenarioListener;
        }

        @NonNull
        @Override
        public ScenarioAdapter.ScenarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_scenario_layout, parent, false);
            return new com.in_sync.adapters.ScenarioAdapter.ScenarioViewHolder(view);
        }

        public Scenario getItem(int position) {
            if (scenarios == null || scenarios.size() == 0) {
                return null;
            }
            return scenarios.get(position);
        }


        @Override
        public void onBindViewHolder(@NonNull ScenarioAdapter.ScenarioViewHolder holder, int position) {
            Scenario scenario = scenarios.get(position);

            holder.scenarioNameTextView.setText(scenario.getTitle());
            holder.projectNameTextView.setText(scenario.getProjectName());
            holder.scenarioDesciptionTextView.setText(scenario.getDescription());


            holder.dateCreatedTextView.setText(scenario.getCreatedAt().format(dateFormatter));

            if (scenario.getIsFavorites()) {
                holder.isFavoriteImageView.setImageResource(R.drawable.favorite);
            } else {
                holder.isFavoriteImageView.setImageResource(R.drawable.no_favorite);
            }
            String updateTime = calculateTimeDifference(LocalDateTime.now(), scenario.getUpdatedAt());
            holder.updatedTimeTextView.setText(updateTime);

        }

        public String calculateTimeDifference(LocalDateTime now, LocalDateTime specificDateTime) {
            // Tính khoảng thời gian giữa hai thời điểm
            if(specificDateTime == null){
                return "Not updated";
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
            return scenarios.size();
        }

        public class ScenarioViewHolder extends RecyclerView.ViewHolder {
            TextView scenarioNameTextView;
            TextView projectNameTextView;
            TextView dateCreatedTextView;
            TextView scenarioDesciptionTextView;
            TextView updatedTimeTextView;
            ImageView isFavoriteImageView;;
            Button runScenario, deleteScenario;

            public ScenarioViewHolder(@NonNull View itemView) {
                super(itemView);
                scenarioNameTextView = itemView.findViewById(R.id.scenario_name_tv);
                projectNameTextView = itemView.findViewById(R.id.project_name_tv);
                scenarioDesciptionTextView = itemView.findViewById(R.id.scenario_description_tv);
                dateCreatedTextView = itemView.findViewById(R.id.scenario_date_created_tv);
                isFavoriteImageView = itemView.findViewById(R.id.is_favorite_scenario_iv);
                runScenario = itemView.findViewById(R.id.run_scenario_btn);
                deleteScenario = itemView.findViewById(R.id.delete_scenario_btn);
                updatedTimeTextView = itemView.findViewById(R.id.scenario_updated_time_tv);


                runScenario.setOnClickListener(v -> onItemClickScenarioListener.onRunClick(v, getAdapterPosition()));
                deleteScenario.setOnClickListener(v -> onItemClickScenarioListener.onDeleteClick(v, getAdapterPosition()));
            }
        }

        public interface OnItemClickScenarioListener {
            void onRunClick(View view, int position);

            void onDeleteClick(View view, int position);
        }
}
