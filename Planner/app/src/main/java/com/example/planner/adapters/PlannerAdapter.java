package com.example.planner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
    private OnEditClickListener editClickListener;

    // Interface for edit click callback
    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public PlannerAdapter(List<Planner> plannerList, OnEditClickListener editClickListener) {
        this.plannerList = plannerList != null ? plannerList : new ArrayList<>();
        this.editClickListener = editClickListener;

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

        // Bind checkbox state
        holder.checkBoxCompleted.setOnCheckedChangeListener(null); // reset listener before reuse
        holder.checkBoxCompleted.setChecked(planner.isCompleted());

        // Checkbox listener to remove completed task
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Update model
                planner.setCompleted(true);

                // Remove the item from the list
                plannerList.remove(position);

                // Notify adapter about item removed
                notifyItemRemoved(position);

                // Optional: notify range changed to fix positions
                notifyItemRangeChanged(position, plannerList.size());
            }
        });

        // Edit button click listener
        holder.buttonEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return plannerList.size();
    }

    public static class PlannerViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dueDateTextView;
        CheckBox checkBoxCompleted;
        ImageButton buttonEdit;

        public PlannerViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textTitle);
            descriptionTextView = itemView.findViewById(R.id.textDescription);
            dueDateTextView = itemView.findViewById(R.id.textDueDate);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);

        }
    }
}