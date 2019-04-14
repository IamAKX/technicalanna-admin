package com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam.Notifications;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akashapplications.technicalannaadmin.Models.SubjectExamModel;
import com.akashapplications.technicalannaadmin.R;
import com.akashapplications.technicalannaadmin.Utils.API;
import com.akashapplications.technicalannaadmin.Utils.RequestQueueSingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class AllNotification extends AppCompatActivity {

    ListView listView;
    ProgressBar progressBar;
    static String subject = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().hasExtra("title"))
            subject = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(subject);
        listView = findViewById(R.id.listview);
        progressBar = findViewById(R.id.progressbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), AddExamNotification.class).putExtra("subject",subject));

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetAllNotifactions().execute();

    }

    private class GetAllNotifactions extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject reqBody = new JSONObject();
            try {
                reqBody.put("subject",subject);
                reqBody.put("name","notification");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.FULL_EXAM_GET_DETAIL_BY_NAME, reqBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (progressBar.getVisibility() == View.VISIBLE)
                                progressBar.setVisibility(View.GONE);
                            ArrayList<String> titleList = new ArrayList<>();

                            if(response.has("res"))
                            {
                                try {
                                    response = response.getJSONObject("res");
                                    JSONArray arr = response.getJSONArray("notification");
                                    for(int i=0; i<arr.length(); i++)
                                    {
                                        titleList.add(arr.getString(i));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                Toast.makeText(getBaseContext(),"No notification Added",Toast.LENGTH_SHORT).show();
                            }
                            Collections.reverse(titleList);
                            ArrayAdapter adapter = new ArrayAdapter<String>(AllNotification.this,
                                    android.R.layout.simple_list_item_1, titleList);
                            listView.setAdapter(adapter);
                            registerForContextMenu(listView);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
                    NetworkResponse networkResponse = error.networkResponse;
                    Toast.makeText(getBaseContext(), "failed " + new String(networkResponse.data), Toast.LENGTH_SHORT).show();
                }
            });

            jsonObjectRequest.setShouldCache(false);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            RequestQueue requestQueue = RequestQueueSingleton.getInstance(getBaseContext())
                    .getRequestQueue();
            requestQueue.getCache().clear();
            requestQueue.add(jsonObjectRequest);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

    }

}


