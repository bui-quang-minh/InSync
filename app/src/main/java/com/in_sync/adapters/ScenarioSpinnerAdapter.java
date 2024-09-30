package com.in_sync.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.in_sync.R;
import com.in_sync.models.Scenario;

import java.util.List;
@RequiresApi(api = Build.VERSION_CODES.O)
public class ScenarioSpinnerAdapter extends ArrayAdapter<Scenario> {
    private Context context;
    private List<Scenario> items;

    public ScenarioSpinnerAdapter(@NonNull Context context, @NonNull List<Scenario> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Lấy item hiện tại
        Scenario item = getItem(position);

        // Kiểm tra nếu view đang sử dụng lại, nếu không thì inflate layout
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_scenario_spinner, parent, false);
        }

        // Cập nhật TextView với dữ liệu của item
        TextView textViewItem = convertView.findViewById(R.id.scenario_name_tv);
        if (item != null && textViewItem != null) {
            textViewItem.setText(item.getTitle());
        }

        return convertView;
    }
    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }
    @Override
    public Scenario getItem(int position) {
        return items != null && position >= 0 && position < items.size() ? items.get(position) : null;
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Tạo view cho item trong dropdown menu
        return getView(position, convertView, parent);
    }
}
