package com.example.planner.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planner.R;
import com.example.planner.adapters.SubtaskAdapter;
import com.example.planner.models.Planner;
import com.example.planner.models.Subtask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "extra_task";

    // Core task
    private Planner task;

    // Top fields
    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView tvDueDate;
    private CheckBox completedCheckBox;

    // Notes
    private EditText editTextNotes;

    // Subtasks UI
    private RecyclerView subtaskRecyclerView;
    private EditText editTextNewSubtask;
    private ImageButton buttonAddSubtask;
    private SubtaskAdapter subtaskAdapter;

    // Save
    private Button saveButton;

    // Back to MainActivity
    private FloatingActionButton fabBack;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dueSdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);


        titleEditText       = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);
        tvDueDate           = findViewById(R.id.detailDueDate);
        completedCheckBox   = findViewById(R.id.detailCompletedCheckBox);
        editTextNotes       = findViewById(R.id.etNotes);

        subtaskRecyclerView = findViewById(R.id.subtaskRecyclerView);
        editTextNewSubtask  = findViewById(R.id.editTextNewSubtask);
        buttonAddSubtask    = findViewById(R.id.buttonAddSubtask);

        saveButton          = findViewById(R.id.buttonSaveNotes);
        fabBack             = findViewById(R.id.fabBack);

        //Load task from intent
        task = (Planner) getIntent().getSerializableExtra(EXTRA_TASK);

        if (task != null) {
            bindTaskToViews();
        }

        //Subtasks list
        subtaskAdapter = new SubtaskAdapter(
                task != null ? task.getSubtasks() : null,
                new SubtaskAdapter.Listener() {
                    @Override
                    public void onToggle(Subtask subtask) {

                        subtask.setDone(!subtask.isDone());
                        int idx = task.getSubtasks().indexOf(subtask);
                        if (idx >= 0) subtaskAdapter.notifyItemChanged(idx);
                    }

                    @Override
                    public void onDelete(Subtask subtask) {
                        int idx = task.getSubtasks().indexOf(subtask);
                        if (idx >= 0) {
                            task.getSubtasks().remove(idx);
                            subtaskAdapter.notifyItemRemoved(idx);
                        }
                    }
                }
        );
        subtaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        subtaskRecyclerView.setAdapter(subtaskAdapter);

        // Add subtask
        buttonAddSubtask.setOnClickListener(v -> {
            if (task == null) return;
            String title = editTextNewSubtask.getText().toString().trim();
            if (title.isEmpty()) return;

            Subtask s = new Subtask(title);
            task.addSubtask(s);
            subtaskAdapter.notifyItemInserted(task.getSubtasks().size() - 1);
            editTextNewSubtask.setText("");
        });


        tvDueDate.setOnClickListener(v -> {
            if (task == null) return;
            Calendar base = Calendar.getInstance();
            if (task.getDueDate() != null) base.setTime(task.getDueDate());

            new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        base.set(Calendar.YEAR, year);
                        base.set(Calendar.MONTH, month);
                        base.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        task.setDueDate(base.getTime());
                        tvDueDate.setText(dueSdf.format(task.getDueDate()));
                    },
                    base.get(Calendar.YEAR),
                    base.get(Calendar.MONTH),
                    base.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Use back arrow to return to MainActivity
        fabBack.setOnClickListener(v -> {
            finish();
        });

        //Save and return to MainActivity
        saveButton.setOnClickListener(v -> {
            if (task == null) {
                finish();
                return;
            }


            task.setTitle(titleEditText.getText().toString());
            task.setDescription(descriptionEditText.getText().toString());
            task.setNotes(editTextNotes.getText().toString());
            if (completedCheckBox != null) {
                task.setCompleted(completedCheckBox.isChecked());
            }


            Intent result = new Intent();
            result.putExtra("task_id", task.getId());
            result.putExtra("updated_title", task.getTitle());
            result.putExtra("updated_description", task.getDescription());
            result.putExtra("updated_due_date",
                    task.getDueDate() != null ? task.getDueDate().getTime() : -1L);
            result.putExtra(EXTRA_TASK, task);

            setResult(RESULT_OK, result);
            finish();
        });
    }

    private void bindTaskToViews() {
        titleEditText.setText(task.getTitle() != null ? task.getTitle() : "");
        descriptionEditText.setText(task.getDescription() != null ? task.getDescription() : "");
        editTextNotes.setText(task.getNotes() != null ? task.getNotes() : "");

        if (task.getDueDate() != null) {
            tvDueDate.setText(dueSdf.format(task.getDueDate()));
        } else {
            tvDueDate.setText("Due Date");
        }

        if (completedCheckBox != null) {
            completedCheckBox.setChecked(task.isCompleted());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}



