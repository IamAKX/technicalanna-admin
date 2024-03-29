package com.akashapplications.technicalannaadmin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.akashapplications.technicalannaadmin.MainMenu.Booster.Booster;
import com.akashapplications.technicalannaadmin.MainMenu.ExamBanner.ExamNotificationBanner;
import com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam.AllExamList;
import com.akashapplications.technicalannaadmin.MainMenu.SubjectExam.AllSubjectList;
import com.akashapplications.technicalannaadmin.MainMenu.ToppersTip.ToppersTip;
import com.akashapplications.technicalannaadmin.Utils.MainMenuItem;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listview;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getBaseContext();

        listview = findViewById(R.id.listview);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, MainMenuItem.getMainMenuItem());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position)
        {
            case 0:
                startActivity(new Intent(context, ExamNotificationBanner.class).putExtra("title",listview.getItemAtPosition(position).toString()));
                break;

            case 1:
                startActivity(new Intent(context, ToppersTip.class).putExtra("title",listview.getItemAtPosition(position).toString()));
                break;

            case 2:
                startActivity(new Intent(context, Booster.class).putExtra("title",listview.getItemAtPosition(position).toString()));
                break;

            case 3:
                startActivity(new Intent(context, AllSubjectList.class).putExtra("title",listview.getItemAtPosition(position).toString()));
                break;

            case 4:
                startActivity(new Intent(context, AllExamList.class).putExtra("title",listview.getItemAtPosition(position).toString()));
                break;
        }
    }
}
