package com.example.dartlife.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dartlife.R;
import com.example.dartlife.activity.FreeFoodActivity;
import com.example.dartlife.service.TrackService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class FoodFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    private MyTrackingReceiver mTrackRecv;
    private Location mCurLocation = null;
    private Marker mNewFoodMarker = null;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 29;
    private boolean mFirstLocation = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        checkPermission();
        mTrackRecv = new MyTrackingReceiver();
        Objects.requireNonNull(getActivity()).registerReceiver(
                mTrackRecv,
                new IntentFilter("tracking information"));
        View v = inflater.inflate(R.layout.fragment_food, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().
                findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //display the blue dot and add map clicklistener
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.equals(mNewFoodMarker)){
                    //when the user hopes to add a new free food
                    Intent freefoodIntent = new Intent(getActivity(), FreeFoodActivity.class);
                    startActivity(freefoodIntent);
                }
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mNewFoodMarker == null) {
                    mNewFoodMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                            .title("Create a new Free Food!")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
                mNewFoodMarker.setPosition(latLng);
                mNewFoodMarker.showInfoWindow();
            }
        });
    }


    //the callback function of requesting permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


    //my receiver, it updates the location information and shows it on the map
    //when it receives the information form tracking service
    public class MyTrackingReceiver extends BroadcastReceiver {

        public double longitude;
        public double latitude;

        @Override
        public void onReceive(Context context, Intent intent){
            longitude = intent.getDoubleExtra("longitude", 0.0);
            latitude = intent.getDoubleExtra("latitude", 0.0);
            Location receivedLocation = new Location("mNewLocation");
            receivedLocation.setLatitude(latitude);
            receivedLocation.setLongitude(longitude);
            getLocationCallback(receivedLocation);
        }
    }

    //check permission of location
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            startTracking();
        }
    }

    //start the tracking service to get the location
    private void startTracking() {
        Objects.requireNonNull(getActivity()).startService(new Intent(getActivity(), TrackService.class));
    }

    @Override
    public void onDestroy() {
        Objects.requireNonNull(getActivity()).stopService(new Intent(getActivity(), TrackService.class));
        getActivity().unregisterReceiver(mTrackRecv);
        super.onDestroy();
    }


    private void getLocationCallback(Location location){
        Log.d("fan", "getLocationCallback: 1122233");
        mCurLocation = location;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(mFirstLocation) {
            mFirstLocation = false;
            builder.include(new LatLng(mCurLocation.getLatitude(), mCurLocation.getLongitude()));
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.12);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mGoogleMap.animateCamera(cu);
        }
    }



}

