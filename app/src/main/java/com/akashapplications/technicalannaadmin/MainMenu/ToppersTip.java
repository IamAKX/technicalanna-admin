package com.akashapplications.technicalannaadmin.MainMenu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akashapplications.technicalannaadmin.Models.ExamNotificationModel;
import com.akashapplications.technicalannaadmin.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToppersTip extends AppCompatActivity {
    LinearLayout linearLayout;
    ArrayList<String> urlList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toppers_tip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linearLayout = findViewById(R.id.linearlayout);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerAlertPopup();
            }
        });
    }

    private void triggerAlertPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(getApplicationContext());
        alert.setMessage("Enter Image Banner Link");
        alert.setView(edittext);
        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               addToList(edittext.getText().toString());
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void addToList(String link) {
        View item = getLayoutInflater().inflate(R.layout.toppers_tip_item, null);
        ImageView iv = item.findViewById(R.id.image);
        TextView tv = item.findViewById(R.id.text);
        tv.setText(link);
        Glide.with(getBaseContext())
                .load(link)
                .into(iv);
        linearLayout.addView(item);
        urlList.add(link);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.save:
                saveNotification();
                break;

        }
        return true;
    }

    private void saveNotification() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Config/TopperTips");

        Map<String,Object> map = new HashMap<>();
        map.put("image_list",urlList);
        ref.updateChildren(map);

    }

}
