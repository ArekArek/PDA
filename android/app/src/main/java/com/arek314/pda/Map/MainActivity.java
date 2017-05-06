package com.arek314.pda.Map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.arek314.pda.Chat.ChatActivity;
import com.arek314.pda.NetworkChangeReceiver;
import com.arek314.pda.OverlayView;
import com.arek314.pda.R;

import java.util.ArrayList;

public class MainActivity extends Activity implements LocationListener {
    private final String TAG = "MainActivity";
    private final int MY_PERMISSIONS_REQUEST_INTERNET = 1;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private final String PREFERENCES_LATITUDE = "latitude";
    private final String PREFERENCES_LONGITUDE = "longitude";
    private final String PREFERENCES_OTHERS_STATE = "others_switch";
    private static int USER_ID;
    private static String USER_NAME;
    static final int MIN_SWIP_DISTANCE = 15;


    private double minLatitude;
    private double minLongitude;
    private double maxLatitude;
    private double maxLongitude;
    private double currentLatitude;
    private double currentLongitude;

    private ViewGroup peopleMarkers;
    private boolean isPeopleMarkers;

    private RelativeLayout thisLayout;
    private ImageView marker;
    private ImageView mapView;

    private int doubleKlicked;
    private boolean outOfArea;
    private ZoomAttributes zoomAttributes;
    private static ReceivingDataTask receivingDataTask;
    private static ReceivingInformationsTask receivingInformationsTask;
    private OverlayView overlayViewConnection;
    private OverlayView overlayViewArea;
    private NetworkChangeReceiver networkChangeReceiver;
    private LocationManager locationManager;
    private SharedPreferences activityPreferences;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermission();
        networkChangeReceiver = new NetworkChangeReceiver(this);
        activityPreferences = getPreferences(MODE_PRIVATE);
        preferences = getSharedPreferences(getResources().getString(R.string.preferences_name), MODE_PRIVATE);
        USER_ID = preferences.getInt(getResources().getString(R.string.preferences_user_id), 0);
        USER_NAME = preferences.getString(getResources().getString(R.string.preferences_user_name), getResources().getString(R.string.default_user_name));

