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
import java.util.List;
import java.util.Locale;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.PlannerViewHolder> {

    // the pencil (edit)
    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    // open details
    public interface OnItemClickListener {
        void onItemClick(Planner planner);
    }

    private final List<Planner> plannerList;
    private final OnEditClickListener editClickListener;
    private final OnItemClickListener itemClickListener;

    public PlannerAdapter(List<Planner> plannerList,
                          OnEditClickListener editClickListener,
                          OnItemClickListener itemClickListener) {
        this.plannerList = plannerList;
        this.editClickListener = editClickListener;
        this.itemClickListener = itemClickListener;
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
        Planner task = plannerList.get(position);

        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        holder.dueDateTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(task.getDueDate()));
        holder.checkBoxCompleted.setChecked(task.isCompleted());

        holder.buttonEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(plannerList.get(position));
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

