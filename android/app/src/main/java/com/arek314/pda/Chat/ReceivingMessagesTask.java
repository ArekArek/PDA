package com.arek314.pda.Chat;

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

public class ReceivingMessagesTask extends AsyncTask {
    private final WeakReference<ChatActivity> chatActivity;
    private final Handler delayHandler;

    private RequestQueue requestQueue;
    private Context context;
    private int delayTime;
    private ArrayList<Message> messagesList;
    private boolean workingState;
    private String messagesUrl;

    public ReceivingMessagesTask(ChatActivity chatActivity, Context context, int delayTime) {
        this.chatActivity = new WeakReference<ChatActivity>(chatActivity);
        this.context = context;
        this.delayTime = delayTime;

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(context.getResources().getString(R.string.main_URI_scheme))
                .authority(context.getResources().getString(R.string.main_URI))
                .appendPath(context.getResources().getString(R.string.messages_append_path));
        messagesUrl = uriBuilder.toString();
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
        workingState = true;
    }

    public void stopRepeatingRequests() {
        delayHandler.removeCallbacks(repatingRequests);
        workingState = false;
    }

    private Runnable repatingRequests = new Runnable() {
        @Override
        public void run() {

            if (workingState) {
                requestQueue.add(createGetMessagesRequest());
            }
            delayHandler.postDelayed(repatingRequests, delayTime);
        }
    };

    private JsonArrayRequest createGetMessagesRequest() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                messagesUrl.toString(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        messagesList = parseResponseToMessagesList(response);
                        chatActivity.get().redrawList(messagesList);
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

    private ArrayList<Message> parseResponseToMessagesList(JSONArray jsonArray) {
        ArrayList<Message> result = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                int id = jsonArray.getJSONObject(i).getInt("id");
                int userId = jsonArray.getJSONObject(i).getInt("userId");
                long date = jsonArray.getJSONObject(i).getLong("date");
                String sender = jsonArray.getJSONObject(i).getString("sender");
                String content = jsonArray.getJSONObject(i).getString("message");
                result.add(new Message(id,
                        userId,
                        date,
                        sender,
                        content));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


}
