package com.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.schibstedspain.leku.LocationPickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Fragment;

import com.Config.BaseURL;
import com.Model.Socity_model;
import com.hmos.grocme.AppController;
import com.hmos.grocme.MainActivity;
import com.hmos.grocme.R;
import com.util.ConnectivityReceiver;
import com.util.CustomVolleyJsonArrayRequest;
import com.util.CustomVolleyJsonRequest;
import com.util.Session_management;

import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;

/**
 * Created by Rajesh Dabhi on 6/7/2017.
 */

public class Add_delivery_address_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Add_delivery_address_fragment.class.getSimpleName();

    private EditText et_phone, et_name, et_area, et_house;
    AppCompatAutoCompleteTextView et_city;
    private RelativeLayout btn_update;
    private TextView tv_phone, tv_name, tv_pin, tv_house, tv_socity, btn_socity;
    private TextView home, office, others;
    private String getsocity = "";

    private Session_management sessionManagement;

    private boolean isEdit = false;

    private String getlocation_id;

    public Add_delivery_address_fragment() {
        // Required empty public constructor
    }
    String socity_id;
    String type = "Others";
    RadioGroup radioGroup;
    String gender = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_delivery_address, container, false);

        if(getActivity() instanceof MainActivity)
            ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.add));

        home = view.findViewById(R.id.home);
        office = view.findViewById(R.id.office);
        others = view.findViewById(R.id.others);
        radioGroup = view.findViewById(R.id.gender);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.mr) {
                    gender = "Mr. ";
                } else if(checkedId == R.id.mrs) {
                    gender = "Mrs. ";
                } else if(checkedId == R.id.miss) {
                    gender = "Miss. ";
                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Home";
                home.setBackground(getResources().getDrawable(R.drawable.bg_rounded_filled));
                home.setTextColor(getResources().getColor(R.color.white));

                office.setBackground(getResources().getDrawable(R.drawable.bg_rounded_unfilled));
                others.setBackground(getResources().getDrawable(R.drawable.bg_rounded_unfilled));
                others.setTextColor(getResources().getColor(R.color.dark_gray));
                office.setTextColor(getResources().getColor(R.color.dark_gray));
            }
        });

        office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Office";
                office.setBackground(getResources().getDrawable(R.drawable.bg_rounded_filled));
                office.setTextColor(getResources().getColor(R.color.white));

                home.setBackground(getResources().getDrawable(R.drawable.bg_rounded_unfilled));
                others.setBackground(getResources().getDrawable(R.drawable.bg_rounded_unfilled));
                home.setTextColor(getResources().getColor(R.color.dark_gray));
                others.setTextColor(getResources().getColor(R.color.dark_gray));
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Others";
                others.setBackground(getResources().getDrawable(R.drawable.bg_rounded_filled));
                others.setTextColor(getResources().getColor(R.color.white));

                office.setBackground(getResources().getDrawable(R.drawable.bg_rounded_unfilled));
                home.setBackground(getResources().getDrawable(R.drawable.bg_rounded_unfilled));
                office.setTextColor(getResources().getColor(R.color.dark_gray));
                home.setTextColor(getResources().getColor(R.color.dark_gray));
            }
        });

        sessionManagement = new Session_management(getActivity());

        et_phone = (EditText) view.findViewById(R.id.et_add_adres_phone);
        et_name = (EditText) view.findViewById(R.id.et_add_adres_name);
        et_city =  view.findViewById(R.id.et_city);

        et_area = (EditText) view.findViewById(R.id.et_add_adres);
        et_house = (EditText) view.findViewById(R.id.et_add_adres_home);

        btn_update = (RelativeLayout) view.findViewById(R.id.btn_add_adres_edit);



        startActivityForResult(new LocationPickerActivity.Builder()
                .withLocation(41.4036299, 2.1743558)
                .withGeolocApiKey("AIzaSyAK3kQgqlB448DmET3wDDawnABrMJ29GOM")
                .withGooglePlacesEnabled()
                .withGoogleTimeZoneEnabled()
                .build(getActivity()), 1);

        String getsocity_name = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);
        String getsocity_id = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);

        makeGetSocityRequest();

        et_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                socity_id = socity_modelList.get(position).getSocity_id();
                String socity_name = socity_modelList.get(position).getSocity_name();

                Session_management sessionManagement = new Session_management(getActivity());

                sessionManagement.updateSocity(socity_name,socity_id);
            }
        });

        Bundle args = getArguments();

        if (args != null) {
            getlocation_id = getArguments().getString("location_id");
            String get_name = getArguments().getString("name");
            String get_phone = getArguments().getString("mobile");
            String get_pine = getArguments().getString("area");
            String get_socity_id = getArguments().getString("socity_id");
            String get_socity_name = getArguments().getString("socity_name");
            //String get_city = getArguments().getString("city");
            String get_house = getArguments().getString("house");
            String type1 = getArguments().getString("atype");



            if(get_name.substring(0,3).equals("Mr.")) {
                radioGroup.check(R.id.mr);
                get_name = get_name.replace("Mr. ","");
            }
            else if(get_name.substring(0,3).equals("Mrs")) {
                radioGroup.check(R.id.mrs);
                get_name = get_name.replace("Mrs. ","");
            }
            else if(get_name.substring(0,3).equals("Mis")) {
                radioGroup.check(R.id.miss);
                get_name = get_name.replace("Miss. ","");
            }

            if(type1 != null) {
                type = type1;
                switch (type1) {
                    case "Home":
                        home.setBackground(getResources().getDrawable(R.drawable.bg_rounded_filled));
                        home.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case "Office":
                        office.setBackground(getResources().getDrawable(R.drawable.bg_rounded_filled));
                        office.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case "Others":
                        others.setBackground(getResources().getDrawable(R.drawable.bg_rounded_filled));
                        others.setTextColor(getResources().getColor(R.color.white));
                        break;
                }
            }

            if (TextUtils.isEmpty(get_name) && get_name == null) {
                isEdit = false;
            } else {
                isEdit = true;

                Toast.makeText(getActivity(), "edit", Toast.LENGTH_SHORT).show();

                et_name.setText(get_name);
                et_phone.setText(get_phone);
                et_area.setText(get_pine);
                et_house.setText(get_house);
                et_city.setText(get_socity_name);

                sessionManagement.updateSocity(get_socity_name, get_socity_id);
            }
        }

        if (!TextUtils.isEmpty(getsocity_name)) {

            //btn_socity.setText(getsocity_name);
            sessionManagement.updateSocity(getsocity_name, getsocity_id);
        }

        btn_update.setOnClickListener(this);
