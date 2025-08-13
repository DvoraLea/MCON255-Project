package com.example.planner.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.planner.R;
import com.example.planner.adapters.PlannerAdapter;
import com.example.planner.databinding.ActivityMainBinding;
import com.example.planner.models.Planner;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements
        PlannerAdapter.OnEditClickListener,
        PlannerAdapter.OnItemClickListener {

    private ActivityMainBinding binding;
    private PlannerAdapter adapter;
    private List<Planner> plannerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.includeToolbar.toolbar);

        plannerList = new ArrayList<>();

        adapter = new PlannerAdapter(plannerList, this, this);

        binding.contentMain.plannerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.contentMain.plannerRecyclerView.setAdapter(adapter);

        // Floating Action Button to add task
        binding.fabAddTask.setOnClickListener(view -> {
            LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.dialog_add_task, null);

            EditText editTitle = dialogView.findViewById(R.id.editTextTitle);
            EditText editDescription = dialogView.findViewById(R.id.editTextDescription);
            EditText editDueDate = dialogView.findViewById(R.id.editTextDueDate);

            new AlertDialog.Builder(this)
                    .setTitle("Add New Task")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String title = editTitle.getText().toString().trim();
                        String description = editDescription.getText().toString().trim();
                        String dueDateStr = editDueDate.getText().toString().trim();

                        if (title.isEmpty() || dueDateStr.isEmpty()) {
                            Snackbar.make(binding.getRoot(), "Title and Date required", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date dueDate = sdf.parse(dueDateStr);

                            String id = UUID.randomUUID().toString();
                            Planner newTask = new Planner(id, title, description, dueDate, false);
                            plannerList.add(newTask);
                            adapter.notifyItemInserted(plannerList.size() - 1);
                        } catch (Exception e) {
                            Snackbar.make(binding.getRoot(), "Invalid date format", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Snackbar.make(binding.getRoot(), "Settings --", Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.about_app))
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(int position) {
        Planner taskToEdit = plannerList.get(position);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);

        EditText editTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editDescription = dialogView.findViewById(R.id.editTextDescription);
        EditText editDueDate = dialogView.findViewById(R.id.editTextDueDate);

        editTitle.setText(taskToEdit.getTitle());
        editDescription.setText(taskToEdit.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editDueDate.setText(sdf.format(taskToEdit.getDueDate()));

        new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newTitle = editTitle.getText().toString().trim();
                    String newDesc = editDescription.getText().toString().trim();
                    String newDueDateStr = editDueDate.getText().toString().trim();

                    if (newTitle.isEmpty() || newDueDateStr.isEmpty()) {
                        Snackbar.make(binding.getRoot(), "Title and Date required", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        Date newDueDate = sdf.parse(newDueDateStr);

                        taskToEdit.setTitle(newTitle);
                        taskToEdit.setDescription(newDesc);
                        taskToEdit.setDueDate(newDueDate);

                        adapter.notifyItemChanged(position);
                    } catch (Exception e) {
                        Snackbar.make(binding.getRoot(), "Invalid date format", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onItemClick(int position) {
        Planner clickedTask = plannerList.get(position);

        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra("task_title", clickedTask.getTitle());
        intent.putExtra("task_description", clickedTask.getDescription());
        intent.putExtra("task_due_date", clickedTask.getDueDate().getTime());

        startActivity(intent);
    }
}
