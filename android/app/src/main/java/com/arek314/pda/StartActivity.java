package com.arek314.pda;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arek314.pda.Map.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StartActivity extends Activity {
    private Button goButton;
    private EditText codeEditText;
    private EditText nameEditText;
    private LocationManager locationManager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        preferences = getSharedPreferences(getResources().getString(R.string.preferences_name), MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean(getResources().getString(R.string.preferences_location_requests_finish), true);
        editor.apply();


        setViews();
    }

    private void setViews() {
        goButton = (Button) findViewById(R.id.go_button);
        codeEditText = (EditText) findViewById(R.id.code_edit_text);
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        codeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 6)
                    goButton.setEnabled(true);
                else
                    goButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        codeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && goButton.isEnabled())
                    goClicked(v);
                return false;
            }
        });
    }

    public void goClicked(View view) {
        int userCode = Integer.parseInt(codeEditText.getText().toString());
        String userName = nameEditText.getText().toString();
        if (isCodeCorrect(userCode)) {
            if (userName.equals(""))
                userName = getResources().getString(R.string.default_user_name);

            editor.putInt(getResources().getString(R.string.preferences_user_id), userCode);
            editor.putString(getResources().getString(R.string.preferences_user_name), userName);
            editor.putBoolean(getResources().getString(R.string.preferences_location_requests_running), false);
            editor.putBoolean(getResources().getString(R.string.preferences_location_requests_finish), false);
            editor.apply();

            setLocationConfiguration();

            SendingLocationTask sendingLocationTask = new SendingLocationTask(this,
                    getApplicationContext().getResources().getInteger(R.integer.dataRequestsDelayTime),
                    userCode,
                    userName,
                    locationManager);
            sendingLocationTask.execute();


            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean isCodeCorrect(int userCode) {
        InputStream in = getResources().openRawResource(R.raw.pda_codes);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (Integer.parseInt(line) == userCode)
                    return true;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "wrong code", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void setLocationConfiguration() {
        //checking permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //setting configuration
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long refreshTime = getResources().getInteger(R.integer.location_refresh_time);
        float refreshDistance = (float) getResources().getInteger(R.integer.location_refresh_distance);


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                refreshTime,
                refreshDistance,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }


}
