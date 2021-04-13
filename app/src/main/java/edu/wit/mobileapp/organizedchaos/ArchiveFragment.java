package edu.wit.mobileapp.organizedchaos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ArchiveFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        class MainActivity extends AppCompatActivity {
            //Path to database
            private String path;
            private SQLiteDatabase db;

            //Columns for database query
            private String[] columns = {"_id", "complete", "title", "deadlineDate", "deadlineTime", "repeatSun", "repeatMon", "repeatTue", "repeatWed", "repeatThu", "repeatFri", "repeatSat", "task_text", "category_id"};

            //Selected Item that user clicks on
            public TaskItem selectedItem;


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
           //     setContentView(R.layout.activity_main);


                //Initialize swiprefresh()
                //  SwipeRefreshLayout swipeRefresh;

                //Set database location
                path = "/data/data/" + getPackageName() + "/organizedchaos.db";
                db = SQLiteDatabase.openOrCreateDatabase(path, null);

                //Initialize Task table
                String sql = "CREATE TABLE IF NOT EXISTS task " +
                        "(_id INTEGER PRIMARY KEY AUTOINCREMENT, complete BOOLEAN, title TEXT, " +
                        "deadlineDate DATE, deadlineTime TIME, repeatSun BOOLEAN, repeatMon BOOLEAN, " +
                        "repeatTue BOOLEAN, repeatWed BOOLEAN, repeatThu BOOLEAN, " +
                        "repeatFri BOOLEAN, repeatSat BOOLEAN, task_text TEXT, category_id INTEGER, " +
                        "CONSTRAINT fk_categories FOREIGN KEY (category_id) REFERENCES categories(category_id));";
                db.execSQL(sql);

                //Initialize Categories table
                sql = "CREATE TABLE IF NOT EXISTS categories " +
                        "(category_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, checked BOOLEAN);";
                db.execSQL(sql);

                // Create "General" category if it hasn't been created
                sql = "INSERT INTO categories(category_id, name, checked) " +
                        "SELECT 1, 'General', true " +
                        "WHERE NOT EXISTS(SELECT 1 FROM categories WHERE category_id = 1 AND name = 'General');";
                db.execSQL(sql);

                //Delete table if required
                //db.execSQL("DROP TABLE IF EXISTS task");

                FloatingActionButton button = (FloatingActionButton) findViewById(R.id.addtask);

                //Get latest list
                List<TaskItem> list = queryDatabase();

                //Add each item to
                TaskItemAdapter adapter;
                adapter = new TaskItemAdapter(this, 0, list);

                GridView gridView = (GridView) findViewById(R.id.GridView01);
                gridView.setAdapter(adapter);


                //Bundle for edit task
                Bundle bundle = new Bundle();

                //When user clicks on task to edit it
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent edittask = new Intent(getActivity(), Task.class);
                        selectedItem = (TaskItem) parent.getItemAtPosition(position);
                        bundle.putInt("taskId", selectedItem.id);
                        edittask.putExtras(bundle);
                        startActivity(edittask);
                    }
                });


                Intent addnewtask = new Intent(getActivity(), AddNewTask.class);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(addnewtask);
                    }
                });

            /*

            //Swipe Refresh to refresh list if complete box is checked
            swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //Stops infinite refresh
                    swipeRefresh.setRefreshing(false);

                    //Gets latest list
                    List<TaskItem> list = queryDatabase();

                    //Update the TaskAdapter and GridItems
                    updateGridView(list);
                }
            });

             */


            }

            //Queries database for latest list
            public List<TaskItem> queryDatabase() {
                path = "/data/data/" + getPackageName() + "/organizedchaos.db";
                db = SQLiteDatabase.openOrCreateDatabase(path, null);
                List<TaskItem> list = new ArrayList<TaskItem>();

                Cursor cursor = db.rawQuery("SELECT * FROM task WHERE complete = " + 0, null);

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    int complete = cursor.getInt(cursor.getColumnIndex("complete"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String deadlineDate = cursor.getString(cursor.getColumnIndex("deadlineDate"));
                    String deadlineTime = cursor.getString(cursor.getColumnIndex("deadlineTime"));
                    int repeatSun = cursor.getInt(cursor.getColumnIndex("repeatSun"));
                    int repeatMon = cursor.getInt(cursor.getColumnIndex("repeatMon"));
                    int repeatTue = cursor.getInt(cursor.getColumnIndex("repeatTue"));
                    int repeatWed = cursor.getInt(cursor.getColumnIndex("repeatWed"));
                    int repeatThu = cursor.getInt(cursor.getColumnIndex("repeatThu"));
                    int repeatFri = cursor.getInt(cursor.getColumnIndex("repeatFri"));
                    int repeatSat = cursor.getInt(cursor.getColumnIndex("repeatSat"));
                    String task_text = cursor.getString(cursor.getColumnIndex("task_text"));
                    int category_id = cursor.getInt(cursor.getColumnIndex("category_id"));

                    //Get string value of category id
                    Cursor categoryCursor = db.rawQuery("SELECT * FROM categories WHERE category_id = " + category_id, null);
                    String category = "";
                    if (categoryCursor.moveToFirst()) {
                        category = categoryCursor.getString(categoryCursor.getColumnIndex("name"));
                    }

                    //Create new task item and add it to list
                    TaskItem a = new TaskItem(id, complete, title, deadlineDate, deadlineTime, repeatSun, repeatMon, repeatTue, repeatWed, repeatThu, repeatFri, repeatSat, task_text, category, category_id);
                    list.add(a);

                    //Close connection to database
                    //db.close();

                }

                return list;
            }


            public void updateGridView(List<TaskItem> list) {
                TaskItemAdapter adapter = new TaskItemAdapter(this, 0, list);
                GridView gridView = (GridView) findViewById(R.id.GridView01);
                gridView.setAdapter(adapter);
            }

            @Override
            protected void onResume() {
                super.onResume();

                List<TaskItem> list = queryDatabase();

                updateGridView(list);
            }

        }

        return inflater.inflate(R.layout.fragment_archive, container, false);
    }
}
