package com.arek314.pda.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arek314.pda.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class ReceivingInformationsTask extends AsyncTask {
    private static String PREFERENCES_MAP_URL;
    private final WeakReference<MainActivity> mainActivity;
    private final Handler delayHandler;

    private RequestQueue requestQueue;
    private Context context;
    private int delayTime;
    private SharedPreferences preferences;
    private String informationsUrl;


    public ReceivingInformationsTask(MainActivity mainActivity, Context context, int delayTime) {
        this.mainActivity = new WeakReference<>(mainActivity);
        this.context = context;
        this.delayTime = delayTime;
        PREFERENCES_MAP_URL = context.getResources().getString(R.string.preferences_map_url);
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(context.getResources().getString(R.string.main_URI_scheme))
                .authority(context.getResources().getString(R.string.main_URI))
                .appendPath(context.getResources().getString(R.string.information_append_path));
        informationsUrl = uriBuilder.toString();
        preferences = this.mainActivity.get().getPreferences(Activity.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(context);
        delayHandler = new Handler();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        startRepeatingRequests();
        return null;
    }

    public void startRepeatingRequests() {
        repatingRequests.run();
    }

    public void stopRepeatingRequests() {
        delayHandler.removeCallbacks(repatingRequests);
    }

    private Runnable repatingRequests = new Runnable() {
        @Override
        public void run() {
            requestQueue.add(createGetInformationRequest());

            delayHandler.postDelayed(repatingRequests, delayTime);
        }
    };

    private JsonObjectRequest createGetInformationRequest() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                informationsUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        receivedInformation(response);
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

    private void receivedInformation(JSONObject response) {
        if (response.has(context.getResources().getString(R.string.preferences_map_url))) {
            try {
                String responseMapURL = response.getString(context.getResources().getString(R.string.preferences_map_url));
                String actualMapURL = preferences.getString(PREFERENCES_MAP_URL, "");
                if (!"".equals(responseMapURL))
                    if (!responseMapURL.equals(actualMapURL)) {
                        if (responseMapURL.equals(context.getResources().getString(R.string.preferences_map_origin))) {
                            mainActivity.get().getMapView().setBackgroundResource(R.drawable.map);
                            SharedPreferences.Editor preferencesEditor = preferences.edit();
                            preferencesEditor.putString(PREFERENCES_MAP_URL, responseMapURL);
                            preferencesEditor.commit();
                        } else
                            requestQueue.add(getImageRequest(responseMapURL));
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ImageRequest getImageRequest(final String url) {
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Drawable ob = new BitmapDrawable(mainActivity.get().getResources(), response);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mainActivity.get().getMapView().setBackground(ob);
                } else {
                    mainActivity.get().getMapView().setBackgroundDrawable(ob);
                }

                SharedPreferences.Editor preferencesEditor = preferences.edit();
                preferencesEditor.putString(PREFERENCES_MAP_URL, url);
                preferencesEditor.commit();
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        return ir;
    }

    public void refreshImage() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_MAP_URL, "");
        editor.commit();
        requestQueue.add(createGetInformationRequest());
    }

    public void refreshImage(String url) {
        requestQueue.add(getImageRequest(url));
    }
}
