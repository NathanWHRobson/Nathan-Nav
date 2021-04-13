package edu.wit.mobileapp.organizedchaos;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class AddNewTask extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "oc_01";

    private Switch setDeadline;
    private DatePicker dateDeadline;
    private TimePicker timeDeadline;
    private Switch setRepeat;
    private ChipGroup daysOfWeek;
    private EditText taskTitle;
    private ListView categories;
    private Button addTask;
    private Button editTask;
    private SQLiteDatabase db;
    private String path;
    private List<CategoryItem> listOfCat;
    private int taskId;
    private Intent enterTask;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);


        path = "/data/data/" + getPackageName() + "/organizedchaos.db";
        db = SQLiteDatabase.openOrCreateDatabase(path, null);
        String sql = "CREATE TABLE IF NOT EXISTS task " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, complete BOOLEAN, title TEXT, " +
                "deadlineDate DATE, deadlineTime TIME, repeatSun BOOLEAN, repeatMon BOOLEAN, " +
                "repeatTue BOOLEAN, repeatWed BOOLEAN, repeatThu BOOLEAN, " +
                "repeatFri BOOLEAN, repeatSat BOOLEAN, task_text TEXT, category_id INTEGER, " +
                "CONSTRAINT fk_categories FOREIGN KEY (category_id) REFERENCES categories(category_id));";
        db.execSQL(sql);

        setDeadline = (Switch) findViewById(R.id.switchSetDateTime);
        dateDeadline = (DatePicker) findViewById(R.id.calendarViewDeadline);
        timeDeadline = (TimePicker) findViewById(R.id.timePickerDeadline);
        setRepeat = (Switch) findViewById(R.id.switchRepeat);
        daysOfWeek = (ChipGroup) findViewById(R.id.daysOfWeek);
        taskTitle = (EditText) findViewById(R.id.editTextNewTaskTitle);
        categories = (ListView) findViewById(R.id.checkCatList);
        addTask = (Button) findViewById(R.id.btnNewTaskSubmit);
        editTask = (Button) findViewById(R.id.btnEditTaskSubmit);

