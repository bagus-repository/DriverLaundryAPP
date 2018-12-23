package laundry.driveraslijempol.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import laundry.driveraslijempol.R;
import laundry.driveraslijempol.adapter.OrderListAdapter;
import laundry.driveraslijempol.model.OrderItems;
import laundry.driveraslijempol.utils.ApiConfig;
import laundry.driveraslijempol.utils.MySingleton;
import laundry.driveraslijempol.utils.SessionManager;

public class PickupActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText inputBerat;
    private Button btnProses;
    private List<OrderItems> orderItemsList;
    private OrderListAdapter mAdapter;
    private Integer jmlData;

    private ProgressDialog progressDialog;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ubah Berat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputBerat = findViewById(R.id.inputBerat);
        btnProses = findViewById(R.id.btnProses);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        Intent getIntent = getIntent();
        final int id = getIntent.getIntExtra("id", 0);
        sessionManager = new SessionManager(PickupActivity.this);

        init_wash_list(id);

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_details(id);
            }
        });
    }

    private void init_wash_list(final int id) {
        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_GET_CUST_WASH_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                Log.d("p rsn", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        JSONArray items = jsonObject.getJSONArray("items");
                        orderItemsList = new ArrayList<>();
                        jmlData = items.length();
                        Integer[] qty = new Integer[jmlData];
                        String[] name = new String[jmlData];
                        String[] tipes = new String[jmlData];

                        for (int i = 0; i < items.length(); i++) {
                            name[i] = items.getJSONObject(i).getString("item_title");
                            tipes[i] = items.getJSONObject(i).getString("item_type");
                            qty[i] = items.getJSONObject(i).getInt("item_qty");

                            OrderItems o = new OrderItems();
                            o.itemTitle = items.getJSONObject(i).getString("item_title");
                            o.itemDesc = items.getJSONObject(i).getString("item_desc");
                            o.imageUrl = items.getJSONObject(i).getString("image_url");
                            o.itemType = items.getJSONObject(i).getString("item_type");
                            o.itemQty = items.getJSONObject(i).getInt("item_qty");
                            o.section = false;
                            orderItemsList.add(o);
                        }

                        MySingleton.getInstance(getApplicationContext()).setPakaianOrder(name);
                        Log.d("name", Arrays.toString(name));
                        MySingleton.getInstance(getApplicationContext()).setJmlpakaianOrder(qty);
                        MySingleton.getInstance(getApplicationContext()).setTipepakaianOrder(tipes);

                        String tmpType = "";
                        List<Integer> index_section = new ArrayList<>(items.length());
                        int j = 0;
                        for (int i=0;i<items.length();i++){
                            String tipe = items.getJSONObject(i).getString("item_type");
                            if(!tipe.equals(tmpType)){
                                index_section.add(i+j);
                                j++;
                            }
                            tmpType = tipe;
                        }
                        for (int i=0;i<index_section.size();i++){
                            String tipe = items.getJSONObject(index_section.get(i)).getString("item_type");
                            orderItemsList.add(index_section.get(i), new OrderItems(tipe.toUpperCase(), true));
                        }
                        mAdapter = new OrderListAdapter(PickupActivity.this, getApplicationContext(), orderItemsList, jmlData);
                        recyclerView.setAdapter(mAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", sessionManager.getAccessToken());
                params.put("id", String.valueOf(id));

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void update_details(final int id) {
        boolean valid = true;
        String berat_str = inputBerat.getText().toString();
        if(berat_str.equals("")){
            berat_str = "0";
        }
        final double berat = Double.valueOf(berat_str);

        boolean validitems = false;
        Integer[] cekItemVal = MySingleton.getInstance(getApplicationContext()).getJmlpakaianOrder();
        for (int i=0;i<cekItemVal.length;i++){
            Log.d("cekItem", cekItemVal[i].toString());
            if(cekItemVal[i] >0){
                validitems = true;
            }
        }
        if (validitems == false){
            Toast.makeText(this, "Oops.. Minimal 1 item", Toast.LENGTH_SHORT).show();
        }
        if (valid && validitems){
            progressDialog.setMessage("loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    ApiConfig.URL_ADD_PICKUP_DETAILS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.hide();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PickupActivity.this);
                            builder.setMessage("Berhasil update pesanan");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            builder.show();
                        } else {
                            Toast.makeText(PickupActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.hide();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("access_token", sessionManager.getAccessToken());
                    params.put("id", String.valueOf(id));
                    params.put("berat", String.valueOf(berat));

                    String[] s_pakaian = MySingleton.getInstance(getApplicationContext()).getPakaianOrder();
                    Integer[] s_qty = MySingleton.getInstance(getApplicationContext()).getJmlpakaianOrder();
                    JSONObject jsonPakaian = new JSONObject();
                    JSONObject jsonQty = new JSONObject();
                    int sign = 0;
                    for (int i=0;i<s_pakaian.length;i++){
                        if (s_qty[i] != 0){
                            try {
                                jsonQty.put("qty_"+sign, s_qty[i]);
                                jsonPakaian.put("pakaian_"+sign, s_pakaian[i]);
                                sign++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    params.put("pakaian", jsonPakaian.toString());
                    params.put("qty", jsonQty.toString());
                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}
