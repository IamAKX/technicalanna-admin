package com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam.Exams;

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

import com.akashapplications.technicalannaadmin.MainMenu.SubjectExam.AddExam;
import com.akashapplications.technicalannaadmin.Models.QuizModel;
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

public class AllExams extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    ProgressBar progressBar;
    static String subject = "null";
    ArrayList<SubjectExamModel> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exams);
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
                startActivity(new Intent(getBaseContext(), AddFullLengthExam.class).putExtra("subject",subject));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetAllExams().execute();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getBaseContext(),"Quiz can be seen in client app only", Toast.LENGTH_SHORT).show();
    }

    private class GetAllExams extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject reqBody = new JSONObject();
            try {
                reqBody.put("subject",subject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.ALL_FULL_EXAM, reqBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (progressBar.getVisibility() == View.VISIBLE)
                                progressBar.setVisibility(View.GONE);
                            ArrayList<String> titleList = new ArrayList<>();
                            Log.e("checking", response.toString());
                            try {
                                JSONArray arr = response.getJSONArray("examList");
                                if(arr.length() == 0)
                                    Toast.makeText(getBaseContext(),"No Exams added", Toast.LENGTH_SHORT).show();
                                list.clear();
                                for (int i=0; i<arr.length(); i++)
                                {
                                    SubjectExamModel m = new SubjectExamModel();
                                    JSONObject object = arr.getJSONObject(i);

                                    m.setFees(0);
                                    m.setTimeAlloted(object.getInt("time_alloted"));
                                    m.setSubject(object.getString("subject"));
                                    m.setFullMarks(0);
                                    m.setName(object.getString("name"));
                                    m.setId(object.getString("id"));

                                    ArrayList<QuizModel> qm = new ArrayList<>();

                                    JSONArray qArr = object.getJSONArray("quiz");
                                    for (int j=0; j<qArr.length(); j++)
                                    {
                                        QuizModel qmodel = new QuizModel();
                                        JSONObject qObject = qArr.getJSONObject(j);
                                        qmodel.setQuestion(qObject.getString("question"));
                                        JSONArray ans = qObject.getJSONArray("answer");
                                        qmodel.setOption1(ans.getString(0));
                                        qmodel.setOption2(ans.getString(1));
                                        qmodel.setOption3(ans.getString(2));
                                        qmodel.setOption4(ans.getString(3));
                                        qmodel.setCorrectOption(qObject.getInt("correct"));

                                        qm.add(qmodel);
                                    }

                                    m.setQuizList(qm);
                                    m.setRegisteredUser(new ArrayList<String>());

                                    list.add(m);
                                    titleList.add(m.getName());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ArrayAdapter adapter = new ArrayAdapter<String>(AllExams.this,
                                    android.R.layout.simple_list_item_1, titleList);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(AllExams.this);
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
            new DeleteExam(info.position).execute();

        }
        return true;
    }

    private class DeleteExam extends AsyncTask<Void,Void,Void>{
        int index;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        public DeleteExam(int index) {
            this.index = index;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject reqBody = new JSONObject();
            try {
                reqBody.put("subject",list.get(index).getSubject());
                reqBody.put("name",list.get(index).getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.FULL_EXAM_DELETE, reqBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (progressBar.getVisibility() == View.VISIBLE)
                                progressBar.setVisibility(View.GONE);
                            Log.e("checking",response.toString());
                            Toast.makeText(getBaseContext(),list.get(index).getName()+" Deleted successfully",Toast.LENGTH_SHORT).show();
                            list.remove(index);
                            new GetAllExams().execute();


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

