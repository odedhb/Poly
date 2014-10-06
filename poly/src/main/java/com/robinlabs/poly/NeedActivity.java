package com.robinlabs.poly;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

public class NeedActivity extends Activity {

    private Need editedNeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need);

        int selectedNeed = getIntent().getIntExtra(Const.NEED_POSITION, -1);

        if (selectedNeed > -1) {
            editedNeed = TheBrainer.instance.needsInMemory.get(selectedNeed);
        } else {
            editedNeed = new Need();
        }

        refreshViews();

        getSaveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText uri = ((EditText) findViewById(R.id.need_uri));
                final EditText title = ((EditText) findViewById(R.id.need_title));
                editedNeed.uri = uri.getText().toString();
                editedNeed.name = title.getText().toString();
                editedNeed.saveToStorage(NeedActivity.this);
            }
        });

        findViewById(R.id.add_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editedNeed.properties.add(new Property());
                refreshListView();
            }
        });

        final EditText title = ((EditText) findViewById(R.id.need_title));
        final EditText uri = ((EditText) findViewById(R.id.need_uri));

        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editedNeed.name = title.getText().toString();
            }
        });

        uri.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editedNeed.uri = uri.getText().toString();
            }
        });


    }

    private void refreshListView() {
        NeedViewAdapter needViewAdapter = new NeedViewAdapter(editedNeed);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        needViewAdapter.setInflater(inflater, this);
        getExpandableListView().setAdapter(needViewAdapter);
    }

    private void refreshViews() {
        ((EditText) findViewById(R.id.need_title)).setText(editedNeed.name);
        ((EditText) findViewById(R.id.need_uri)).setText(editedNeed.uri);

        refreshListView();
    }

   /* private void saveNeed() {

        List<Property> properties = new ArrayList<Property>();
        int propertyCount = getExpandableListView().getChildCount();
        for (int i = 0; i < propertyCount; i++) {
            View propertyView = getExpandableListView().getChildAt(i).;
            String name = ((EditText) propertyView.findViewById(R.id.property_name)).getText().toString();
            String question = ((EditText) propertyView.findViewById(R.id.property_question)).getText().toString();
            Property p = new Property(name);
            p.setQuestion(question);
            properties.add(p);
        }
        need.properties = properties;


    } */

    private ExpandableListView getExpandableListView() {
        return (ExpandableListView) findViewById(R.id.question_list);
    }

    Button getSaveButton() {
        return (Button) findViewById(R.id.save);
    }

}
