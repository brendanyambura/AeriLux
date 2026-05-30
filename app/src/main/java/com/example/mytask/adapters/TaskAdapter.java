package com.example.mytask.adapters;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mytask.R;
import com.example.mytask.models.Task;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskListener onTaskListener;

    public interface OnTaskListener {
        void onTaskClick(int position);
        void onTaskLongClick(int position);
        void onDeleteClick(int position);
        void onStatusChange(int position, boolean isCompleted);
    }

    public TaskAdapter(List<Task> taskList, OnTaskListener onTaskListener) {
        this.taskList = taskList;
        this.onTaskListener = onTaskListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view, onTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.checkBox.setChecked(task.isCompleted());

        if (task.isCompleted()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.title.setTextColor(Color.GRAY);
            holder.itemView.setAlpha(0.6f);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.title.setTextColor(Color.BLACK);
            holder.itemView.setAlpha(1.0f);
        }

        int priorityColor;
        switch (task.getPriority()) {
            case "High":
                priorityColor = Color.parseColor("#EE5253");
                break;
            case "Medium":
                priorityColor = Color.parseColor("#FF9F43");
                break;
            default:
                priorityColor = Color.parseColor("#1DD1A1");
                break;
        }
        holder.priorityView.getBackground().setTint(priorityColor);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CheckBox checkBox;
        View priorityView;
        ImageButton deleteBtn;

        public TaskViewHolder(@NonNull View itemView, OnTaskListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.text_task_title);
            checkBox = itemView.findViewById(R.id.checkbox_completed);
            priorityView = itemView.findViewById(R.id.view_priority);
            deleteBtn = itemView.findViewById(R.id.btn_delete);

            itemView.setOnLongClickListener(v -> {
                listener.onTaskLongClick(getAdapterPosition());
                return true;
            });

            deleteBtn.setOnClickListener(v -> listener.onDeleteClick(getAdapterPosition()));

            checkBox.setOnClickListener(v -> {
                if (checkBox.isChecked()) {
                    v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction(() -> 
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                    ).start();
                }
                listener.onStatusChange(getAdapterPosition(), checkBox.isChecked());
            });
        }
    }
}