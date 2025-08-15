package com.example.planner.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements PlannerAdapter.OnEditClickListener, PlannerAdapter.OnItemClickListener {
//
    private static final int REQ_TASK_DETAILS = 1001;

    private ActivityMainBinding binding;
    private PlannerAdapter adapter;
    private ArrayList<Planner> plannerList;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("planner_list", new ArrayList<>(plannerList));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar
        setSupportActionBar(binding.includeToolbar.toolbar);

        //  adapter - call from savedInstanceState or otherwise create empty one
        if (savedInstanceState != null) {
            plannerList = (ArrayList<Planner>) savedInstanceState.getSerializable("planner_list");
        } else {
            plannerList = new ArrayList<>(); // or load from persistent storage
        }
        adapter = new PlannerAdapter(plannerList, this, this);

        binding.contentMain.plannerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.contentMain.plannerRecyclerView.setAdapter(adapter);

        //Add a new task dialog
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

                            // IMPORTANT: your Planner constructor takes 5 args: (id, title, desc, date, completed)
                            Planner newTask = new Planner(
                                    UUID.randomUUID().toString(),
                                    title,
                                    description,
                                    dueDate,
                                    false
                            );
                            plannerList.add(newTask);
                            adapter.notifyItemInserted(plannerList.size() - 1);
                        } catch (Exception e) {
                            Snackbar.make(binding.getRoot(), "Invalid date format (use yyyy-MM-dd)", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    // Toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Toolbar actions
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

    // Edit (pencil)
    @Override
    public void onEditClick(int position) {
        if (position < 0 || position >= plannerList.size()) return;

        Planner taskToEdit = plannerList.get(position);


        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText editTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editDescription = dialogView.findViewById(R.id.editTextDescription);
        EditText editDueDate = dialogView.findViewById(R.id.editTextDueDate);

        editTitle.setText(taskToEdit.getTitle());
        editDescription.setText(taskToEdit.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editDueDate.setText(taskToEdit.getDueDate() != null ? sdf.format(taskToEdit.getDueDate()) : "");

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
                        Snackbar.make(binding.getRoot(), "Invalid date format (use yyyy-MM-dd)", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // open details activity
    @Override
    public void onItemClick(Planner planner) {
        if (planner == null) return;

        Intent intent = new Intent(this, TaskDetailsActivity.class);
        intent.putExtra("extra_task", planner);
        intent.putExtra("task_id", planner.getId());

        //TODO
        startActivityForResult(intent, REQ_TASK_DETAILS);
    }

    //  updates back
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_TASK_DETAILS && resultCode == RESULT_OK && data != null) {


            Planner updated = (Planner) data.getSerializableExtra(TaskDetailsActivity.EXTRA_TASK);
            if (updated != null && updated.getId() != null) {
                for (int i = 0; i < plannerList.size(); i++) {
                    if (updated.getId().equals(plannerList.get(i).getId())) {
                        plannerList.set(i, updated);
                        adapter.notifyItemChanged(i);
                        return;
                    }
                }
            }


            String id = data.getStringExtra("task_id");
            String title = data.getStringExtra("updated_title");
            String desc = data.getStringExtra("updated_description");
            long dueDateMillis = data.getLongExtra("updated_due_date", -1);

            if (id != null) {
                for (int i = 0; i < plannerList.size(); i++) {
                    Planner p = plannerList.get(i);
                    if (id.equals(p.getId())) {
                        if (title != null) p.setTitle(title);
                        if (desc != null) p.setDescription(desc);
                        if (dueDateMillis > 0) p.setDueDate(new java.util.Date(dueDateMillis));
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

}
