package laundry.driveraslijempol.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import laundry.driveraslijempol.R;
import laundry.driveraslijempol.adapter.AdapterListPickup;
import laundry.driveraslijempol.model.Pickup;
import laundry.driveraslijempol.utils.ApiConfig;
import laundry.driveraslijempol.utils.MySingleton;
import laundry.driveraslijempol.utils.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PickupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PickupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    private AdapterListPickup mAdapter;
    private LinearLayout linearLayout;
    private ImageView btnRefresh;

    public PickupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PickupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickupFragment newInstance(String param1, String param2) {
        PickupFragment fragment = new PickupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_pickup, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        linearLayout = rootView.findViewById(R.id.main_content);

        btnRefresh = rootView.findViewById(R.id.refreshFrag);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(PickupFragment.this).attach(PickupFragment.this).commit();
            }
        });

        init_pickup_data();
        return rootView;
    }

    private void init_pickup_data() {
        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_PICKUP_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        JSONArray pickups = jsonObject.getJSONArray("pickup_list");
                        if (pickups.length() > 0) {
                            List<Pickup> items = new ArrayList<>();
                            for (int i = 0; i < pickups.length(); i++) {
                                Pickup item = new Pickup();
                                item.id_order = pickups.getJSONObject(i).getInt("id_order");
                                item.nama_pelanggan = pickups.getJSONObject(i).getString("nama_pelanggan");
                                item.tgl_pickup = pickups.getJSONObject(i).getString("tgl_pickup");
                                item.alamat = pickups.getJSONObject(i).getString("alamat");
                                item.jam_pickup= pickups.getJSONObject(i).getString("jam_pickup");
                                item.lat = pickups.getJSONObject(i).getDouble("lat");
                                item.lng= pickups.getJSONObject(i).getDouble("lng");
                                items.add(item);
                            }

                            mAdapter = new AdapterListPickup(getContext(), items);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        recyclerView.setVisibility(View.INVISIBLE);
                        Snackbar.make(linearLayout, jsonObject.getString("msg"), Snackbar.LENGTH_LONG).show();
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
                SessionManager sessionManager = new SessionManager(getContext());
                params.put("access_token",sessionManager.getAccessToken());

                return params;
            }

            @Override
            public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
                retryPolicy = new DefaultRetryPolicy(
                        15000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                return super.setRetryPolicy(retryPolicy);
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        init_pickup_data();
    }

    @Override
    public void onDestroyView() {
        progressDialog.dismiss();
        super.onDestroyView();
    }
}
