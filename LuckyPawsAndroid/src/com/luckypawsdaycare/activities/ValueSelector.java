/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.luckypawsdaycare.R;

import java.util.ArrayList;
import java.util.List;

public class ValueSelector extends ListActivity {
    String[] values;
    String selectedValue;
    String header;

    Button saveButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Intent creatingIntent = getIntent();
        values = creatingIntent.getStringArrayExtra("valuesArray");
        selectedValue = creatingIntent.getStringExtra("selectedValue");
        header = creatingIntent.getStringExtra("header");

        setContentView(R.layout.value_selector);

        saveButton = (Button)findViewById(R.id.save_button);
        cancelButton = (Button)findViewById(R.id.cancel_button);
        saveButton.setOnClickListener(saveListener);
        cancelButton.setOnClickListener(cancelListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(header != null) {
            TextView headerDisplay = (TextView)findViewById(R.id.header_text);
            headerDisplay.setText(header);
        }
        prepListView();
    }

    private void prepListView() {
        List<IndividualItem> listItems = new ArrayList<IndividualItem>();
        if(values != null) {
            for(String value : values) {
                boolean selected = false;
                if(selectedValue != null && selectedValue.equals(value))
                    selected = true;
                listItems.add(new IndividualItem(value, selected));
            }
        }

        setListAdapter(new IndividualItemAdapter(this, R.layout.value_selector_list_layout, listItems));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        int itemCount = l.getCount();
        for(int i = 0; i < itemCount; i++) {
            IndividualItem thisItem = (IndividualItem)l.getItemAtPosition(i);
            if(i == position) {
                thisItem.selected = true;
                selectedValue = thisItem.getName();
            } else {
                thisItem.selected = false;
            }
        }
        prepListView();
    }

    Button.OnClickListener saveListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent result = new Intent();
            result.putExtra("selectedValue", selectedValue);
            setResult(RESULT_OK, result);
            finish();
        }
    };

    Button.OnClickListener cancelListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent result = new Intent();
            setResult(RESULT_CANCELED);
            finish();
        }
    };

    ////////////////////////////////////
    // Modal List Inflation Utilities //
    ////////////////////////////////////

    static class IndividualItem {
        String optionText;
        boolean selected;

        IndividualItem(String text, boolean selected) {
            optionText = text;
            this.selected = selected;
        }

        String getName() {
            return optionText;
        }

        boolean getSelected() {
            return selected;
        }
    }

    class IndividualItemAdapter extends ArrayAdapter<IndividualItem> {

        int listLayout;
        List<IndividualItem> list;

        IndividualItemAdapter(Context context, int listLayout, List<IndividualItem> list){
            super(context, listLayout, R.id.value_label, list);
            this.listLayout = listLayout;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            IndividualItemWrapper wrapper = null;

            if (convertView == null){
                convertView = getLayoutInflater().inflate(listLayout, null);
                wrapper = new IndividualItemWrapper(convertView);
                convertView.setTag(wrapper);
            }
            else{
                wrapper = (IndividualItemWrapper)convertView.getTag();
            }

            wrapper.populateFrom(getItem(position));

            return convertView;
        }

        public List<IndividualItem> getList() {
            return list;
        }
    }

    class IndividualItemWrapper{

        private TextView filterOption = null;
        private ImageView icon = null;
        private View row = null;

        IndividualItemWrapper(View row){
            this.row = row;
        }

        TextView getItemOption(){
            if (filterOption == null){
                filterOption = (TextView)row.findViewById(R.id.value_label);
            }
            return filterOption;
        }

        ImageView getIcon(){
            if (icon == null){
                icon = (ImageView)row.findViewById(R.id.value_checkbox);
            }
            return icon;
        }

        void populateFrom(IndividualItem item){
            getItemOption().setText(item.getName());
            if(item.getSelected()) {
                getIcon().setImageResource(R.drawable.checked);
            } else {
                getIcon().setImageResource(R.drawable.unchecked);
            }
        }
    }
}
