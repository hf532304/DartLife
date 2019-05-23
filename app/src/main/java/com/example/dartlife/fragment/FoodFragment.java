package com.example.dartlife.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.example.dartlife.activity.FoodDetailActivity;
import com.example.dartlife.activity.FreeFoodActivity;
import com.example.dartlife.activity.MainActivity;
import com.example.dartlife.model.Food;
import com.example.dartlife.service.TrackService;
import com.example.dartlife.tools.IconBorder;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

//TODO: request permission in fragment. before final
public class FoodFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    private MyTrackingReceiver mTrackRecv = null;
    private Location mCurLocation = null;
    private Marker mNewFoodMarker = null;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 29;
    private boolean mFirstLocation = true;
    private FirebaseDatabase mDB;
    private FirebaseStorage mStorage;
    private HashMap<String, Food> foods = new HashMap<>();
    private LinkedList<Target> mTargets = new LinkedList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //checkPermission();
        //register the boardcastreceiver
        mTrackRecv = new MyTrackingReceiver();
        Objects.requireNonNull(getActivity()).registerReceiver(
                mTrackRecv,
                new IntentFilter("tracking information"));

        View v = inflater.inflate(R.layout.fragment_food, container, false);

        //get sync
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().
                findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //initialization of the firebase
        mDB = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    private void setMarker(final Food curFood) {
        final MarkerOptions curMarker = new MarkerOptions();
        if(curFood.getFoodSource().equals("FreeFood")) {
            Target tmp = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.d("fan", "onBitmapLoaded: aaa");
                    LatLng location = new LatLng(curFood.getLocation().getLatitude(),
                            curFood.getLocation().getLongitude());
                    curMarker.position(location)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title(curFood.getTitle());
                    mGoogleMap.addMarker(curMarker);
                    foods.put(curMarker.getTitle(), curFood);
                    mTargets.remove(this);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Log.d("fan", "onPrepareLoad");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d("fan", "onPrepareLoad");
                }
            };
            mTargets.add(tmp);
            Picasso.get()
                    .load(curFood.getImageUrl())
                    .resize(500, 500)
                    .transform(new IconBorder(20, Color.GREEN))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(tmp);
        }
        else{
            //it is a restaurant
            Target tmp = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.d("fan", "onBitmapLoaded: aaa");
                    LatLng location = new LatLng(curFood.getLocation().getLatitude(),
                            curFood.getLocation().getLongitude());
                    curMarker.position(location)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title(curFood.getTitle());
                    mGoogleMap.addMarker(curMarker);
                    foods.put(curMarker.getTitle(), curFood);
                    mTargets.remove(this);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            mTargets.add(tmp);
            Picasso.get()
                    .load(curFood.getImageUrl())
                    .resize(500, 500)
                    .transform(new IconBorder(20, Color.GREEN))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(tmp);
        }
    }


    //the callback function of requesting permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("fan", "onRequestPermissionsResult: bbb");
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d("fan", "onRequestPermissionsResult: aaa");
                startTracking();
            }
            else{
                requestPermissions(
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
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {

                // No explanation needed; request the permission
                requestPermissions(
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
        try {
            getActivity().unregisterReceiver(mTrackRecv);
        }
        catch (Exception ignored){
        }
        super.onDestroy();
    }

    //update the map when get a new location from track service
    private void getLocationCallback(Location location){
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

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            //check permission
            checkPermission();
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //display the blue dot and add map clicklistener
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            if (marker.equals(mNewFoodMarker)) {
                                Intent freeFoodIntent = new Intent(getActivity(), FreeFoodActivity.class);
                                freeFoodIntent.putExtra("latitude", marker.getPosition().latitude);
                                freeFoodIntent.putExtra("longitude", marker.getPosition().longitude);
                                startActivity(freeFoodIntent);
                            }
                        }
                    });
                    return null;
                }
            });
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (mNewFoodMarker == null) {
                        mNewFoodMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                                .title("Create a new Free Food!")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }
                    mNewFoodMarker.setPosition(latLng);
                    mNewFoodMarker.showInfoWindow();
                }
            });

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Intent showFoodIntent = new Intent(getActivity(), FoodDetailActivity.class);
                    if(foods.containsKey(marker.getTitle())) {
                        Log.d("fan", "onMarkerClick: " + marker.getTitle());
                        showFoodIntent.putExtra("Food", new Gson().toJson(foods.get(marker.getTitle())));
                        startActivity(showFoodIntent);
                    }
                    else{
                        Log.d(TAG, "onInfoWindowClick: there is no such a food");
                    }
                    return false;
                }
            });

            //set the listeners for the food data
            DatabaseReference databaseRef = mDB.getReference().child("Food");
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Food curFood = child.getValue(Food.class);
                        assert curFood != null;
                        setMarker(curFood);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


}

