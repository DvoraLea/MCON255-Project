package com.example.planner.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.planner.R;
import com.example.planner.adapters.PlannerAdapter;
import com.example.planner.databinding.ActivityMainBinding;
import com.example.planner.models.Planner;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PlannerAdapter adapter;
    private List<Planner> plannerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // test data -DELETE This later
        plannerList = new ArrayList<>();
        plannerList.add(new Planner("Finish Homework", "Chapter 5 Exercises", new Date(), false));
        plannerList.add(new Planner("Buy Groceries", "Eggs, Milk, Bread", new Date(), false));


        adapter = new PlannerAdapter(plannerList);
        binding.plannerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.plannerRecyclerView.setAdapter(adapter);

        // FAB
        binding.fabAddTask.setOnClickListener(view -> {
            //dialog layout
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

                            Planner newTask = new Planner(title, description, dueDate, false);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
