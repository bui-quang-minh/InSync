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
import com.in_sync.models.Log;
import com.in_sync.models.LogSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<Log> logs;
    private Context context;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private OnItemClickLogListener onItemClickLogListener;

    public LogAdapter(Context context, List<Log> logs, OnItemClickLogListener onItemClickLogListener) {
        this.context = context;
        this.logs = logs;
        this.onItemClickLogListener = onItemClickLogListener;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_logs_of_session, parent, false);
        return new LogViewHolder(view);
    }

    public Log getItem(int position) {
        if (logs == null || logs.size() == 0) {
            return null;
        }
        return logs.get(position);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Log log = logs.get(position);

        if(log == null) return;
        holder.descriptionLogTextView.setText(log.getDescription());
        if(log.getNote() == null || log.getNote().equals("")){
            holder.noteLogTextView.setText("No note");
        }else{
            holder.noteLogTextView.setText(log.getNote());
        }


        try{
            LocalDateTime dateTime = LocalDateTime.parse(log.getDate_created(), dateTimeFormatter);
            holder.dateCreatedTextView.setText(dateTime.format(dateFormatter));
        }catch (Exception e){
            holder.dateCreatedTextView.setText("No Date Created");
            e.printStackTrace();
        }

        if(log.isStatus()){
            holder.statusLog.setImageResource(R.drawable.mark);

        }else{
            holder.statusLog.setImageResource(R.drawable.warning);
        }
    }

    @Override
    public int getItemCount() {
        return (logs != null) ? logs.size() : 0;
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionLogTextView;
        TextView noteLogTextView;
        TextView dateCreatedTextView;
        ImageView statusLog;


        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionLogTextView = itemView.findViewById(R.id.description_log_tv);
            noteLogTextView = itemView.findViewById(R.id.note_log_tv);


            dateCreatedTextView = itemView.findViewById(R.id.date_created_log_tv);
            statusLog = itemView.findViewById(R.id.status_log);

        }
    }
    public interface OnItemClickLogListener {

    }

}