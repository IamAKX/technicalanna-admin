package com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam.Syllabus;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class AddSyllabus extends AppCompatActivity {
    String subject = "";
    MaterialEditText text;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_syllabus);
        text = findViewById(R.id.text);
        progressBar = findViewById(R.id.progressbar);
        subject = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(subject);
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
                new SaveData().execute();
                break;

        }
        return true;
    }



    private class SaveData extends AsyncTask<Void,Void,Void> {

        LovelyProgressDialog progressDialog = new LovelyProgressDialog(AddSyllabus.this)
                .setIcon(android.R.drawable.ic_menu_save)
                .setTitle("Saving")
                .setCancelable(false)
                .setTopColorRes(R.color.colorPrimary);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject reqBody = new JSONObject();
            try {
                reqBody.put("subject",subject);
                reqBody.put("name","syllabus");
                reqBody.put("syllabus",text.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("checking",reqBody.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.FULL_EXAM_ADD_SYLLABUS, reqBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("checking",response.toString());
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),"Exam Saved",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    NetworkResponse networkResponse = error.networkResponse;
                    Toast.makeText(getBaseContext(), "Registration failed " + new String(networkResponse.data), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetSyllabus().execute();
    }

    private class GetSyllabus extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject reqBody = new JSONObject();
            try {
                reqBody.put("subject",subject);
                reqBody.put("name","syllabus");
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
                                    text.setText(response.getString("syllabus"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

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