        peopleMarkers = new RelativeLayout(this);
        peopleMarkers.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.MATCH_PARENT));
        peopleMarkers.setId(R.id.peopleMarkersGroup);


        //setting initial configuration of:
        // - extreme map coordinates
        // - mapView (background image-map)
        // - marker of position
        // - location finder(e.g.GPS)
        setCornerCoordinatesFromResources();
        setMapView();
        setMarker();
        setLocationConfiguration();
        doubleKlicked = 0;

        //adding to layout of MainActivity background image (it's map), and marker of position
        thisLayout = (RelativeLayout) findViewById(R.id.activity_main);
        //thisLayout.addView(mapView);
        thisLayout.addView(marker);

        thisLayout.addView(peopleMarkers);
        thisLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && zoomAttributes == null) {
                    slideActivity(motionEvent);
                }
                return true;
            }
        });

        findViewById(R.id.ui_layout).bringToFront();

        receivingDataTask = new ReceivingDataTask(this, this,
                getApplicationContext().getResources().getInteger(R.integer.dataRequestsDelayTime),
                USER_ID,
                USER_NAME);
        receivingDataTask.execute();


        receivingInformationsTask = new ReceivingInformationsTask(this,
                this,
                getApplicationContext().getResources().getInteger(R.integer.informationRequestsDelayTime));
        receivingInformationsTask.execute();


        setOthersSwitch();
        if (!getPreferences(MODE_PRIVATE).getString(this.getResources().getString(R.string.preferences_map_url), "").equals("")) {
            receivingInformationsTask.refreshImage(getPreferences(MODE_PRIVATE).getString(this.getResources().getString(R.string.preferences_map_url), ""));
        }


    }

    @Override
    protected void onStart() {
        registerReceiver(networkChangeReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onStart();

        if (!((ToggleButton) findViewById(R.id.switch_others)).isChecked() && !receivingDataTask.isWorkingState()) {
            startMapRequests();
            SharedPreferences.Editor tmpPrefEditor = preferences.edit();
            tmpPrefEditor.putBoolean(getResources().getString(R.string.preferences_location_requests_running), true);
            tmpPrefEditor.apply();
            isPeopleMarkers = true;
        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeReceiver);
        stopMapRequests();

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putFloat(PREFERENCES_LATITUDE, (float) currentLatitude);
        editor.putFloat(PREFERENCES_LONGITUDE, (float) currentLongitude);
        editor.putBoolean(PREFERENCES_OTHERS_STATE, ((ToggleButton) findViewById(R.id.switch_others)).isChecked());
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        USER_ID = preferences.getInt(getResources().getString(R.string.preferences_user_id), 0);
        USER_NAME = preferences.getString(getResources().getString(R.string.preferences_user_name), getResources().getString(R.string.default_user_name));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            Location tmpLocation = new Location("");
            tmpLocation.setLatitude(activityPreferences.getFloat(PREFERENCES_LATITUDE, 0));
            tmpLocation.setLongitude(activityPreferences.getFloat(PREFERENCES_LONGITUDE, 0));
            myLocationHadChanged(tmpLocation);
            ((ToggleButton) findViewById(R.id.switch_others)).setChecked(activityPreferences.getBoolean(PREFERENCES_OTHERS_STATE, false));
        }
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
        // permission for access fine location
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }

    private void setCornerCoordinatesFromResources() {
        minLatitude = Double.parseDouble(getResources().getString(R.string.bottom_right_corner_latitude));
        minLongitude = Double.parseDouble(getResources().getString(R.string.top_left_corner_longitude));
        maxLatitude = Double.parseDouble(getResources().getString(R.string.top_left_corner_latitude));
        maxLongitude = Double.parseDouble(getResources().getString(R.string.bottom_right_corner_longitude));
    }

    private void setMarker() {
        marker = new ImageView(this);
        marker.setBackgroundResource(R.drawable.marker);
        marker.setVisibility(View.INVISIBLE);
    }

    private void setMapView() {
        mapView = (ImageView) findViewById(R.id.map_view);
        mapView.setBackgroundResource(R.drawable.map);


        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (zoomAttributes != null) {
                        if (doubleKlicked >= 1) {
                            zoomOut();
                            doubleKlicked = 0;
                        } else {
                            new Thread(new DoubleClickedTask()).start();
                            doubleKlicked++;
                        }
                    } else {
                        if (doubleKlicked >= 1) {
                            zoomIn(motionEvent);
                            doubleKlicked = 0;
                        } else {
                            new Thread(new DoubleClickedTask()).start();
                            doubleKlicked++;
                        }
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && zoomAttributes == null) {
                    slideActivity(motionEvent);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && zoomAttributes != null) {
                    moveZoom(motionEvent);
                }
                return true;
            }

        });
    }

    public ImageView getMapView() {
        return mapView;
    }

    private void setOthersSwitch() {
        ((ToggleButton) findViewById(R.id.switch_others)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor tmpPrefEditor = preferences.edit();
                    tmpPrefEditor.putBoolean(getResources().getString(R.string.preferences_location_requests_running), false);
                    tmpPrefEditor.apply();
                    receivingDataTask.setWorkingState(false);
                    peopleMarkers.removeAllViews();
                    isPeopleMarkers = false;
                } else {
                    SharedPreferences.Editor tmpPrefEditor = preferences.edit();
                    tmpPrefEditor.putBoolean(getResources().getString(R.string.preferences_location_requests_running), true);
                    tmpPrefEditor.apply();
                    receivingDataTask.setWorkingState(true);
                    isPeopleMarkers = true;
                }
            }
        });
    }

    private class DoubleClickedTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
                doubleKlicked = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class ZoomAttributes {
        float positionX;
        float positionY;
        int scale;

        ZoomAttributes(MotionEvent motionEvent) {
            float clickedXPosition = motionEvent.getX();
            float clickedYPosition = motionEvent.getY();

            //calculating position to zoom in
            int horizontalActivityMarigin = getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
            int verticalActivityMarigin = getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
            positionX = clickedXPosition / thisLayout.getWidth() * horizontalActivityMarigin * 2;
            positionY = clickedYPosition / thisLayout.getHeight() * verticalActivityMarigin * 2;
            positionX += clickedXPosition;
            positionY += clickedYPosition;

            scale = getResources().getInteger(R.integer.zoom_animation_scale);
        }

    }

    private void zoomIn(MotionEvent motionEvent) {

        //setting zoom atributes
        zoomAttributes = new ZoomAttributes(motionEvent);

        AnimationSet as = new AnimationSet(true);
        as.setDuration(getResources().getInteger(R.integer.zoom_animation_time));
        ScaleAnimation sa = new ScaleAnimation(1,
                zoomAttributes.scale,
                1,
                zoomAttributes.scale,
                zoomAttributes.positionX,
                zoomAttributes.positionY);
        as.addAnimation(sa);
        as.setFillAfter(true);
        as.setFillEnabled(true);

        thisLayout.startAnimation(as);
    }

    private void zoomOut() {
        //setting animation
        AnimationSet as = new AnimationSet(true);
        as.setDuration(getResources().getInteger(R.integer.zoom_animation_time));
        ScaleAnimation sa = new ScaleAnimation(zoomAttributes.scale,
                1,
                zoomAttributes.scale,
                1,
                zoomAttributes.positionX,
                zoomAttributes.positionY);
        as.addAnimation(sa);
        as.setFillAfter(true);
        as.setFillEnabled(true);

        zoomAttributes = null;

        thisLayout.startAnimation(as);
    }

    private void moveZoom(MotionEvent motionEvent) {
        //setting animation
        AnimationSet as = new AnimationSet(true);

        int motionHistorySize = motionEvent.getHistorySize();
        if (motionHistorySize <= 0)
            return;

        zoomAttributes.positionX += (motionEvent.getHistoricalX(motionHistorySize - 1) - motionEvent.getX());
        zoomAttributes.positionY += (motionEvent.getHistoricalY(motionHistorySize - 1) - motionEvent.getY());

        ScaleAnimation sa = new ScaleAnimation(zoomAttributes.scale,
                zoomAttributes.scale,
                zoomAttributes.scale,
                zoomAttributes.scale,
                zoomAttributes.positionX,
                zoomAttributes.positionY);
        as.addAnimation(sa);
        as.setFillAfter(true);
        as.setFillEnabled(true);

        thisLayout.startAnimation(as);
    }

    public void setMarkerPosition(Location location) {
        marker.setVisibility(View.VISIBLE);

        float x = calculateXPosition(location);
        float y = calculateYPosition(location);

        marker.setX(x);
        marker.setY(y);
    }

    private float calculateXPosition(Location location) {
        double scale = (mapView.getWidth() / (maxLongitude - minLongitude));
        double tmp = (location.getLongitude() - minLongitude) * scale;
        tmp += mapView.getLeft();
        tmp -= marker.getMeasuredWidth() / 2;
        return (float) tmp;
    }

    private float calculateYPosition(Location location) {
        double scale = mapView.getHeight() / (maxLatitude - minLatitude);
        double tmp = (maxLatitude - location.getLatitude()) * scale;
        //tmp -= mapView.getTop();
        tmp += marker.getMeasuredHeight() / 2;
        return (float) tmp;
    }

    private float calculateXPositionPeople(double longitude, View tmpMarker) {
        double scale = (mapView.getWidth() / (maxLongitude - minLongitude));
        double tmp = (longitude - minLongitude) * scale;
        //tmp += mapView.getLeft();
        tmp -= tmpMarker.getWidth() / 2;
        return (float) tmp;
    }

    private float calculateYPositionPeople(double latitude, View tmpMarker) {
        double scale = mapView.getHeight() / (maxLatitude - minLatitude);
        double tmp = (maxLatitude - latitude) * scale;
        //tmp -= mapView.getTop();
        tmp += tmpMarker.getHeight() / 2;
        return (float) tmp;
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
            Log.w(TAG, "No permission granted");
            return;
        }

        //setting configuration
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long refreshTime = getResources().getInteger(R.integer.location_refresh_time);
        float refreshDistance = (float) getResources().getInteger(R.integer.location_refresh_distance);


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                refreshTime,
                refreshDistance,
                this);
    }

    private void myLocationHadChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        //checking if user location is in defined area
        if ((location.getLatitude() > minLatitude) &&
                (location.getLatitude() < maxLatitude) &&
                (location.getLongitude() > minLongitude) &&
                (location.getLongitude() < maxLongitude)) {
            setMarkerPosition(location);
            if (outOfArea) {
                hideOutOfArea();
                outOfArea = false;
            }
        } else {
            if (!outOfArea) {
                displayOutOfArea();
                outOfArea = true;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocationHadChanged(location);
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

    public void redrawPeoplePositions(ArrayList<Person> allPeople) {
        if (!isPeopleMarkers)
            return;
        peopleMarkers.removeAllViews();
        for (Person person : allPeople) {
            ImageView tmpMarker = new ImageView(this);
            if (person.getId() > 900000)
                tmpMarker.setBackgroundResource(R.drawable.mutant_marker);
            else
                tmpMarker.setBackgroundResource(R.drawable.people_marker);

            float x = calculateXPositionPeople(person.getLongitude(), tmpMarker);
            float y = calculateYPositionPeople(person.getLatitude(), tmpMarker);

            tmpMarker.setX(x);
            tmpMarker.setY(y);
            peopleMarkers.addView(tmpMarker);

            int index = peopleMarkers.getChildCount() - 1;
            peopleMarkers.getChildAt(index).measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            x = peopleMarkers.getChildAt(index).getX() - (peopleMarkers.getChildAt(index).getMeasuredWidth() / 2);
            y = peopleMarkers.getChildAt(index).getY() - (peopleMarkers.getChildAt(index).getMeasuredHeight() / 2);
            peopleMarkers.getChildAt(index).setX(x);
            peopleMarkers.getChildAt(index).setY(y);
        }
    }

    public void startMapRequests() {
        receivingDataTask.startRepeatingRequests();
        receivingInformationsTask.startRepeatingRequests();
    }

    public void stopMapRequests() {
        receivingDataTask.stopRepeatingRequests();
        receivingInformationsTask.stopRepeatingRequests();
    }

    public void displayNoConnection() {

        if (overlayViewConnection == null) {
            overlayViewConnection = new OverlayView(this.getBaseContext(), BitmapFactory.decodeResource(getResources(), R.drawable.no_connection));
            //overlayViewConnection.setBackgroundColor(getResources().getColor(R.color.noConnection));
            overlayViewConnection.setAlpha(0.7f);

            this.addContentView(overlayViewConnection, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
    }

    public void displayOutOfArea() {

        if (overlayViewArea == null) {
            overlayViewArea = new OverlayView(this.getBaseContext(), BitmapFactory.decodeResource(getResources(), R.drawable.out_of_area));
            //overlayViewArea.setBackgroundColor(getResources().getColor(R.color.noConnection));
            overlayViewArea.setAlpha(0.5f);

            this.addContentView(overlayViewArea, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
    }

    public void hideNoConnection() {
        if (overlayViewConnection != null) {
            overlayViewConnection.clearCanvas();
            ((ViewGroup) overlayViewConnection.getParent()).removeView(overlayViewConnection);
            overlayViewConnection = null;
        }
    }

    public void hideOutOfArea() {
        if (overlayViewArea != null) {
            overlayViewArea.clearCanvas();
            ((ViewGroup) overlayViewArea.getParent()).removeView(overlayViewArea);
            overlayViewArea = null;
        }
    }

    public void refreshButtonClicked(View view) {
        receivingInformationsTask.refreshImage();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void slideActivity(MotionEvent motionEvent) {
        int motionHistorySize = motionEvent.getHistorySize();
        if (motionHistorySize <= 0)
            return;

        float distanceX = (motionEvent.getHistoricalX(motionHistorySize - 1) - motionEvent.getX());

        System.out.print("\n\n" + distanceX + "\n\n");

        if (distanceX > MIN_SWIP_DISTANCE) {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        }
    }

}
