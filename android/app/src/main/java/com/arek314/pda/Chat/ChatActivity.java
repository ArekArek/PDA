package com.arek314.pda.Chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arek314.pda.Chat.MessagesList.MessageRowBean;
import com.arek314.pda.Chat.MessagesList.MessagesRowAdapter;
import com.arek314.pda.Map.MainActivity;
import com.arek314.pda.NetworkChangeReceiver;
import com.arek314.pda.OverlayView;
import com.arek314.pda.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class ChatActivity extends Activity {
    private static int USER_ID;
    private static String USER_NAME;
    static final int MIN_SWIP_DISTANCE = 20;
    private final int MY_PERMISSIONS_REQUEST_INTERNET = 1;

    private NetworkChangeReceiver networkChangeReceiver;
    private OverlayView overlayView;
    private ArrayList<MessageRowBean> messagesList;
    private ListView messagesViewList;
    private SharedPreferences preferences;
    private ReceivingMessagesTask receivingMessagesTask;
    private RequestQueue requestQueue;
    private Button sendButton;
    private EditText inputEditText;
    private MessagesRowAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(getResources().getString(R.string.preferences_name), MODE_PRIVATE);
        USER_ID = preferences.getInt(getResources().getString(R.string.preferences_user_id), 0);
        USER_NAME = preferences.getString(getResources().getString(R.string.preferences_user_name), getResources().getString(R.string.default_user_name));
        setContentView(R.layout.activity_chat);

        checkForPermission();
        networkChangeReceiver = new NetworkChangeReceiver(this);
        requestQueue = Volley.newRequestQueue(this);
        sendButton = (Button) findViewById(R.id.message_send_button);
        inputEditText = (EditText) findViewById(R.id.message_input);
        inputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesViewList.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        messagesViewList.setSelection(messagesList.size() - 1);
                    }
                }, 250);
            }
        });
        inputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    messagesViewList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messagesViewList.setSelection(messagesList.size() - 1);
                        }
                    }, 250);
                }

            }
        });
        inputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && sendButton.isEnabled())
                    sendMessage();
                return false;
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        messagesList = new ArrayList<>();
        adapter = new MessagesRowAdapter(messagesList, this);
        messagesViewList = (ListView) findViewById(R.id.messages_list_view);
        messagesViewList.setAdapter(adapter);

        messagesViewList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    slideActivity(event);
                }
                return false;
            }
        });

        receivingMessagesTask = new ReceivingMessagesTask(this, this, 2000);
        receivingMessagesTask.execute();
    }

    private void sendMessage() {
        String messageText = inputEditText.getText().toString();
        if (messageText.equals(""))
            return;
        requestQueue.add(createPostMessageRequest(messageText));

        sendButton.setEnabled(false);
    }

    private JsonObjectRequest createPostMessageRequest(String inputText) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(getApplicationContext().getResources().getString(R.string.main_URI_scheme))
                .authority(getApplicationContext().getResources().getString(R.string.main_URI))
                .appendPath(getApplicationContext().getResources().getString(R.string.messages_append_path));

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(getApplicationContext().getResources().getString(R.string.message_json_user_id), USER_ID);
            jsonObject.put(getApplicationContext().getResources().getString(R.string.message_json_sender), USER_NAME);
            jsonObject.put(getApplicationContext().getResources().getString(R.string.message_json_content), inputText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, uriBuilder.toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                        inputEditText.setText("");
                        sendButton.setEnabled(true);
                        messagesViewList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                messagesViewList.setSelection(messagesList.size() - 1);
                            }
                        }, 500);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error.Response", error.toString());
                        sendButton.setEnabled(true);
                    }
                }
        );
        request.setShouldCache(false);
        return request;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    @Override
    public void onStart() {
        registerReceiver(networkChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeReceiver);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRequests();
        USER_ID = preferences.getInt(getResources().getString(R.string.preferences_user_id), 0);
        USER_NAME = preferences.getString(getResources().getString(R.string.preferences_user_name), getResources().getString(R.string.default_user_name));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransitionEnter();
    }

    private void checkForPermission() {
        // permission for internet
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.INTERNET)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET);

            }
        }
    }

    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            slideActivity(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
    }

    public void displayNoConnection() {

        if (overlayView == null) {
            overlayView = new OverlayView(this.getBaseContext(), BitmapFactory.decodeResource(getResources(), R.drawable.no_connection));
            overlayView.setBackgroundColor(getResources().getColor(R.color.noConnection));
            overlayView.setAlpha(0.2f);

            this.addContentView(overlayView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
    }

    public void stopRequests() {
        receivingMessagesTask.stopRepeatingRequests();
    }

    public void startRequests() {
        receivingMessagesTask.startRepeatingRequests();
    }

    public void hideNoConnection() {
        if (overlayView != null) {
            overlayView.clearCanvas();
            ((ViewGroup) overlayView.getParent()).removeView(overlayView);
            overlayView = null;
        }
    }

    public void redrawList(ArrayList<Message> messages) {
        ArrayList<MessageRowBean> messageRows = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone(getResources().getString(R.string.country)));
        for (Message tmp : messages) {
            messageRows.add(new MessageRowBean(tmp.getId(),
                    tmp.getUserId(),
                    dateFormat.format((new Timestamp(tmp.getDate())).getTime()),
                    tmp.getSender(),
                    tmp.getContent()));
        }

        messagesList.clear();
        messagesList.addAll(messageRows);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });


    }

    private void slideActivity(MotionEvent motionEvent) {
        int motionHistorySize = motionEvent.getHistorySize();
        if (motionHistorySize <= 0)
            return;

        float distanceX = (motionEvent.getHistoricalX(motionHistorySize - 1) - motionEvent.getX());
        float distanceY = (motionEvent.getHistoricalY(motionHistorySize - 1) - motionEvent.getY());

        System.out.print("\n\n" + distanceX + "\n\n");

        if (distanceX < MIN_SWIP_DISTANCE * (-1) && Math.abs(distanceX) > Math.abs(distanceY)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


}
