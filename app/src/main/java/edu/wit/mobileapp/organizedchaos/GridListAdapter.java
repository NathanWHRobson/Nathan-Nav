package edu.wit.mobileapp.organizedchaos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import java.util.List;

/**
 * Created by sonu on 08/02/17.
 */

public class GridListAdapter extends BaseAdapter {
    private Context context;
    private List<CategoryItem> listOfCat;
    private LayoutInflater inflater;
    private boolean isListView;
    private int selectedPosition;

    public GridListAdapter(Context context, List<CategoryItem> listOfCat, boolean isListView, int selPosition) {
        this.context = context;
        this.listOfCat = listOfCat;
        this.isListView = isListView;
        inflater = LayoutInflater.from(context);
        selectedPosition = selPosition;
    }

    @Override
    public int getCount() {
        return listOfCat.size();
    }

    @Override
    public Object getItem(int i) {
        return listOfCat.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listOfCat.get(i).categoryID;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();

            //inflate the layout on basis of boolean
            if (isListView)
                view = inflater.inflate(R.layout.listview_radiobtn_categories, viewGroup, false);

            viewHolder.radioButton = (RadioButton) view.findViewById(R.id.radio_button);

            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        viewHolder.radioButton.setText(listOfCat.get(i).categoryTitle);

        //check the radio button if both position and selectedPosition matches
        viewHolder.radioButton.setChecked(i == selectedPosition);
        listOfCat.get(i).isChecked = (i == selectedPosition);

        //Set the position tag to both radio button and label
        viewHolder.radioButton.setTag(i);

        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCheckChanged(v);
            }
        });
        listOfCat.get(i).rb = viewHolder.radioButton;

        return view;
    }

    //On selecting any view set the current position to selectedPositon and notify adapter
    private void itemCheckChanged(View v) {
        selectedPosition = (Integer) v.getTag();
        listOfCat.get(selectedPosition).isChecked = true;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private RadioButton radioButton;
    }
}