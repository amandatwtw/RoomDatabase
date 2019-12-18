package com.example.roomdatabase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.roomdatabase.R;
import com.example.roomdatabase.adapter.RecycleAdapter;
import com.example.roomdatabase.room.AppDatabase;
import com.example.roomdatabase.room.Mahasiswa;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.roomdatabase.AppApplication.db;

public class DetailActivity extends AppCompatActivity {

    RecyclerView myRecyclerview;
    FloatingActionButton myFab;
    RecycleAdapter recycleAdapter;
    List<Mahasiswa> listMahasiswas = new ArrayList<>();

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static String url_edit       = Server.URL + "edit.php";
    private static String url_delete     = Server.URL + "delete.php";

    public static final String TAG_Nama       = "nama";
    public static final String TAG_Nim    = "nim";
    public static final String TAG_Kejuruan  = "kejuruan";
    private static final String TAG_Alamat = "alamat";

    String tag_json_obj = "json_obj_req";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        myRecyclerview = findViewById(R.id.myRecyclerview);
        myFab = findViewById(R.id.myFab);

        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });

        fetchDataFromRoom();
        initRecyclerView();
        setAdapter();
    }

    private void fetchDataFromRoom() {
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "mahasiswa").allowMainThreadQueries().build();
        listMahasiswas = db.userDao().getAll();

        //just checking data from db
        for (int i = 0; i < listMahasiswas.size(); i++) {
            Log.e("Aplikasi", listMahasiswas.get(i).getAlamat() + i);
            Log.e("Aplikasi", listMahasiswas.get(i).getKejuruan() + i);
            Log.e("Aplikasi", listMahasiswas.get(i).getNama() + i);
            Log.e("Aplikasi", listMahasiswas.get(i).getNim() + i);
        }
        Log.e("cek list", "" + listMahasiswas.size());
    }
    private void edit(final String idx){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_edit, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("get edit data", jObj.toString());
                        String idx      = jObj.getString(TAG_ID);
                        String namax    = jObj.getString(TAG_NAMA);
                        String alamatx  = jObj.getString(TAG_ALAMAT);

                        DialogForm(idx, namax, alamatx, "UPDATE");

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", idx);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void initRecyclerView() {
        myRecyclerview.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerview.setLayoutManager(llm);
        recycleAdapter = new RecycleAdapter(this, listMahasiswas);

    }

    private void setAdapter() {
        myRecyclerview.setAdapter(recycleAdapter);
    }
}
