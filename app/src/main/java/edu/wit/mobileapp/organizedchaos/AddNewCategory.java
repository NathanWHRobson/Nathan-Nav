package edu.wit.mobileapp.organizedchaos;

/*

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class AddNewCategory extends AppCompatActivity {

    private Button newCatButton;
    private ListView categories;
    private Button addCategory;
    private SQLiteDatabase db;
    private String path;
    private ArrayAdapter<String> adp;
    private List<String> listOfCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_category);

        newCatButton = (Button)findViewById(R.id.btnNewCategory);
        categories = (ListView)findViewById(R.id.catList);

        // Create default categories database if it doesn't exist
        path = "/data/data/" + getPackageName() + "/organizedchaos.db";
        db = SQLiteDatabase.openOrCreateDatabase(path, null);
        String sql = "CREATE TABLE IF NOT EXISTS categories " +
                "(category_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, checked BOOLEAN);";
        db.execSQL(sql);

        // Create "General" category if it hasn't been created
        sql = "INSERT INTO categories(category_id, name, checked) " +
                "SELECT 1, 'General', true " +
                "WHERE NOT EXISTS(SELECT 1 FROM categories WHERE category_id = 1 AND name = 'General');";
        db.execSQL(sql);

        // Create category list and populate it with categories
        listOfCat = new ArrayList<String>();
        String selectQuery = "SELECT * FROM categories";
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        while(cursor.isAfterLast() == false) {
            listOfCat.add(cursor.getString(1));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        adp = new ArrayAdapter<String>(this, R.layout.category_list_item, listOfCat);
        categories.setAdapter(adp);

        // When the new category button is clicked, open fragment
        newCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(savedInstanceState == null) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    Fragment newCatFragment = new NewCategoryFragment();
                    transaction.replace(R.id.newCatContainer, newCatFragment);
                    transaction.commit();
                }
            }
        });
    }

    // Called from the fragment to update the list in real-time
    public void updateList(String catTitle) {
        path = "/data/data/" + getPackageName() + "/organizedchaos.db";
        db = SQLiteDatabase.openOrCreateDatabase(path, null);

        // Fetch the category that was just created (the last one in the table)
        String selectQuery = "SELECT * FROM categories";
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToLast();

        // Add the category to the list
        listOfCat.add(cursor.getString(1));

        cursor.close();
        db.close();

        // Update list
        adp.notifyDataSetChanged();
    }
}

*/