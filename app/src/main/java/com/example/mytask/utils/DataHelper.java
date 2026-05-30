package com.example.mytask.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mytask.models.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DataHelper {
    private static final String PREF_NAME = "todo_prefs";
    private static final String TASKS_KEY = "tasks_list";

    public static void saveTasks(Context context, List<Task> tasks) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", task.getId());
                jsonObject.put("title", task.getTitle());
                jsonObject.put("priority", task.getPriority());
                jsonObject.put("isCompleted", task.isCompleted());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putString(TASKS_KEY, jsonArray.toString()).apply();
    }

    public static List<Task> loadTasks(Context context) {
        List<Task> tasks = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String tasksJson = prefs.getString(TASKS_KEY, null);
        if (tasksJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(tasksJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    tasks.add(new Task(
                            obj.getLong("id"),
                            obj.getString("title"),
                            obj.getString("priority"),
                            obj.getBoolean("isCompleted")
                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }
}