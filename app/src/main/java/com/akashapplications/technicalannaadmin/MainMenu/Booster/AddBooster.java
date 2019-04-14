package com.akashapplications.technicalannaadmin.MainMenu.Booster;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AddBooster extends Activity {

    MaterialEditText name, content;
    Spinner type;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booster);

        name = findViewById(R.id.name);
        content = findViewById(R.id.content);
        type = findViewById(R.id.spinner);

        String typeArr[] = { "Text", "Image"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typeArr);
        type.setAdapter(adapter);


        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1)
                {
                    triggerImagePicker(view);              //
                }
                else {
                    content.setEnabled(true);
                    content.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveBooster().execute();
            }
        });

        if(getIntent().hasExtra("model"))
            setDataFromIntent();
    }

    private void setDataFromIntent() {
        BoosterModel m = (BoosterModel) getIntent().getSerializableExtra("model");
        name.setText(m.getName());
        name.setEnabled(false);

        content.setText(m.getContent());
        content.setEnabled(false);

        type.setOnItemSelectedListener(null);

        if(m.getType().equalsIgnoreCase("Text"))
            type.setSelection(0);
        else
            type.setSelection(1);
        type.setEnabled(false);

        save.setVisibility(View.GONE);
    }

    private void triggerImagePicker(View v) {
//        Intent intent = new Intent(this, ImageSelectActivity.class);
//        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
//        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
//        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
//        startActivityForResult(intent, 1213);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1213);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK && data!=null) {
            String filePath = String.valueOf(data.getData());
            uploadBoosterImage(filePath);
            content.setText(filePath);
            content.setEnabled(false);
        }
        else {
            type.setSelection(0);
            content.setText("");
            content.setEnabled(true);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadBoosterImage(String image) {
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
                                content.setText(uri.toString());
                                content.setEnabled(false);
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

        LovelyProgressDialog progressDialog = new LovelyProgressDialog(AddBooster.this)
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
                reqBody.put("name",name.getText().toString());
                reqBody.put("type",type.getSelectedItem().toString());
                reqBody.put("content",content.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("checking",reqBody.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.BOOSTER_ADD, reqBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("checking",response.toString());
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(),"Booster Saved",Toast.LENGTH_SHORT).show();
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

