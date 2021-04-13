package edu.wit.mobileapp.organizedchaos;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Task extends AppCompatActivity {

    private Button backButton;
    private Button editButton;
    private FloatingActionButton completeButton;
    private EditText taskText;
    private SQLiteDatabase db;
    private String path;
    private Bundle receivedBundle;
    private Bundle editBundle;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // Fetch buttons
        backButton = (Button)findViewById(R.id.btnBack);
        editButton = (Button)findViewById(R.id.btnEdit);
        completeButton = (FloatingActionButton) findViewById(R.id.btnComplete);
        taskText = (EditText)findViewById(R.id.taskText);

        // Retrieve bundle and set task ID
        receivedBundle = this.getIntent().getExtras();
        taskId = receivedBundle.getInt("taskId");
        System.out.println("Received taskId: " + taskId);

        // Set the intent for exiting the task
        Intent exitTask = new Intent();
        exitTask.setClass(Task.this, MainActivity.class);

        // Set the intent for entering the edit menu
        Intent gotoEdit = new Intent(Task.this, AddNewTask.class);
        editBundle = new Bundle();
        editBundle.putInt("taskId", taskId);
        editBundle.putInt("addOrEdit", 1); // '1' indicates that the user wants to edit

        // Open database
        path = "/data/data/" + getPackageName() + "/organizedchaos.db";
        db = SQLiteDatabase.openOrCreateDatabase(path, null);

        //Get string value of category id
        Cursor taskCursor = db.rawQuery("SELECT * FROM task WHERE _id = "+ taskId,null);
        String text = "";
        taskCursor.moveToFirst();
        text = taskCursor.getString(taskCursor.getColumnIndex("task_text"));

        if(text == null) {
            System.out.println("Text is null");
        } else {
            System.out.println("Setting text to: " + text);
            taskText.setText(text);
        }

        // If the user presses the back button...
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!taskText.getText().toString().matches("")) { // "If the string is empty"
                    // Alert user they are going to exit without saving
                    alertUser(exitTask);
                } else {
                    // Otherwise, exit task
                    startActivity(exitTask);
                }
            }
        });

        // If the user clicks the edit button, go to AddNewTask
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEdit.putExtras(editBundle);
                db.close();
                startActivity(gotoEdit);
            }
        });

        // If the user presses the check button, save task text to database
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = taskText.getText().toString();

                String sql = "UPDATE task SET task_text = "+"'"+text+"' "+  "WHERE _id = "+"'"+Integer.toString(taskId)+"'";
                db.execSQL(sql);

                startActivity(exitTask);
            }
        });
    }

    // Method for alerting the user
    private void alertUser(Intent exitTask) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you would like to exit without saving?");
        alert.setTitle("Save Task");

        // Exit the task if the user clicks "Yes"
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(exitTask);
            }
        });

        // Return to task if user clicks "Cancel"
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog showAlert = alert.create();
        showAlert.show();
    }
}
