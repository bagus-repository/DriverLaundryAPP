package laundry.driveraslijempol.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import laundry.driveraslijempol.R;
import laundry.driveraslijempol.utils.ApiConfig;
import laundry.driveraslijempol.utils.MySingleton;
import laundry.driveraslijempol.utils.SessionManager;

public class DeliveryActivity extends AppCompatActivity {

    private TextView txtNama;
    private TextView txtAlamat;
    private TextView txtTglPickup;
    private TextView txtJenisService;
    private TextView txtBerat;
    private TextView txtBiayaTambahan;
    private TextView txtKodeVoucher;
    private TextView txtTotal;
    private TextView txtPotongan;
    private TextView txtBayar;
    private TextView viewBayar;
    private TextView viewKembali;
    private EditText inputBayar;
    private Button btnBayar;

    private ProgressDialog progressDialog;
    private int bayar=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        txtNama = findViewById(R.id.txtNama);
        txtAlamat = findViewById(R.id.txtAlamat);
        txtTglPickup = findViewById(R.id.txtTglPickup);
        txtJenisService = findViewById(R.id.txtJenisService);
        txtBerat = findViewById(R.id.txtBerat);
        txtBiayaTambahan = findViewById(R.id.txtBiayaTambahan);
        txtKodeVoucher = findViewById(R.id.txtKodeVoucher);
        txtTotal = findViewById(R.id.txtTotal);
        txtPotongan = findViewById(R.id.txtPotongan);
        txtBayar = findViewById(R.id.txtBayar);
        viewBayar = findViewById(R.id.viewBayar);
        viewKembali = findViewById(R.id.viewKembali);
        btnBayar = findViewById(R.id.btnBayar);
        inputBayar = findViewById(R.id.inputBayar);

        btnBayar = findViewById(R.id.btnBayar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        Intent getIntent = getIntent();
        final int id = getIntent.getIntExtra("id", 0);
        init_toolbar();
        init_data(id);

        inputBayar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > 0){
                    viewBayar.setText("Rp. "+editable.toString());
                    int inputBayar = Integer.parseInt(editable.toString());
                    int kembali = 0;
                    if (inputBayar >= bayar){
                        kembali = inputBayar-bayar;
                    }
                    viewKembali.setText("Rp. "+String.valueOf(kembali));
                }else{
                    viewBayar.setText("Rp. 0");
                    viewKembali.setText("Rp. 0");
                }
            }
        });

        btnBayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_payment(id);
            }
        });
    }

    private void confirm_payment(final int id) {
        boolean valid = true;
        final String bayar = inputBayar.getText().toString();

        if (bayar.isEmpty()){
            inputBayar.setError("Input tidak valid");
            valid = false;
        }else {
            inputBayar.setError(null);
        }

        if (valid){
            progressDialog.setMessage("loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    ApiConfig.URL_CONFIRM_PAYMENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.hide();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            Dialog dialog = new Dialog(DeliveryActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_info);
                            dialog.setCancelable(false);

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                            dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            });
                            ((TextView) dialog.findViewById(R.id.title)).setText("Terima Kasih !");
                            ((TextView) dialog.findViewById(R.id.description)).setText("Pembayaran Diterima.");

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                        } else {
                            Toast.makeText(DeliveryActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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
                    SessionManager sessionManager = new SessionManager(DeliveryActivity.this);
                    params.put("access_token", sessionManager.getAccessToken());
                    params.put("id", String.valueOf(id));
                    params.put("bayar", bayar);

                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    private void init_data(final int id) {
        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_GET_DELIVERY_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        JSONObject order = jsonObject.getJSONObject("order");
                        Log.d("respns order", order.toString());
                        txtNama.setText(order.getString("fullname"));
                        txtAlamat.setText(order.getString("alamat"));
                        txtTglPickup.setText(order.getString("tanggal"));
                        txtJenisService.setText(order.getString("nama_service"));
                        txtBerat.setText(order.getString("berat"));

                        int biaya_tambahan = order.getInt("biaya_tambahan");
                        txtBiayaTambahan.setText("Rp. "+String.valueOf(biaya_tambahan));

                        String kode_voucher = order.getString("kode");
                        txtKodeVoucher.setText(kode_voucher);

                        int total = order.getInt("total");
                        txtTotal.setText("Rp. "+String.valueOf(total));
                        int potongan = order.getInt("potongan");
                        txtPotongan.setText("Rp. "+String.valueOf(potongan));
                        bayar = order.getInt("bayar");
                        txtBayar.setText("Rp. "+String.valueOf(bayar));
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
                HashMap<String,String> params = new HashMap<>();
                SessionManager sessionManager = new SessionManager(DeliveryActivity.this);
                params.put("access_token", sessionManager.getAccessToken());
                params.put("id", String.valueOf(id));

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void init_toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pembayaran");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }
}
