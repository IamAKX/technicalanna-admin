package com.akashapplications.technicalannaadmin.MainMenu;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akashapplications.technicalannaadmin.Models.ExamNotificationModel;
import com.akashapplications.technicalannaadmin.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

public class ExamNotificationBanner extends AppCompatActivity {

    MaterialEditText text, link;
    TextView notificationMarquee;
    String lauchingURI = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_notification_banner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        text = findViewById(R.id.text);
        link = findViewById(R.id.link);
        notificationMarquee = findViewById(R.id.marqueeExamNotification);
        notificationMarquee.setSelected(true);

        fetchNotification();

        notificationMarquee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri webpage = Uri.parse(lauchingURI);
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getBaseContext(), "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetchNotification() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("Config/ExamNotification");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                notificationMarquee.setText((CharSequence) dataSnapshot.child("text").getValue());
                lauchingURI = String.valueOf(dataSnapshot.child("link").getValue());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                notificationMarquee.setText((CharSequence) dataSnapshot.child("text").getValue());
                lauchingURI = String.valueOf(dataSnapshot.child("link").getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
        DatabaseReference ref = database.getReference("Config/ExamNotification");
        ExamNotificationModel model = new ExamNotificationModel();
        model.setLink(link.getText().toString());
        model.setText(text.getText().toString());

        Map<String,Object> map = new HashMap<>();
        map.put("value",model);
        ref.updateChildren(map);

        link.setText("");
        text.setText("");
    }

}
