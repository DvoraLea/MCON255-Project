package com.example.planner.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planner.R;
import com.example.planner.models.Subtask;

import java.util.List;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskAdapter.SubtaskVH> {

    public interface Listener {
        void onToggle(Subtask subtask);
        void onDelete(Subtask subtask);
    }

    private final List<Subtask> items;
    private final Listener listener;

    public SubtaskAdapter(List<Subtask> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubtaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subtask_item, parent, false);
        return new SubtaskVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtaskVH h, int position) {
        Subtask s = items.get(position);
        h.title.setText(s.getTitle());
        h.check.setOnCheckedChangeListener(null);
        h.check.setChecked(s.isDone());

        // strike-through when done
        h.title.setPaintFlags(s.isDone()
                ? (h.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG)
                : (h.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)));

        h.check.setOnCheckedChangeListener((btn, isChecked) -> {
            if (listener != null) listener.onToggle(s);
        });

        h.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onDelete(s);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class SubtaskVH extends RecyclerView.ViewHolder {
        CheckBox check;
        TextView title;

        SubtaskVH(@NonNull View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.subtaskCheck);
            title = itemView.findViewById(R.id.subtaskTitle);
        }
    }
}
