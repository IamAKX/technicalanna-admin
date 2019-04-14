package com.akashapplications.technicalannaadmin.MainMenu.FullLengthExam.PrevQuesPaper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akashapplications.technicalannaadmin.R;
import com.akashapplications.technicalannaadmin.Utils.API;
import com.akashapplications.technicalannaadmin.Utils.GetLast30years;
import com.akashapplications.technicalannaadmin.Utils.RequestQueueSingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.applandeo.FilePicker;
import com.applandeo.constants.FileType;
import com.applandeo.listeners.OnSelectFileListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class AddPrevQuesPaper extends Activity {
    String subject = "";
    Spinner spinner;
    Button button;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prev_ques_paper);

        spinner = findViewById(R.id.spinner);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textview);
        subject = getIntent().getStringExtra("subject");
//        getSupportActionBar().setTitle(subject);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, GetLast30years.getYears());
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerFileChooser();
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveBooster().execute();

            }
        });
    }

    private void triggerFileChooser() {
        Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
        intentPDF.setType("application/pdf");
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intentPDF,"ChooseFile"), 123);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123 && resultCode == Activity.RESULT_OK && data!=null) {
            String filePath = String.valueOf(data.getData());
            textView.setText(filePath);
            uploadQPaper(filePath);
        }
        else {
            
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadQPaper(String image) {
        if (image == "")
            return;

        final LovelyProgressDialog progressDialog = new LovelyProgressDialog(this)
                .setIcon(R.drawable.upload)
                .setTitle("Uploading your booster image")
                .setCancelable(false)
                .setTopColorRes(R.color.colorPrimary);

        progressDialog.show();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();


        final StorageReference profRef = mStorageRef.child("booster_images/" +System.currentTimeMillis());
        profRef.putFile(Uri.parse(image))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                textView.setText(uri.toString());

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });

                        progressDialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads

                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
//                        progressDialog.setMessage((int)progress + " of the upload is complete");
                        if(progress >= 100)
                            Toast.makeText(getBaseContext(), "Profile image uploaded", Toast.LENGTH_SHORT).show();

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
                new SaveBooster().execute();
                break;

        }
        return true;
    }

    private class SaveBooster extends AsyncTask<Void,Void,Void> {

        LovelyProgressDialog progressDialog = new LovelyProgressDialog(AddPrevQuesPaper.this)
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
                reqBody.put("name","previousQuestionPaper");
                JSONObject newObj = new JSONObject();
                newObj.put("year", Integer.parseInt(spinner.getSelectedItem().toString()));
                newObj.put("link", textView.getText().toString());
                reqBody.put("previousQuestionPaper",newObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("checking",reqBody.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.FULL_EXAM_ADD_PREV_QUES_PAPER, reqBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("checking",response.toString());
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),"Q Paper Saved",Toast.LENGTH_SHORT).show();
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


