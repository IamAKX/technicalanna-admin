package com.akashapplications.technicalannaadmin.MainMenu.Booster;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.akashapplications.technicalannaadmin.Models.BoosterModel;
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

public class Booster extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    ProgressBar progressBar;
    ArrayList<BoosterModel> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booster);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.listview);
        progressBar = findViewById(R.id.progressbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), AddBooster.class));
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetAllBoosters().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(getBaseContext(),AddBooster.class).putExtra("model",list.get(position)));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listview) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.delete, menu);
        }
    }

    public boolean onContextItemSelected(MenuItem item){
        if(item.getItemId() == R.id.delete)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            new DeleteBooster(info.position).execute();

        }
        return true;
    }

    private class GetAllBoosters extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            final JSONObject reqBody = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.ALL_BOOSTER, reqBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (progressBar.getVisibility() == View.VISIBLE)
                                progressBar.setVisibility(View.GONE);
                            ArrayList<String> titleList = new ArrayList<>();
                            Log.e("checking", response.toString());
                            try {
                                JSONArray arr = response.getJSONArray("boosterList");
                                if(arr.length() == 0)
                                    Toast.makeText(getBaseContext(),"No Booster added", Toast.LENGTH_SHORT).show();
                                list.clear();
                                for (int i=0; i<arr.length(); i++)
                                {
                                    BoosterModel m = new BoosterModel();
                                    m.setId(arr.getJSONObject(i).getString("id"));
                                    m.setName(arr.getJSONObject(i).getString("name"));
                                    m.setContent(arr.getJSONObject(i).getString("content"));
                                    m.setType(arr.getJSONObject(i).getString("type"));
                                    list.add(m);
                                    titleList.add(m.getName());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ArrayAdapter adapter = new ArrayAdapter<String>(Booster.this,
                                    android.R.layout.simple_list_item_1, titleList);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(Booster.this);
                            registerForContextMenu(listView);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    private class DeleteBooster extends AsyncTask<Void,Void,Void>{
        int index;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        public DeleteBooster(int index) {
            this.index = index;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject reqBody = new JSONObject();
            try {
                reqBody.put("id",list.get(index).getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.BOOSTER_DELETE, reqBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (progressBar.getVisibility() == View.VISIBLE)
                                progressBar.setVisibility(View.GONE);
                            Log.e("checking",response.toString());
                            Toast.makeText(getBaseContext(),list.get(index).getName()+" Deleted successfully",Toast.LENGTH_SHORT).show();
                            list.remove(index);
                            new GetAllBoosters().execute();


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
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
}
