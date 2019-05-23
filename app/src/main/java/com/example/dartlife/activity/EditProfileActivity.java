package com.example.dartlife.activity;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.dartlife.R;
import com.example.dartlife.model.Food;
import com.example.dartlife.model.Profile;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

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
    private Button mEditProfileButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mNameWidget = findViewById(R.id.EditProfileNameEditText);
        mEmailWidget = findViewById(R.id.EditProfileEmailEditText);
        mPasswordWidget = findViewById(R.id.EditProfilePwdEditText);
        mPhoneWidget = findViewById(R.id.EditProfilePhoneEditText);
        mMajorWidget = findViewById(R.id.EditProfileMajorEditText);
        mDartmouthClassWidget = findViewById(R.id.EditProfileClassEditText);
        mGenderWidget = findViewById(R.id.EditProfileGenderGroup);
        mMaleButton = findViewById(R.id.EditProfileMaleBtn);
        mFemaleButton = findViewById(R.id.EditProfileFemaleBtn);
        mPhoto = findViewById(R.id.EditProfileImageView);
        mEditProfileButton = findViewById(R.id.EditProfileChangeBtn);

        String profileStr = Objects.requireNonNull(getIntent().getExtras()).getString("profile");
        Profile selectedProfile = new Gson().fromJson(profileStr, Profile.class);
        setView(selectedProfile);
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
        mDartmouthClassWidget.setText(loadProfile.getDartmouthClass());
        mPhoneWidget.setText(loadProfile.getPhone());
        Picasso.get()
                .load(loadProfile.getImageUrl())
                .into(mPhoto);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else if(item.getItemId() == R.id.SaveProfileBtn){
            saveProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProfile() {
    }
}
