package com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.akashapplications.technicalannaadmin.R;
import com.akashapplications.technicalannaadmin.Utils.Subjects;

public class AllExamList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exam_list);
        getSupportActionBar().setTitle("All Exams");

        listview = findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Subjects.getAllExamsList());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(getBaseContext(), ExamMenu.class).putExtra("title",listview.getItemAtPosition(position).toString()));

    }
}
