package com.example.salat.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationService extends Service {

    private static final String TAG = com.example.salat.services.LocationService.class.getSimpleName();
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    public static double latitude;
    public static double longitude;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            if (intent.getAction() != null){
                if (intent.getAction().equals(ACTION_STOP_FOREGROUND_SERVICE)){
                    stopForegroundService();
                }else {
                    getLocation();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                    .setContentTitle("background service")
                    .setContentText("location").build();

            startForeground(1, notification);
        }

    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {

        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {

                Log.d(TAG, "task fail");
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "getLocationUpdate is null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "getLocationUpdate = lat " + location.getLatitude() + "," + location.getLongitude());
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
                sendBroadcastToHome("exist");
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(20000);
        locationRequest.setWaitForAccurateLocation(true);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void stopForegroundService() {
        Log.d(TAG, "Stop foreground service.");
        stopLocationUpdates();

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void sendBroadcastToHome(String status){
        Intent intent = new Intent("ACTION_GET_LOCATION");
        intent.putExtra("status", status);
        LocalBroadcastManager.getInstance(com.example.salat.services.LocationService.this).sendBroadcast(intent);
    }

}
