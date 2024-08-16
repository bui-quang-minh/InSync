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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.O)
public class LogSessionAdapter extends RecyclerView.Adapter<LogSessionAdapter.LogSessionViewHolder> {

    private List<LogSession> logSessions;
    private Context context;
    private OnItemClickLogSessionListener onItemClickLogSessionListener;
    private static final  DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public LogSessionAdapter(Context context, List<LogSession> logSessions, OnItemClickLogSessionListener onItemClickLogSessionListener) {
        this.context = context;
        this.logSessions = logSessions;
        this.onItemClickLogSessionListener = onItemClickLogSessionListener;
    }

    @NonNull
    @Override
    public LogSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_log_session_recyleview, parent, false);
        return new LogSessionViewHolder(view);
    }

    public LogSession getItem(int position) {
        if (logSessions == null || logSessions.size() == 0) {
            return null;
        }
        return logSessions.get(position);
    }


    @Override
    public void onBindViewHolder(@NonNull LogSessionViewHolder holder, int position) {
        LogSession logSession = logSessions.get(position);

        holder.sessionNameTextView.setText(logSession.getSession_name());
        holder.deviceNameTextView.setText(logSession.getDevice_name());
        if(logSession.isNeed_resolve()){
            holder.need_resolve_img.setImageResource(R.drawable.warning);
        }else{
            holder.need_resolve_img.setImageResource(R.drawable.mark);
        }
        try{
            LocalDateTime dateTime = LocalDateTime.parse(logSession.getDate_created(), dateTimeFormatter);
            holder.dateCreatedTextView.setText(dateTime.format(dateFormatter));

        }catch (Exception e){
            holder.dateCreatedTextView.setText("No Date Created");
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return logSessions.size();
    }

    public class LogSessionViewHolder extends RecyclerView.ViewHolder {
        TextView sessionNameTextView;
        TextView deviceNameTextView;
        TextView dateCreatedTextView;
        ImageView need_resolve_img;
        Button viewLogs, deleteSession;

        public LogSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionNameTextView = itemView.findViewById(R.id.session_name_tv);
            deviceNameTextView = itemView.findViewById(R.id.device_name_tv);
            dateCreatedTextView = itemView.findViewById(R.id.date_created_tv);
            viewLogs = itemView.findViewById(R.id.view_log_btn);
            deleteSession = itemView.findViewById(R.id.delete_session_btn);
            need_resolve_img = itemView.findViewById(R.id.need_resolve_img);

            viewLogs.setOnClickListener(v -> onItemClickLogSessionListener.onViewClick(v, getAdapterPosition()));
            deleteSession.setOnClickListener(v-> onItemClickLogSessionListener.onDeleteClick(v, getAdapterPosition()));
        }
    }
    public interface OnItemClickLogSessionListener {
        void onViewClick(View view, int position);

        void onDeleteClick(View view, int position);
    }

}
