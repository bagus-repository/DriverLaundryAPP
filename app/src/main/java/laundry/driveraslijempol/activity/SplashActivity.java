package laundry.driveraslijempol.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import laundry.driveraslijempol.BuildConfig;
import laundry.driveraslijempol.R;
import laundry.driveraslijempol.utils.ApiConfig;
import laundry.driveraslijempol.utils.MySingleton;
import laundry.driveraslijempol.utils.NetworkChecker;

public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    @Override
    protected void onStart() {
        super.onStart();
        NetworkChecker networkChecker = new NetworkChecker();
        if (!networkChecker.isConnected(getApplicationContext())) {
            showNetworkDialog(getResources().getDrawable(R.drawable.ic_cloud_off), "No Internet!", "Check your internet guys...", "Exit", "exit");
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    ApiConfig.URL_CEK_APP_UPDATE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Cek Update", "Response update: " + response.toString());

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showNetworkDialog(getResources().getDrawable(R.drawable.ic_apps), "App Update Ready!", "Please update your app we make some changes :)", "Update", "update");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Connection error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Cek Update", "Login Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    String ver = String.valueOf(BuildConfig.VERSION_CODE);
                    params.put("ver", ver);

                    return params;
                }
            };

            MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    private void showNetworkDialog(Drawable icon, String title, String content, String bt_text, final String bt_trigger) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_warning);

        ImageView this_icon = dialog.findViewById(R.id.icon);
        this_icon.setImageDrawable(icon);

        TextView this_title = dialog.findViewById(R.id.title);
        this_title.setText(title);

        TextView this_content = dialog.findViewById(R.id.content);
        this_content.setText(content);

        Button this_button = dialog.findViewById(R.id.bt_close);
        this_button.setText(bt_text);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        this_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(bt_trigger.equals("exit")){
                    finish();
                }else if (bt_trigger.equals("update")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID));
                    startActivity(intent);
                    finish();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
