package com.example.planner.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planner.R;
import com.example.planner.models.Planner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.PlannerViewHolder> {

    private List<Planner> plannerList;

    public PlannerAdapter(List<Planner> plannerList) {

        this.plannerList = plannerList != null ? plannerList : new ArrayList<>();
    }

    @NonNull
    @Override
    public PlannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new PlannerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannerViewHolder holder, int position) {
        Planner planner = plannerList.get(position);
        holder.titleTextView.setText(planner.getTitle());
        holder.descriptionTextView.setText(planner.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.dueDateTextView.setText(sdf.format(planner.getDueDate()));
    }

    @Override
    public int getItemCount() {
        return plannerList.size();
    }

    public static class PlannerViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dueDateTextView;

        public PlannerViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textTitle);
            descriptionTextView = itemView.findViewById(R.id.textDescription);
            dueDateTextView = itemView.findViewById(R.id.textDueDate);
        }
    }
}
