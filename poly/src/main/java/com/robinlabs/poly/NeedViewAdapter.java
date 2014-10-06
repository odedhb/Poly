package com.robinlabs.poly;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * Created by oded on 2/13/14.
 */
public class NeedViewAdapter extends BaseExpandableListAdapter {

    private final Need need;
    private LayoutInflater inflater;
    private Activity activity;

    NeedViewAdapter(Need need) {
        this.need = need;
    }

    @Override
    public int getGroupCount() {
        return need.properties.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //collapsed title view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.property_title, null);
        }
        ((TextView) convertView).setText(need.properties.get(groupPosition).getQuestion());
        return convertView;
    }

    //expanded content view
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ExpandableListView list = (ExpandableListView) parent;

        final Property property = need.properties.get(groupPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.property_settings, null);
        }
        final EditText propQuestionView = (EditText) convertView.findViewById(R.id.property_question);

        propQuestionView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                property.question = propQuestionView.getText().toString();
            }
        });

        convertView.findViewById(R.id.remove_property).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.collapseGroup(groupPosition);
                need.properties.remove(groupPosition);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setInflater(LayoutInflater inflater, Activity act) {
        this.inflater = inflater;
        this.activity = act;
    }


}
