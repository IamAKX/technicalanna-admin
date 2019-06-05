package com.akashapplications.technicalannaadmin.MainMenu.SubjectExam;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akashapplications.technicalannaadmin.Models.QuizModel;
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

public class AddExam extends AppCompatActivity {
    String subject = "";
    MaterialEditText name, fullMarks, time, fees, question, option1, option2, option3, option4, correctOption,solution, negmark;
    Button add;
    TextView questionCount;

    JSONObject reqBody = new JSONObject();
    JSONArray quizArray = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        subject = getIntent().getStringExtra("subject");
        getSupportActionBar().setTitle(subject);

        name = findViewById(R.id.name);
        fullMarks = findViewById(R.id.marks);
        time = findViewById(R.id.time);
        fees = findViewById(R.id.fees);
        question = findViewById(R.id.question);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        negmark = findViewById(R.id.negmark);
        solution = findViewById(R.id.soln);
        correctOption = findViewById(R.id.correctoption);
        add = findViewById(R.id.add);
        questionCount = findViewById(R.id.questionNo);
        questionCount.setText("Question Count : 0");

        fees.setText("0");
        fees.setEnabled(true);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToQuiz();
            }
        });
    }

    private void addToQuiz() {
        JSONObject object = new JSONObject();
        try {
            object.put("question",question.getText().toString());
            JSONArray arr = new JSONArray();
            arr.put(0,option1.getText().toString());
            arr.put(1,option2.getText().toString());
            arr.put(2,option3.getText().toString());
            arr.put(3,option4.getText().toString());
            object.put("answer",arr);
            object.put("solution",solution.getText().toString());
            object.put("correct", Integer.parseInt(correctOption.getText().toString()));
            quizArray.put(object);
            questionCount.setText("Question Count : "+quizArray.length());
            question.setText("");
            option1.setText("");
            option2.setText("");
            option3.setText("");
            option4.setText("");
            solution.setText("");
            correctOption.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                new SaveSubjectExam().execute();
                break;

        }
        return true;
    }

    private class SaveSubjectExam extends AsyncTask<Void,Void,Void> {

        LovelyProgressDialog progressDialog = new LovelyProgressDialog(AddExam.this)
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
            reqBody = new JSONObject();
            try {
                reqBody.put("subject",subject);
                reqBody.put("name",name.getText().toString());
                reqBody.put("time_alloted",Integer.parseInt(time.getText().toString()));
                reqBody.put("full_marks",Integer.parseInt(fullMarks.getText().toString()));
                reqBody.put("quiz",quizArray);
                reqBody.put("negmark",Double.parseDouble(negmark.getText().toString()));
                reqBody.put("fees",Integer.parseInt(fees.getText().toString()));
                reqBody.put("registered_user",new JSONArray());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("checking",reqBody.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.SUBJECT_EXAM_ADD, reqBody,
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
}
