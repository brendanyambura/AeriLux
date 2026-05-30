package com.example.mytask;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytask.adapters.TaskAdapter;
import com.example.mytask.models.Task;
import com.example.mytask.utils.DataHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private List<Task> filteredList;
    private FloatingActionButton fabAdd;
    private TextView textSummary;
    private LinearProgressIndicator progressBar;
    private LinearLayout layoutEmpty;
    private EditText editSearch;
    private TextView textQuote;

    private final String[] quotes = {
            "\"Stay focused and be productive!\"",
            "\"Small steps lead to big goals.\"",
            "\"Make today count!\"",
            "\"Organize your life, achieve your dreams.\"",
            "\"Your future is created by what you do today.\""
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadData();
        setupRecyclerView();
        setupSearch();
        updateSummary();
        setRandomQuote();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_tasks);
        fabAdd = findViewById(R.id.fab_add);
        textSummary = findViewById(R.id.text_task_summary);
        progressBar = findViewById(R.id.progress_tasks);
        layoutEmpty = findViewById(R.id.layout_empty);
        editSearch = findViewById(R.id.edit_search);
        textQuote = findViewById(R.id.text_quote);

        fabAdd.setOnClickListener(v -> showTaskDialog(null));
    }

    private void setRandomQuote() {
        int index = (int) (Math.random() * quotes.length);
        textQuote.setText(quotes[index]);
    }

    private void loadData() {
        taskList = DataHelper.loadTasks(this);
        filteredList = new ArrayList<>(taskList);
        sortTasks();
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(filteredList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder t) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                onDeleteClick(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setupSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(taskList);
        } else {
            for (Task item : taskList) {
                if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void showTaskDialog(Task taskToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        
        EditText editTitle = view.findViewById(R.id.edit_task_title);
        Spinner spinnerPriority = view.findViewById(R.id.spinner_priority);
        
        if (taskToEdit != null) {
            editTitle.setText(taskToEdit.getTitle());
            int spinnerPosition = 0;
            for (int i = 0; i < spinnerPriority.getCount(); i++) {
                if (spinnerPriority.getItemAtPosition(i).toString().equalsIgnoreCase(taskToEdit.getPriority())) {
                    spinnerPosition = i;
                    break;
                }
            }
            spinnerPriority.setSelection(spinnerPosition);
        }

        builder.setView(view)
                .setPositiveButton(taskToEdit == null ? "Add" : "Update", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();
                    String priority = spinnerPriority.getSelectedItem().toString();

                    if (title.isEmpty()) {
                        Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (taskToEdit == null) {
                        Task newTask = new Task(System.currentTimeMillis(), title, priority, false);
                        taskList.add(newTask);
                    } else {
                        taskToEdit.setTitle(title);
                        taskToEdit.setPriority(priority);
                    }
                    
                    saveAndRefresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveAndRefresh() {
        sortTasks();
        DataHelper.saveTasks(this, taskList);
        filter(editSearch.getText().toString());
        updateSummary();
    }

    private void sortTasks() {
        taskList.sort((t1, t2) -> {
            if (t1.isCompleted() != t2.isCompleted()) {
                return t1.isCompleted() ? 1 : -1;
            }
            return Long.compare(t2.getId(), t1.getId());
        });
    }

    private void updateSummary() {
        int total = taskList.size();
        int completed = 0;
        for (Task task : taskList) {
            if (task.isCompleted()) completed++;
        }
        int remaining = total - completed;
        
        String summary = remaining + " Tasks Remaining | " + completed + " Completed";
        textSummary.setText(summary);
        
        if (total > 0) {
            progressBar.setProgress((completed * 100) / total);
        } else {
            progressBar.setProgress(0);
        }
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTaskClick(int position) {}

    @Override
    public void onTaskLongClick(int position) {
        showTaskDialog(filteredList.get(position));
    }

    @Override
    public void onDeleteClick(int position) {
        Task deletedTask = filteredList.get(position);
        int originalIndex = taskList.indexOf(deletedTask);
        
        taskList.remove(deletedTask);
        saveAndRefresh();

        Snackbar.make(recyclerView, "Task Deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                    taskList.add(originalIndex, deletedTask);
                    saveAndRefresh();
                }).show();
    }

    @Override
    public void onStatusChange(int position, boolean isCompleted) {
        Task task = filteredList.get(position);
        task.setCompleted(isCompleted);
        saveAndRefresh();
    }
}