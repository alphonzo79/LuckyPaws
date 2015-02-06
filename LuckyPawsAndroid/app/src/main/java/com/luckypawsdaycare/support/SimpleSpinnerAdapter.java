/*
 * Copyright (c) 2013. Lucky Paws, Inc
 */

package com.luckypawsdaycare.support;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.luckypawsdaycare.R;

import java.util.List;

public class SimpleSpinnerAdapter extends ArrayAdapter<SimpleSpinnerValue> {
    private Activity context;
    List<SimpleSpinnerValue> options = null;

    private final String TAG = "SimpleSpinnerAdapter";

    public SimpleSpinnerAdapter(Activity context, List<SimpleSpinnerValue> options)
    {
//        super(context, context.getResources().getInteger(R.id.value_label), options);
        super(context, R.id.value_label, options);
        this.context = context;
        this.options = options;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {   // This view starts when we click the spinner.
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.value_selector_list_layout, parent, false);
        }

        SimpleSpinnerValue item = options.get(position);

        if(item != null)
        {   // Parse the data from each object and set it.
            ImageView checkbox = (ImageView) row.findViewById(R.id.value_checkbox);
            TextView optionLabel = (TextView) row.findViewById(R.id.value_label);
            if(checkbox != null)
            {
                if(item.isSelected()) {
                    checkbox.setImageResource(R.drawable.checked);
                } else {
                    checkbox.setImageResource(R.drawable.unchecked);
                }
            }

            if(optionLabel != null)
                optionLabel.setText(item.getName());
        }

        return row;
    }
}
