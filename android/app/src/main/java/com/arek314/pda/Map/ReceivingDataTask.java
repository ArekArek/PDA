package com.arek314.pda.Map;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.arek314.pda.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ReceivingDataTask extends AsyncTask {
    private static int USER_ID;
    private static String USER_NAME;
    private final WeakReference<MainActivity> mainActivity;
    private final Handler delayHandler;

    private RequestQueue requestQueue;
    private Context context;
    private int delayTime;
    private ArrayList<Person> allPeople;
    private boolean workingState;
    private String peopleGetUrl;

    ReceivingDataTask(MainActivity activity, Context context, int delayTime, int userId, String userName) {
        this.mainActivity = new WeakReference<>(activity);
        this.context = context;
        this.delayTime = delayTime;
        USER_ID = userId;
        USER_NAME = userName;

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(context.getResources().getString(R.string.main_URI_scheme))
                .authority(context.getResources().getString(R.string.main_URI))
                .appendPath(context.getResources().getString(R.string.people_append_path))
                .appendQueryParameter(context.getResources().getString(R.string.id_query_parameter), String.valueOf(USER_ID))
                .appendQueryParameter(context.getResources().getString(R.string.isonline_query_parameter), context.getResources().getString(R.string.isonline_true));
        peopleGetUrl = uriBuilder.toString();
        requestQueue = Volley.newRequestQueue(context);

        delayHandler = new Handler();
        workingState = false;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        repatingRequests.run();
        return null;
    }

    private Runnable repatingRequests = new Runnable() {
        @Override
        public void run() {
            if (workingState) {

                requestQueue.add(createGetPeoplePositionsRequest());

            }
            delayHandler.postDelayed(repatingRequests, delayTime);
        }
    };

    public void startRepeatingRequests() {
        workingState = true;
    }

    public void stopRepeatingRequests() {
        delayHandler.removeCallbacks(repatingRequests);
        workingState = false;
    }


    private JsonArrayRequest createGetPeoplePositionsRequest() {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                peopleGetUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        allPeople = parseAPIResponseToList(response);
                        mainActivity.get().redrawPeoplePositions(allPeople);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());

                    }
                });

        request.setShouldCache(false);
        return request;
    }


    private ArrayList<Person> parseAPIResponseToList(final JSONArray jsonArray) {
        ArrayList<Person> result = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                int id = jsonArray.getJSONObject(i).getInt(context.getResources().getString(R.string.person_json_id));
                double latitude = jsonArray.getJSONObject(i).getDouble(context.getResources().getString(R.string.person_json_latitude));
                double longitude = jsonArray.getJSONObject(i).getDouble(context.getResources().getString(R.string.person_json_longitude));
                String label = jsonArray.getJSONObject(i).getString(context.getResources().getString(R.string.person_json_label));
                result.add(new Person(id,
                        latitude,
                        longitude,
                        label));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isWorkingState() {
        return workingState;
    }

    public void setWorkingState(boolean workingState) {
        this.workingState = workingState;
    }
}

