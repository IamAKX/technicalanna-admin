package com.akashapplications.technicalannaadmin.MainMenu.SubjectExam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.akashapplications.technicalannaadmin.R;
import com.akashapplications.technicalannaadmin.Utils.MainMenuItem;
import com.akashapplications.technicalannaadmin.Utils.Subjects;

public class AllSubjectList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_subject_list);
        listview = findViewById(R.id.listView);
        getSupportActionBar().setTitle("All Subjects");
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Subjects.getAllSubject());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(getBaseContext(), SubjectExam.class).putExtra("title",listview.getItemAtPosition(position).toString()));

    }
}
