package com.akashapplications.technicalannaadmin.MainMenu.ToppersTip;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akashapplications.technicalannaadmin.Models.ExamNotificationModel;
import com.akashapplications.technicalannaadmin.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToppersTip extends AppCompatActivity {
    LinearLayout linearLayout;
    ArrayList<Map<String,String>> urlList = new ArrayList<>();
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

        setDatabaseListener();
    }

    private void setDatabaseListener() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Config/TopperTips");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                linearLayout.removeAllViews();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    View item = getLayoutInflater().inflate(R.layout.toppers_tip_item, null);
                    ImageView iv = item.findViewById(R.id.image);
                    TextView tv = item.findViewById(R.id.text);
                    tv.setText(ds.child("text").getValue(String.class));
                    Glide.with(getBaseContext())
                            .load(ds.child("link").getValue(String.class))
                            .into(iv);
                    linearLayout.addView(item);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                linearLayout.removeAllViews();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    View item = getLayoutInflater().inflate(R.layout.toppers_tip_item, null);
                    ImageView iv = item.findViewById(R.id.image);
                    TextView tv = item.findViewById(R.id.text);
                    tv.setText(ds.child("text").getValue(String.class));
                    Glide.with(getBaseContext())
                            .load(ds.child("link").getValue(String.class))
                            .into(iv);
                    linearLayout.addView(item);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                linearLayout.removeAllViews();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    View item = getLayoutInflater().inflate(R.layout.toppers_tip_item, null);
                    ImageView iv = item.findViewById(R.id.image);
                    TextView tv = item.findViewById(R.id.text);
                    tv.setText(ds.child("text").getValue(String.class));
                    Glide.with(getBaseContext())
                            .load(ds.child("link").getValue(String.class))
                            .into(iv);
                    linearLayout.addView(item);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                linearLayout.removeAllViews();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    View item = getLayoutInflater().inflate(R.layout.toppers_tip_item, null);
                    ImageView iv = item.findViewById(R.id.image);
                    TextView tv = item.findViewById(R.id.text);
                    tv.setText(ds.child("text").getValue(String.class));
                    Glide.with(getBaseContext())
                            .load(ds.child("link").getValue(String.class))
                            .into(iv);
                    linearLayout.addView(item);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void triggerAlertPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Add Topper Tip");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_topper_tip, null);
        final EditText image = dialogView.findViewById(R.id.image);
        final EditText text = dialogView.findViewById(R.id.text);
        alert.setView(dialogView);
        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Map<String,String> map = new HashMap<>();
                map.put("link",image.getText().toString());
                map.put("text",text.getText().toString());
                urlList.add(map);
                dialog.dismiss();
                Toast.makeText(getBaseContext(),"Items added : "+urlList.size(),Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();
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
