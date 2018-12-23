package laundry.driveraslijempol.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import laundry.driveraslijempol.R;
import laundry.driveraslijempol.utils.ApiConfig;
import laundry.driveraslijempol.utils.MySingleton;
import laundry.driveraslijempol.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText inputUsername;
    private EditText inputPassword;
    private Button btnLoginButton;
    LinearLayout linearLayout;
    private ProgressDialog progressDialog;

    SessionManager sessionManager;
    private final static String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isLoggedIn()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

//        Tools.setSystemBarColor(this, android.R.color.white);
//        Tools.setSystemBarLight(this);

        inputUsername= findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        btnLoginButton = findViewById(R.id.btnLoginButton);
        linearLayout = findViewById(R.id.linearLayout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        btnLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                String email = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();

                if(email.isEmpty()){
                    inputUsername.setError("Username tidak valid");
                    valid = false;
                }else {
                    inputUsername.setError(null);
                }

                if (password.isEmpty() || password.length() < 5 || password.length() > 12) {
                    inputPassword.setError("Password min 5 sampai 12 karakter");
                    valid = false;
                }else{
                    inputPassword.setError(null);
                }

                if (valid){
                    checkLogin(email,password);
                }else {
                    Snackbar.make(linearLayout, "Check your input again guys...", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkLogin(final String username, final String password) {
        btnLoginButton.setEnabled(false);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Logging in ...");
        progressDialog.setInverseBackgroundForced(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login response: " + response.toString());
                progressDialog.hide();

                btnLoginButton.setEnabled(true);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        sessionManager.setIsLogin(true);
                        String token = jsonObject.getString("access_token");
                        String fullname = jsonObject.getString("fullname");
                        String nohp = jsonObject.getString("no_hp");
                        String os_player_id = jsonObject.getString("os_player_id");
                        if (sessionManager.getOsPlayerId()!= null && sessionManager.getOsPlayerId() != os_player_id){
                            OneSignal.setSubscription(true);
                            update_os_player_id(sessionManager.getOsPlayerId(), token);
                        }
                        sessionManager.setAccessToken(token);
                        sessionManager.setFullname(fullname);
                        sessionManager.setNohp(nohp);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Connection error...", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Login Error: " + error.getMessage());
                progressDialog.hide();
                btnLoginButton.setEnabled(true);
            }
        }){

            @Override
            protected Map<String, String > getParams(){

                Map<String, String > params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void update_os_player_id(final String osPlayerId, final String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_UPDATE_OS_PLAYER_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("OSID", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error){
                        Log.d("OSID", "Player id updated");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("os_player_id", osPlayerId);
                params.put("access_token", token);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }
}