//

        listOfCat = new ArrayList<CategoryItem>();
        String selectQuery = "SELECT * FROM categories";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            CategoryItem itemtemp = new CategoryItem();
            itemtemp.categoryTitle = cursor.getString(cursor.getColumnIndex("name"));
            itemtemp.categoryID = cursor.getInt(cursor.getColumnIndex("category_id"));
            listOfCat.add(itemtemp);
            cursor.moveToNext();
        }
        cursor.close();

        int isSelected = 0;
        Bundle bun = this.getIntent().getExtras();
        if (bun != null) {
            int whereFrom = bun.getInt("addOrEdit");
            if (whereFrom == 1) {
                addTask.setVisibility(View.GONE);
                editTask.setVisibility(View.VISIBLE);
//                taskTitle.setText();
                selectQuery = "SELECT * FROM task WHERE _id=" + Integer.toString(bun.getInt("taskId")) + ";";
                cursor = db.rawQuery(selectQuery, null);
                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    taskTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
                    if (cursor.getString(cursor.getColumnIndex("deadlineDate")) != null) {
                        setDeadline.setChecked(true);
                        String[] dateStored = cursor.getString(cursor.getColumnIndex("deadlineDate")).split("/");
                        dateDeadline.init(Integer.parseInt(dateStored[2]), Integer.parseInt(dateStored[0]), Integer.parseInt(dateStored[1]), null);
                        String[] timeStored = cursor.getString(cursor.getColumnIndex("deadlineTime")).split(":");
                        timeDeadline.setHour(Integer.parseInt(timeStored[0]));
                        timeDeadline.setMinute(Integer.parseInt(timeStored[1]));
                    }
                    boolean repExist = cursor.getString(cursor.getColumnIndex("repeatSun")) != null;
                    Log.v("myApp", String.valueOf(repExist));
                    boolean sunRep = cursor.getInt(cursor.getColumnIndex("repeatSun")) == 0 ? false : true;
                    boolean monRep = cursor.getInt(cursor.getColumnIndex("repeatMon")) == 0 ? false : true;
                    boolean tueRep = cursor.getInt(cursor.getColumnIndex("repeatTue")) == 0 ? false : true;
                    boolean wedRep = cursor.getInt(cursor.getColumnIndex("repeatWed")) == 0 ? false : true;
                    boolean thuRep = cursor.getInt(cursor.getColumnIndex("repeatThu")) == 0 ? false : true;
                    boolean friRep = cursor.getInt(cursor.getColumnIndex("repeatFri")) == 0 ? false : true;
                    boolean satRep = cursor.getInt(cursor.getColumnIndex("repeatSat")) == 0 ? false : true;
                    boolean daysSel[] = {sunRep, monRep, tueRep, wedRep, thuRep, friRep, satRep};
                    if (repExist) {
                        setRepeat.setChecked(true);
                        for (int j = 0; j < 7; j++) {
                            if (daysSel[j] == true) {
                                ((Chip) daysOfWeek.getChildAt(j)).setChecked(true);
                            }
                        }
                    }
                    int catSel = cursor.getInt(cursor.getColumnIndex("category_id"));
                    for (int i = 0; i < listOfCat.size(); i++) {
                        CategoryItem cat = (CategoryItem) listOfCat.get(i);
                        if (cat.categoryID == catSel) {
                            isSelected = (Integer) i;
                            cat.isChecked = true;
                            Log.v("MyApp", String.valueOf(catSel));
                        }
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            } else {
                addTask.setVisibility(View.VISIBLE);
                editTask.setVisibility(View.GONE);
            }
        } else {
            addTask.setVisibility(View.VISIBLE);
            editTask.setVisibility(View.GONE);
        }

        showDeadlineAttributes();
        showRepeatAttributes();
        GridListAdapter adapter = new GridListAdapter(this, listOfCat, true, isSelected);
        categories.setAdapter(adapter);
        ViewGroup.LayoutParams l = categories.getLayoutParams();
        l.height = 150 * listOfCat.size();
        categories.setLayoutParams(l);

        // Create intent and bundle for when the task is created
        enterTask = new Intent(AddNewTask.this, Task.class);
        bundle = new Bundle();

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = "/data/data/" + getPackageName() + "/organizedchaos.db";
                db = SQLiteDatabase.openOrCreateDatabase(path, null);
                ContentValues values = new ContentValues();
                String titleInput = taskTitle.getText().toString();
                values.put("title", titleInput);
                values.put("complete", false);
                if (setDeadline.isChecked()) {
                    values.put("deadlineDate", dateDeadline.getMonth() + 1 + "/" + dateDeadline.getDayOfMonth() + "/" + dateDeadline.getYear());
                    values.put("deadlineTime", timeDeadline.getHour() + ":" + timeDeadline.getMinute());
                }
                if (setRepeat.isChecked()) {
                    int dayChipsCount = daysOfWeek.getChildCount();
                    String daysOfWeekLbl[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                    if (dayChipsCount > 0) {
                        for (int i = 0; i < dayChipsCount; i++) {
                            Chip c = (Chip) daysOfWeek.getChildAt(i);
                            String rptLbl = "repeat" + daysOfWeekLbl[i];
                            Boolean check = false;
                            if (c.isChecked()) {
                                check = true;
                            }
                            values.put(rptLbl, check);
                        }
                    }
                }
                int numOfCat = categories.getChildCount();
                int selCat = -1;
                for (int i = 0; i < numOfCat; i++) {
                    CategoryItem cat = (CategoryItem) categories.getItemAtPosition(i);
                    if (cat.isChecked) {
                        selCat = cat.categoryID;
                    }
                }
                Log.v("MyApp", String.valueOf(selCat));
                values.put("category_id", selCat);
                values.put("task_text", "");
                if (!titleInput.isEmpty() && selCat != -1) {
                    db.insert("task", null, values);
//                    LocalDateTime dueDate = LocalDateTime.of(dateDeadline.getYear(), dateDeadline.getMonth(), dateDeadline.getDayOfMonth(), timeDeadline.getHour(), timeDeadline.getMinute());
//                    schedNotification(getNotification(titleInput), dueDat) ;

                    // Query to select most recent task (the one being created now)
                    String selectQuery = "SELECT * " +
                            "FROM task " +
                            "WHERE _id = (SELECT MAX(_id)  FROM task);";
                    Cursor cursor = db.rawQuery(selectQuery, null);

                    // Get the id of the task
                    cursor.moveToFirst();
                    taskId = cursor.getInt(0);
                    cursor.close();

                    // Pass the id to the task activity
                    bundle.putInt("taskId", taskId);
                    enterTask.putExtras(bundle);
                    db.close();
                    startActivity(enterTask);
                }

            }
        });

        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = "/data/data/" + getPackageName() + "/organizedchaos.db";
                db = SQLiteDatabase.openOrCreateDatabase(path, null);
                ContentValues values = new ContentValues();
                String titleInput = taskTitle.getText().toString();
                values.put("title", titleInput);
                values.put("complete", false);
                if (setDeadline.isChecked()) {
                    values.put("deadlineDate", dateDeadline.getMonth() + "/" + dateDeadline.getDayOfMonth() + "/" + dateDeadline.getYear());
                    values.put("deadlineTime", timeDeadline.getHour() + ":" + timeDeadline.getMinute());
                }
                if (setRepeat.isChecked()) {
                    int dayChipsCount = daysOfWeek.getChildCount();
                    String daysOfWeekLbl[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                    if (dayChipsCount > 0) {
                        for (int i = 0; i < dayChipsCount; i++) {
                            Chip c = (Chip) daysOfWeek.getChildAt(i);
                            String rptLbl = "repeat" + daysOfWeekLbl[i];
                            Boolean check = false;
                            if (c.isChecked()) {
                                check = true;
                            }
                            values.put(rptLbl, check);
                        }
                    }
                }
                int numOfCat = categories.getChildCount();
                int selCat = -1;
                for (int i = 0; i < numOfCat; i++) {
                    CategoryItem cat = (CategoryItem) categories.getItemAtPosition(i);
                    if (cat.isChecked) {
                        selCat = cat.categoryID;
                    }
                }
                Log.v("MyApp", String.valueOf(selCat));
                values.put("category_id", selCat);
                //values.put("task_text", "");
                if (!titleInput.isEmpty() && selCat != -1) {
                    String[] args = {String.valueOf(bun.getInt("taskId"))};
                    db.update("task", values, "_id=?", args);
//                    LocalDateTime dueDate = LocalDateTime.of(dateDeadline.getYear(), dateDeadline.getMonth(), dateDeadline.getDayOfMonth(), timeDeadline.getHour(), timeDeadline.getMinute());
//                    schedNotification(getNotification(titleInput), System.currentTimeMillis()) ;

                    System.out.println("Parsed taskId: " + Integer.parseInt(args[0]));
                    // Pass the id to the task activity
                    bundle.putInt("taskId", Integer.parseInt(args[0]));
                    enterTask.putExtras(bundle);
                    db.close();
                    startActivity(enterTask);
                }

            }
        });

        setDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeadlineAttributes();
            }
        });
        setRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRepeatAttributes();
            }
        });
        db.close();
    }

    private Notification getNotification(String title) {
//        createNotificationChannel();
//        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle("Don't forget!")
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
//        nm.notify(0, builder.build());
    }

    private void schedNotification(Notification notif, long delay) {
        Intent notifIntent = new Intent(this, NotificationReceiver.class);
        notifIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        notifIntent.putExtra(NotificationReceiver.NOTIFICATION, notif);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay, pendingIntent);
    }
//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("oc_01", name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    private void showRepeatAttributes() {
        if (setRepeat.isChecked()) {
            daysOfWeek.setVisibility(View.VISIBLE);
        } else {
            daysOfWeek.setVisibility(View.GONE);
        }
    }

    private void showDeadlineAttributes() {
        if (setDeadline.isChecked()) {
            dateDeadline.setVisibility(View.VISIBLE);
            timeDeadline.setVisibility(View.VISIBLE);
        } else {
            dateDeadline.setVisibility(View.GONE);
            timeDeadline.setVisibility(View.GONE);
        }
    }
}