//        btn_socity.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_add_adres_edit) {
            attemptEditProfile();
        }
//        } else if (id == R.id.btn_add_adres_socity) {
//
//            /*String getpincode = et_area.getText().toString();
//
//            if (!TextUtils.isEmpty(getpincode)) {*/
//
//                Bundle args = new Bundle();
//                Fragment fm = new Socity_fragment();
//                //args.putString("pincode", getpincode);
//                fm.setArguments(args);
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
//                        .addToBackStack(null).commit();
//            /*} else {
//                Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_pincode), Toast.LENGTH_SHORT).show();
//            }*/
//
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {
            if (data != null)
            et_area.setText(data.getStringExtra(LOCATION_ADDRESS));
        }
    }

    private void attemptEditProfile() {



        String getphone = et_phone.getText().toString();
        String getname = et_name.getText().toString();
        String getarea = et_area.getText().toString();
        String gethouse = et_house.getText().toString();
        String getcity = et_city.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(getphone)) {

            focusView = et_phone;
            cancel = true;
        }

        if (TextUtils.isEmpty(getname)) {
            //tv_name.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_name;
            cancel = true;
        }

        if (TextUtils.isEmpty(getarea)) {
            //tv_pin.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_area;
            cancel = true;
        }

        if (TextUtils.isEmpty(gethouse)) {
            //tv_house.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_house;
            cancel = true;
        }

        if (TextUtils.isEmpty(getsocity) && getsocity == null) {
            //tv_socity.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = btn_socity;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            if (ConnectivityReceiver.isConnected()) {

                String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

                // check internet connection
                if (ConnectivityReceiver.isConnected()) {
                    if (isEdit) {
                        makeEditAddressRequest(getlocation_id, getarea, socity_id, gethouse, gender+getname, getphone);
                    } else {
                        makeAddAddressRequest(user_id, getarea, socity_id, gethouse, gender+getname, getphone);
                    }
                }
            }
        }
    }

    private boolean isPhoneValid(String phoneno) {
        //TODO: Replace this with your own logic
        return phoneno.length() > 9;
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeAddAddressRequest(String user_id, String pincode, String city,
                                       String house_no, String receiver_name, String receiver_mobile) {

        // Tag used to cancel the request
        String tag_json_obj = "json_add_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("pincode", pincode);
        params.put("socity_id", city);
        params.put("house_no", house_no);
        params.put("receiver_name", receiver_name);
        params.put("receiver_mobile", receiver_mobile);
        params.put("type", type);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_ADDRESS_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("sas", response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {
//                        Toast.makeText(getActivity(), "jsklad", Toast.LENGTH_SHORT).show();
                        //((MainActivity) getActivity()).onBackPressed();
                        getActivity().onBackPressed();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeEditAddressRequest(String location_id, String pincode, String city,
                                        String house_no, String receiver_name, String receiver_mobile) {

        // Tag used to cancel the request
        String tag_json_obj = "json_edit_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("location_id", location_id);
        params.put("pincode", pincode);
        params.put("socity_id", city);
        params.put("house_no", house_no);
        params.put("receiver_name", receiver_name);
        params.put("receiver_mobile", receiver_mobile);
        params.put("type", type);






        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.EDIT_ADDRESS_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                Log.e("resp", response.toString());
                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("data");
                        Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();

                        ((MainActivity) getActivity()).onBackPressed();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private List<Socity_model> socity_modelList = new ArrayList<>();

    private void makeGetSocityRequest() {

        // Tag used to cancel the request
        String tag_json_obj = "json_socity_req";

        /*Map<String, String> params = new HashMap<String, String>();
        params.put("pincode", pincode);*/

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.GET,
                BaseURL.GET_SOCITY_URL, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Socity_model>>() {
                }.getType();

                socity_modelList = gson.fromJson(response.toString(), listType);

                ArrayList<String> societis = new ArrayList<>();
                for(Socity_model model : socity_modelList){
                    societis.add(model.getSocity_name());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (getActivity(), android.R.layout.select_dialog_item, societis);
                //rv_socity.setAdapter(adapter);
                et_city.setAdapter(adapter);

                if(socity_modelList.isEmpty()){
                    if(getActivity() != null) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    if(getActivity() != null) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

}
