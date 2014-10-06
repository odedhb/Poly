package com.robinlabs.poly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class NeedListActivity extends Activity {


    private ListView listView;
    private List<Need> needList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_list);

        needList = Storage.getAllNeeds(this);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.need_list);

        // Defined Array values to show in ListView
        String[] values = new String[needList.size()];

        for (int i = 0; i < needList.size(); i++) {
            values[i] = needList.get(i).name;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(NeedListActivity.this, NeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(Const.NEED_POSITION, position);
                startActivity(i);

            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NeedListActivity.this,"menu",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        findViewById(R.id.new_need).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NeedListActivity.this, NeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }

}




