package com.example.planner.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

    // Preference boolean; indicates if autosave is currently enabled/disabled
    private boolean mPrefUseAutoSave;

    // Name for/of Preference file on device or emulator
    private final String mKeyPrefsName = "PREFS";

    // Preference Key
    private String mKeyAutoSave = "AUTO_SAVE";

    private void restoreAppSettingsFromPrefs() {
        // Since this is for reading only, no editor is needed unlike in saveRestoreState
        SharedPreferences preferences = getSharedPreferences(mKeyPrefsName, MODE_PRIVATE);

        // restore AutoSave preference value
        mPrefUseAutoSave = preferences.getBoolean(mKeyAutoSave, true);
    }

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

        restoreAppSettingsFromPrefs();

        //  call plannerList from savedInstanceState if there is
        if (savedInstanceState != null) {
            plannerList = (ArrayList<Planner>) savedInstanceState.getSerializable("planner_list");
        } else if (mPrefUseAutoSave) { // call from storage
            SharedPreferences preferences = getSharedPreferences(mKeyPrefsName, MODE_PRIVATE);
            String json = preferences.getString("saved_planner_list", null);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Planner>>() {}.getType();
                plannerList = gson.fromJson(json, type);
            }
        }
        // if plannerList has not been set yet set to empty list
        if (plannerList == null) {
            plannerList = new ArrayList<>();
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

        // Set the AutoSave checkbox state from preferences
        MenuItem autoSaveItem = menu.findItem(R.id.pref_auto_save);
        autoSaveItem.setChecked(mPrefUseAutoSave); // sync the UI with the saved value

        return true;
    }

    // Toolbar actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.pref_auto_save) {
            toggleMenuItem(item);
            mPrefUseAutoSave = item.isChecked();
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

    private void toggleMenuItem(MenuItem item) {
        item.setChecked(!item.isChecked());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_TASK_DETAILS && resultCode == RESULT_OK && data != null) {

            //
            if (requestCode == REQ_TASK_DETAILS && resultCode == RESULT_OK && data != null) {
                // if you return the whole Planner object
                Planner updated = (Planner) data.getSerializableExtra(TaskDetailsActivity.EXTRA_TASK);
                if (updated != null && updated.getId() != null) {
                    for (int i = 0; i < plannerList.size(); i++) {
                        if (updated.getId().equals(plannerList.get(i).getId())) {
                            if (updated.isCompleted()) { // uses your model's isCompleted()
                                plannerList.remove(i);
                                adapter.notifyItemRemoved(i);
                            } else {
                                plannerList.set(i, updated);
                                adapter.notifyItemChanged(i);
                            }
                            return;
                        }
                    }
                }

                String id = data.getStringExtra("task_id");
                boolean completed = data.getBooleanExtra("completed", false);
                if (id != null) {
                    for (int i = 0; i < plannerList.size(); i++) {
                        if (id.equals(plannerList.get(i).getId())) {
                            if (completed) {
                                plannerList.remove(i);
                                adapter.notifyItemRemoved(i);
                            } else {
                                // update other fields here if you pass them back
                                adapter.notifyItemChanged(i);
                            }
                            return;
                        }
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

    @Override
    protected void onStop() {
        saveToSharedPref();
        super.onStop();
    }

    private void saveToSharedPref() {
        // Create a SP reference to the prefs file on the device whose name matches mKeyPrefsName
        // If the file on the device does not yet exist, then it will be created
        SharedPreferences preferences = getSharedPreferences(mKeyPrefsName, MODE_PRIVATE);

        // Create an Editor object to write changes to the preferences object above
        SharedPreferences.Editor editor = preferences.edit();

        // clear whatever was set last time
        editor.clear();

        // save the settings (Show Errors and Use AutoSave)
        saveSettingsToSharedPrefs(editor);

        // if autoSave is on then save the board
        saveListToSharedPrefsIfAutoSaveIsOn(editor);

        // apply the changes to the XML file in the device's storage
        editor.apply();
    }

    private void saveListToSharedPrefsIfAutoSaveIsOn(SharedPreferences.Editor editor) {
        if (mPrefUseAutoSave) {
            Gson gson = new Gson();
            String json = gson.toJson(plannerList);
            editor.putString("saved_planner_list", json);
        }
    }

    private void saveSettingsToSharedPrefs(SharedPreferences.Editor editor) {
        // save "autoSave" preference
        editor.putBoolean(mKeyAutoSave, mPrefUseAutoSave);
    }
}
