package edu.wit.mobileapp.organizedchaos;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewCategoryFragment extends Fragment {

    private Button addNewCat;
    private View inflatedView;
    private EditText titleText;
    private String catTitle;
    private String path;
    private SQLiteDatabase db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewCategoryFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewCategoryFragment newInstance(String param1, String param2) {
        NewCategoryFragment fragment = new NewCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflatedView = inflater.inflate(R.layout.fragment_new_category, container, false);

        addNewCat = (Button)inflatedView.findViewById(R.id.btnAddCategory);
        titleText = (EditText)inflatedView.findViewById(R.id.newCatTitle);

        addNewCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titleText.getText().toString().replaceAll("\\s+","").matches("")) {
                    System.out.println("Please enter a name for the category");
                } else {
                    path = "/data/data/" + getActivity().getPackageName() + "/organizedchaos.db";
                    db = SQLiteDatabase.openOrCreateDatabase(path, null);

                    // Get the number of rows in the category table and set the new catID to the current number + 1
                    long count = DatabaseUtils.queryNumEntries(db, "categories");
                    int newCatId = (int)count + 1;

                    catTitle = titleText.getText().toString();

                    // Insert a new row into the categories database for the new category
                    String sql = "INSERT INTO categories(category_id, name, checked) " +
                            "SELECT " + newCatId + ", '" + catTitle + "', true " +
                            "WHERE NOT EXISTS(SELECT 1 FROM categories WHERE category_id = " + newCatId + " AND name = '" + catTitle + "');";
                    db.execSQL(sql);

                    // Update the list in the main activity and then close the fragment
                    ((CategoryFragment.AddNewCategory)getActivity()).updateList(catTitle);
                    getFragmentManager().beginTransaction().remove(NewCategoryFragment.this).commit();

                }
            }
        });

        return inflatedView;
    }
}
