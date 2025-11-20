package com.example.cgpaconverter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<Conversion> conversions;

    public HistoryAdapter(List<Conversion> conversions) {
        this.conversions = conversions;
    }

    public List<Conversion> getConversions() {
        return conversions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCgpa, tvPercentage, tvDate;

        public ViewHolder(View view) {
            super(view);
            tvCgpa = view.findViewById(R.id.tvCgpa);
            tvPercentage = view.findViewById(R.id.tvPercentage);
            tvDate = view.findViewById(R.id.tvDate);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Conversion item = conversions.get(position);

        holder.tvCgpa.setText("CGPA: " + String.format("%.2f", item.cgpa));
        holder.tvPercentage.setText("Percentage: " + String.format("%.2f", item.percentage) + "%");
        holder.tvDate.setText(item.date);
    }

    @Override
    public int getItemCount() {
        return conversions.size();
    }

    public void addConversion(Conversion conversion) {
        conversions.add(0, conversion);
        notifyItemInserted(0);
    }
}

