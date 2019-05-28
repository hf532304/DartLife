package com.example.dartlife.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.dartlife.Const.Const;
import com.example.dartlife.R;
import com.example.dartlife.model.Food;
import com.example.dartlife.model.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.gson.Gson;
import com.sdsmdg.tastytoast.TastyToast;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.example.dartlife.Const.Const.MY_GET_IMAGE;
import static com.example.dartlife.Const.Const.MY_PERMISSIONS_CAMERA;
import static com.example.dartlife.Const.Const.MY_PERMISSIONS_PICK_PHOTO;
import static com.example.dartlife.Const.Const.MY_PICK_PHOTO;

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
    private Uri mFileUri;


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

        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Your Profile");
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mFileUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/mipmap/ic_launcher1");

        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

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
        mPasswordWidget.setText(loadProfile.getPassword());
        mDartmouthClassWidget.setText(loadProfile.getDartmouthClass());
        mPhoneWidget.setText(loadProfile.getPhone());
        Picasso.get()
                .load(loadProfile.getImageUrl())
                .into(mPhoto);
    }

    //get user's id
    private String getId() {
        return String.valueOf(Objects.requireNonNull(mEmailWidget.getText()).toString().hashCode());
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editprofile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.SaveProfileBtn) {
            TastyToast.makeText(getApplicationContext(), "This feature is under development", TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
        else if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }




    //the function is called when the user grants or denies the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_CAMERA){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //the user grants the permission
                takePhoto();
            }
            else{
                //the user denies the request
                Toast.makeText(getBaseContext(), "DartLife wants to use camera.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == MY_PERMISSIONS_PICK_PHOTO){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //the user grants the permission
                pickPhoto();
            }
            else{
                //the user denies the request
                Toast.makeText(getBaseContext(), "DartLife wants to access the external storage.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void beginCrop(Uri source) {
        Uri destination= Uri.fromFile(new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg"));
        mFileUri = destination;
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mPhoto.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void pickPhoto(){
        //the intent is for picking a photo from gallery
        Intent mPickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(mPickIntent, MY_PICK_PHOTO);
    }

    //to get a new picture from the camera or the gallery
    private void getPicture(){
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        String mTitle = "Profile Picture Picker";
        mDialog.setTitle(mTitle);

        mDialog.setItems(Const.mChoices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //the user chooses to take a photo by the camera
                    //first, check for the permission
                    if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        //if the app does not have the permission to use the camera
                        if(ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this,
                                Manifest.permission.CAMERA)){
                            ActivityCompat.requestPermissions(EditProfileActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                            //inform the user that the app needs the permission
                            Toast.makeText(getBaseContext(), "DartLife wants to use camera.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //request permission
                            ActivityCompat.requestPermissions(EditProfileActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                        }
                    }
                    else{
                        //the permission has been granted, we could take photo
                        takePhoto();
                    }
                }
                else if(which == 1){
                    if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //if the app does not have the permission to access external storage
                        if(ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            //inform the user that the app needs the permission
                            ActivityCompat.requestPermissions(EditProfileActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_PICK_PHOTO);
                            Toast.makeText(getBaseContext(), "DartLife wants to access the external storage",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //request permission
                            ActivityCompat.requestPermissions(EditProfileActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_PICK_PHOTO);
                        }
                    }
                    else{
                        pickPhoto();
                    }
                }
            }
        });
        mDialog.create().show();
    }

    //the function is to take the photo for the profile
    private void takePhoto() {
        //the intent is for taking the photo
        Intent mPhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File mFile = new File(EditProfileActivity.this.getExternalCacheDir(),
                System.currentTimeMillis() + ".jpg");
        mFileUri = Uri.fromFile(mFile);
        mPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivityForResult(mPhotoIntent, MY_GET_IMAGE);
    }

    //the function is to do some things when an activity is done
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //The user just gets the photo from camera, then we need to crop the photo
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MY_GET_IMAGE) {
                beginCrop(mFileUri);
            }
            // The user just crops the photo and show it in the imageview
            else if (requestCode == Crop.REQUEST_CROP) {
                // get the returned data
                handleCrop(resultCode, data);
            } else if (requestCode == MY_PICK_PHOTO) {
                mFileUri = data.getData();
                beginCrop(mFileUri);
            }
        }
    }

}
