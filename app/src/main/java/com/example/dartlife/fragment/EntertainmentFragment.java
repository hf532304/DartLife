package com.example.dartlife.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dartlife.R;
import com.example.dartlife.activity.EntertainmentActivity;
import com.example.dartlife.activity.EntertainmentInfoActivity;
import com.example.dartlife.adapter.EntertainmentAdapter;
import com.example.dartlife.model.Entertainment;
import com.example.dartlife.tools.IconBorder;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import com.squareup.picasso.Target;

public class EntertainmentFragment extends Fragment {

    private EntertainmentAdapter mSportAdapter;
    private ArrayList<Entertainment> mSportData;

    private FirebaseDatabase mDB;

    private EntertainmentAdapter mDramaAdapter;
    private ArrayList<Entertainment> mDramaData;

    private FloatingActionButton addButton;
    private SubActionButton sportButton;
    private SubActionButton dramaButton;
    private FloatingActionMenu actionMenu;

    private HashMap<String, Entertainment> Entertainments;
    private LinkedList<Target> mTargets;

    public EntertainmentFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_entertainment, container, false);

        mSportData = new ArrayList<>();
        mDramaData = new ArrayList<>();
        mTargets = new LinkedList<>();

        //the firebase handle
        mDB = FirebaseDatabase.getInstance();

        //setting for sport
        RecyclerView sportView = v.findViewById(R.id.SportContainer);
        sportView.setHasFixedSize(true);
        RecyclerView.LayoutManager sportLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        sportView.setLayoutManager(sportLayoutManager);
        mSportAdapter = new EntertainmentAdapter(getActivity(), mSportData);

        //setting for drama
        RecyclerView dramaView = v.findViewById(R.id.DramaContainer);
        dramaView.setHasFixedSize(true);
        RecyclerView.LayoutManager dramaLayoutManager = new LinearLayoutManager(getActivity()
                ,LinearLayoutManager.HORIZONTAL, false);
        dramaView.setLayoutManager(dramaLayoutManager);
        mDramaAdapter = new EntertainmentAdapter(getActivity(), mDramaData);

        mSportAdapter.setClickListener(new EntertainmentAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //go to the corresponding detail
                Entertainment selectedEntertainment = mSportData.get(position);
                Intent DetailedIntent = new Intent(getActivity(), EntertainmentInfoActivity.class);
                DetailedIntent.putExtra("entertainment", new Gson().toJson(selectedEntertainment));
                startActivity(DetailedIntent);
            }
        });

        mDramaAdapter.setClickListener(new EntertainmentAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //go to the corresponding detail
                Entertainment selectedEntertainment = mDramaData.get(position);
                Intent DetailedIntent = new Intent(getActivity(), EntertainmentInfoActivity.class);
                DetailedIntent.putExtra("entertainment", new Gson().toJson(selectedEntertainment));
                startActivity(DetailedIntent);
            }
        });

        sportView.setAdapter(mSportAdapter);
        dramaView.setAdapter(mDramaAdapter);

        //next, get the information of the data



        return v;
    }

    //when the fragment is visible, load the floating button
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            //setting of floating button

            ImageView sportIcon = new ImageView(getActivity()); // Create an icon
            sportIcon.setImageDrawable(getResources().getDrawable(R.drawable.sport));

            ImageView addIcon = new ImageView(getActivity());
            addIcon.setImageDrawable(getResources().getDrawable(R.drawable.add));

            ImageView dramaIcon = new ImageView(getActivity());
            dramaIcon.setImageDrawable(getResources().getDrawable(R.drawable.drama));

            addButton = new FloatingActionButton.Builder(Objects.requireNonNull(getActivity()))
                    .setContentView(addIcon)
                    .setPosition(3)
                    .build();

            SubActionButton.Builder itemBuilder = new SubActionButton.Builder(getActivity());
            sportButton = itemBuilder.setContentView(sportIcon).build();
            dramaButton = itemBuilder.setContentView(dramaIcon).build();

            sportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = new Intent(getActivity(), EntertainmentActivity.class);
                    newIntent.putExtra("type", "sport");
                    startActivity(newIntent);
                }
            });

            dramaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newIntent = new Intent(getActivity(), EntertainmentActivity.class);
                    newIntent.putExtra("type", "drama");
                    startActivity(newIntent);
                }
            });

            actionMenu = new FloatingActionMenu.Builder(getActivity())
                    .addSubActionView(sportButton)
                    .addSubActionView(dramaButton)
                    .attachTo(addButton)
                    .build();

            //set the listeners for the entertainment data
            mSportData.clear();
            mDramaData.clear();

            DatabaseReference databaseRef = mDB.getReference().child("Entertainment");
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Entertainment curEntertainment = child.getValue(Entertainment.class);
                        assert curEntertainment != null;
                        if(curEntertainment.getActivityType().equals("sport")){
                            mSportData.add(curEntertainment);
                            mSportAdapter.notifyDataSetChanged();
                            Log.d("fan", "onDataChange: addsport");
                        }
                        else if(curEntertainment.getActivityType().equals("drama")){
                            mDramaData.add(curEntertainment);
                            mDramaAdapter.notifyDataSetChanged();
                            Log.d("fan", "onDataChange: adddrama");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            if(addButton != null) {
                actionMenu.close(true);
                addButton.detach();
            }
        }
    }


}
