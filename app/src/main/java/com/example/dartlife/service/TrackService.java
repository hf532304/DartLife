package com.example.dartlife.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dartlife.R;
import com.example.dartlife.activity.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class TrackService extends Service {

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private final static String TRACKING_CHANNEL = "Tracking";
    final int TRACKING_NOTIFICATION_SERVICE_ID = 1001;
    private LocationCallback mLocationCallback;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //send the notification when created
        Notification notification = createNotification(this);
        startForeground(TRACKING_NOTIFICATION_SERVICE_ID, notification);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("fan", "onStartCommand: 11123");
        init_LocationListening();
        startLocationUpdates();
        return Service.START_STICKY;
    }


    //initialize the location request and start listen to the location changes
    private void init_LocationListening() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(200);
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                //broadcast the location information to map activity
                                Intent intent = new Intent();
                                intent.putExtra("longitude",location.getLongitude());
                                intent.putExtra("latitude", location.getLatitude());
                                Log.d("fan", "get location in service");
                                intent.setAction("tracking information");
                                sendBroadcast(intent);
                            }
                        }
                    });
        }
        //the callback is to use when find the location changes,
        //send broadcast to inform the map activity
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Intent intent = new Intent();
                        intent.putExtra("longitude",location.getLongitude());
                        intent.putExtra("latitude", location.getLatitude());
                        intent.setAction("tracking information");
                        Log.d("fan", "get location in service1");
                        sendBroadcast(intent);
                    }
                }
            }
        };
    }


    //use buildNotification and createChannel to create the notification
    public Notification createNotification(Service context) {

        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = TRACKING_CHANNEL;
            createChannel(context, channelId);
            notification = buildNotification(context, channelId);
        }
        return notification;
    }

    //build the notification and go to mapActivity when user clicks on the notification
    private Notification buildNotification(Service context, String channelId) {
        @SuppressLint("PrivateResource")
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Tracking Service")
                .setContentText("Tracking service started").
                        setPriority(Notification.PRIORITY_HIGH).
                        setDefaults(Notification.DEFAULT_SOUND).
                        setSmallIcon(R.drawable.icon).setOngoing(true);
        builder.setVibrate(new long[0]);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        return builder.build();
    }

    //create a channel
    private static void createChannel(Service ctx, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager =
                    (NotificationManager)
                            ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence channelName = "tracking service channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel;
            notificationChannel = new NotificationChannel(
                    channelId, channelName, importance);

            notificationManager.createNotificationChannel(
                    notificationChannel);
        }
    }


    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    //the function to start update the location information
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, null);
        }
    }

    //the function to stop updating location information
    private void stopLocationUpdates() {
        if(fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

}
