package edu.wit.mobileapp.organizedchaos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TaskItemAdapter extends ArrayAdapter<TaskItem> {
    private String path;
    private SQLiteDatabase db;
    private LayoutInflater mInflater;

    public TaskItemAdapter(Context context, int rid, List<TaskItem> list){
        super(context, rid, list);
        mInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        //Set database location
        path = "/data/data/edu.wit.mobileapp.organizedchaos/organizedchaos.db";
        db = SQLiteDatabase.openOrCreateDatabase(path, null);

        //Retrieve Data
        TaskItem item = (TaskItem) getItem(position);

        //Use Layout file to generate view
        View view = mInflater.inflate(R.layout.task_item,null);

        //Set title
        TextView title;
        title = (TextView) view.findViewById(R.id.tasktitle);
        title.setText(item.title);

        //Set description
        TextView description;
        description = (TextView) view.findViewById(R.id.description);
        description.setText(item.task_text);


        //Set date/time
        TextView date;
        date = (TextView) view.findViewById(R.id.date);
        if(item.deadlineDate == null && item.deadlineTime == null){
            date.setText("Due: None");
        }else{
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            try {
                final Date time = sdf.parse(item.deadlineTime);
                date.setText(String.format("Due: " + item.deadlineDate + ' ' + new SimpleDateFormat("hh:mm aa").format(time)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //Set Category
        TextView category;
        category = (TextView) view.findViewById(R.id.category);
        category.setText("Category: " + item.category);

        //Get value from checkbox
        CheckBox complete;
        complete = (CheckBox) view.findViewById(R.id.checkBox);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(complete.isChecked()){
                    String sql = "UPDATE task SET complete = "+1+ " WHERE _id = " + item.id;
                    db.execSQL(sql);
                }else{
                    String sql = "UPDATE task SET complete = "+0+ " WHERE _id = " + item.id;
                    db.execSQL(sql);
                }

            }
        });
        return view;
    }
}
