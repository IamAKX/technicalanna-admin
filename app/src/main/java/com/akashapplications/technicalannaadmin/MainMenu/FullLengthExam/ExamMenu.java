package com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam.Exams.AllExams;
import com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam.Notifications.AllNotification;
import com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam.PrevQuesPaper.AllPrevQuesPaper;
import com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam.Syllabus.AddSyllabus;
import com.akashapplications.technicalannaadmin.R;
import com.akashapplications.technicalannaadmin.Utils.Subjects;

import java.util.ArrayList;

public class ExamMenu extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listview;

    String subject="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_menu);

        if(getIntent().hasExtra("title"))
            subject = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(subject);
        listview = findViewById(R.id.listView);

        String[] items = {"Notification","Syllabus","Previous year questions","Full Length exam"};

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position)
        {
            case 0:
                startActivity(new Intent(getBaseContext(), AllNotification.class).putExtra("title",subject));
                break;
            case 1:
                startActivity(new Intent(getBaseContext(), AddSyllabus.class).putExtra("title",subject));
                break;
            case 2:
                startActivity(new Intent(getBaseContext(), AllPrevQuesPaper.class).putExtra("title",subject));
                break;
            case 3:
                startActivity(new Intent(getBaseContext(), AllExams.class).putExtra("title",subject));

                break;
        }
    }
}
