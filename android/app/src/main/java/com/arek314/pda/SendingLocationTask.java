package com.arek314.pda;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SendingLocationTask extends AsyncTask {
    private static int USER_ID;
    private static String USER_NAME;
    private final Handler delayHandler;

    private RequestQueue requestQueue;
    private Context context;
    private int delayTime;
    private boolean workingState;
    private String peoplePutUrl;
    private SharedPreferences sharedPreferences;
    private LocationManager locationManager;

    SendingLocationTask(Context context, int delayTime, int userId, String userName, LocationManager locationManager) {
        this.context = context;
        this.delayTime = delayTime;
        USER_ID = userId;
        USER_NAME = userName;
        this.locationManager = locationManager;

        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.preferences_name), Context.MODE_PRIVATE);


        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(context.getResources().getString(R.string.main_URI_scheme))
                .authority(context.getResources().getString(R.string.main_URI))
                .appendPath(context.getResources().getString(R.string.people_append_path));
        peoplePutUrl = uriBuilder.toString();

        requestQueue = Volley.newRequestQueue(context);

        delayHandler = new Handler();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        repatingRequests.run();
        return null;
    }

    private Runnable repatingRequests = new Runnable() {
        @Override
        public void run() {

            if (sharedPreferences.getBoolean(context.getResources().getString(R.string.preferences_location_requests_running), false)) {
                double latitude = 0;
                double longitude = 0;

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                        latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                        longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
                    }
                }
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                        latitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                        longitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                    }
                }

                if (longitude != 0 || latitude != 0) {
                    requestQueue.add(createPutUserRequest(latitude, longitude));
                }

            }
            if (!sharedPreferences.getBoolean(context.getResources().getString(R.string.preferences_location_requests_finish), true))
                delayHandler.postDelayed(repatingRequests, delayTime);
            else
                delayHandler.removeCallbacks(repatingRequests);
        }
    };

    private JsonObjectRequest createPutUserRequest(double latitude, double longitude) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(context.getResources().getString(R.string.person_json_id), USER_ID);
            jsonObject.put(context.getResources().getString(R.string.person_json_latitude), latitude);
            jsonObject.put(context.getResources().getString(R.string.person_json_longitude), longitude);
            jsonObject.put(context.getResources().getString(R.string.person_json_online), true);
            jsonObject.put(context.getResources().getString(R.string.person_json_label), USER_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest putRequest;

        putRequest = new JsonObjectRequest(Request.Method.PUT, peoplePutUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        putRequest.setShouldCache(false);

        return putRequest;
    }

    public void setWorkingState(boolean workingState) {
        this.workingState = workingState;
    }
}
