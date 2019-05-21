package com.example.dartlife.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.example.dartlife.R;
import com.example.dartlife.model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    //firebase tools
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;


    //the following is the widget in the profile
    private TextInputEditText mNameWidget = null;
    private TextInputEditText mEmailWidget = null;
    private TextInputEditText mPasswordWidget = null;
    private TextInputEditText mPhoneWidget = null;
    private TextInputEditText mMajorWidget = null;
    private TextInputEditText mDartmouthClassWidget = null;
    private RadioGroup mGenderWidget = null;
    private RadioButton mMaleButton = null;
    private RadioButton mFemaleButton = null;
    private ImageView mPhoto = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View cur_view = inflater.inflate(R.layout.fragment_profile, container, false);

        mNameWidget = cur_view.findViewById(R.id.ProfileNameEditText);
        mEmailWidget = cur_view.findViewById(R.id.ProfileEmailEditText);
        mPasswordWidget = cur_view.findViewById(R.id.ProfilePwdEditText);
        mPhoneWidget = cur_view.findViewById(R.id.ProfilePhoneEditText);
        mMajorWidget = cur_view.findViewById(R.id.ProfileMajorEditText);
        mDartmouthClassWidget = cur_view.findViewById(R.id.ProfileClassEditText);
        mGenderWidget = cur_view.findViewById(R.id.ProfileGenderGroup);
        mMaleButton = cur_view.findViewById(R.id.ProfileMaleBtn);
        mFemaleButton = cur_view.findViewById(R.id.ProfileFemaleBtn);
        mPhoto = cur_view.findViewById(R.id.ProfileImageView);

        //initialize the firebase tool
        mDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //set the listener
        mDB.getReference().child("Profile").child(getid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Profile loadProfile = dataSnapshot.getValue(Profile.class);
                assert loadProfile != null;
                setView(loadProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return cur_view;
    }

    //set the value of different view in this fragment
    private void setView(Profile loadProfile) {
        mNameWidget.setText(loadProfile.getName());
        mNameWidget.setEnabled(false);
        mEmailWidget.setText(loadProfile.getEmail());
        mEmailWidget.setEnabled(false);
        if(loadProfile.getGender().equals("Male")){
            mMaleButton.toggle();
        }
        else{
            mFemaleButton.toggle();
        }
        mMaleButton.setEnabled(false);
        mFemaleButton.setEnabled(false);
        mMajorWidget.setText(loadProfile.getMajor());
        mMajorWidget.setEnabled(false);
        mPasswordWidget.setText(loadProfile.getPassword());
        mPasswordWidget.setEnabled(false);
        mDartmouthClassWidget.setText(loadProfile.getDartmouthClass());
        mDartmouthClassWidget.setEnabled(false);
        mPhoneWidget.setText(loadProfile.getPhone());
        mPhoneWidget.setEnabled(false);
        Picasso.get()
                .load(loadProfile.getImageUrl())
                .into(mPhoto);
    }

    //get user's id
    private String getid() {
        return String.valueOf(Objects.requireNonNull(Objects.requireNonNull(
                mAuth.getCurrentUser()).getEmail()).hashCode());
    }

}
