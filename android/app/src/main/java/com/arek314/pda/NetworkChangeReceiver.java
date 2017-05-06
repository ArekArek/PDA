package com.arek314.pda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.arek314.pda.Chat.ChatActivity;
import com.arek314.pda.Map.MainActivity;

import java.lang.ref.WeakReference;


public class NetworkChangeReceiver extends BroadcastReceiver {

    private WeakReference<MainActivity> mainActivity;
    private WeakReference<ChatActivity> chatActivity;

    public NetworkChangeReceiver(MainActivity mainActivity) {
        this.mainActivity = new WeakReference<MainActivity>(mainActivity);
    }

    public NetworkChangeReceiver() {
    }

    public NetworkChangeReceiver(ChatActivity chatActivity) {
        this.chatActivity = new WeakReference<ChatActivity>(chatActivity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mainActivity != null) {
            if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.i("Connection changed: ", "No internet connection");
                mainActivity.get().stopMapRequests();
                mainActivity.get().displayNoConnection();
            } else if (intent.getExtras() != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected()) {
                    Log.i("Connection changed: ", "There is internet connection");
                    mainActivity.get().startMapRequests();
                    mainActivity.get().hideNoConnection();
                }
            }
        } else if (chatActivity != null) {
            if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.i("Connection changed: ", "No internet connection");
                chatActivity.get().displayNoConnection();
                chatActivity.get().stopRequests();
            } else if (intent.getExtras() != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected()) {
                    Log.i("Connection changed: ", "There is internet connection");
                    chatActivity.get().hideNoConnection();
                    chatActivity.get().startRequests();
                }
            }
        }
    }
}
