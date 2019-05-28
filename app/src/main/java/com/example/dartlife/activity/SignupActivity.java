package com.example.dartlife.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.example.dartlife.model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.example.dartlife.Const.Const.MY_GET_IMAGE;
import static com.example.dartlife.Const.Const.MY_PERMISSIONS_CAMERA;
import static com.example.dartlife.Const.Const.MY_PERMISSIONS_PICK_PHOTO;
import static com.example.dartlife.Const.Const.MY_PICK_PHOTO;

public class SignupActivity extends AppCompatActivity {

    //the firebase variables
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseDatabase mDB;
    private FirebaseVisionImage mImage = null;
    private FirebaseVisionTextRecognizer mDetector = FirebaseVision.getInstance()
            .getOnDeviceTextRecognizer();

    //for cropping a photo
    private Uri mFileUri;

    //for the Profile message
    private String mProfileGender = "";
    private String mProfileName = "";
    private String mProfileEmail = "";
    private String mProfilePassword = "";
    private String mProfilePhone = "";
    private String mProfileMajor = "";
    private String mProfileDartmouthClass = "";
    //the user's choice of changing the photo of the profile

    //the following is the widget in the profile
    private TextInputEditText mNameWidget = null;
    private TextInputEditText mEmailWidget = null;
    private TextInputEditText mPasswordWidget = null;
    private TextInputEditText mPhoneWidget = null;
    private TextInputEditText mMajorWidget = null;
    private TextInputEditText mDartmouthClassWidget = null;
    private TextInputEditText mDOBWidget = null;
    private TextInputEditText mUIDWidget = null;
    private RadioGroup mGenderWidget = null;
    private RadioButton mMaleButton = null;
    private RadioButton mFemaleButton = null;
    private ImageView mPhoto = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Objects.requireNonNull(getSupportActionBar()).setTitle("SignUp");
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize of the face detector

        //the mChange button is to change the photo of the profile
        Button mChange = findViewById(R.id.SignUpChangeBtn);
        mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

        //initialization of the firebase stuff
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDB = FirebaseDatabase.getInstance();

        //initialization of the widgets
        mNameWidget = findViewById(R.id.SignUpNameTextInput);
        mEmailWidget = findViewById(R.id.SignUpEmailEditText);
        mPasswordWidget = findViewById(R.id.SignUpPwdEditText);
        mPhoneWidget = findViewById(R.id.SignUpPhoneEditText);
        mMajorWidget = findViewById(R.id.SignUpMajorEditText);
        mDartmouthClassWidget = findViewById(R.id.SignUpClassEditText);
        mGenderWidget = findViewById(R.id.SignUpGenderGroup);
        mMaleButton = findViewById(R.id.SignUpMaleBtn);
        mFemaleButton = findViewById(R.id.SignUpFemaleBtn);
        mFileUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/mipmap/ic_launcher1");
        mPhoto = findViewById(R.id.SignUpImageView);
        mDOBWidget = findViewById(R.id.SignUpDOBEditText);
        mUIDWidget = findViewById(R.id.SignUpUIDEditText);
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
                    if(ContextCompat.checkSelfPermission(SignupActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        //if the app does not have the permission to use the camera
                        if(ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this,
                                Manifest.permission.CAMERA)){
                            ActivityCompat.requestPermissions(SignupActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                            //inform the user that the app needs the permission
                            Toast.makeText(getBaseContext(), "DartLife wants to use camera.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //request permission
                            ActivityCompat.requestPermissions(SignupActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                        }
                    }
                    else{
                        //the permission has been granted, we could take photo
                        takePhoto();
                    }
                }
                else if(which == 1){
                    if(ContextCompat.checkSelfPermission(SignupActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //if the app does not have the permission to access external storage
                        if(ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            //inform the user that the app needs the permission
                            ActivityCompat.requestPermissions(SignupActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_PICK_PHOTO);
                            Toast.makeText(getBaseContext(), "DartLife wants to access the external storage",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //request permission
                            ActivityCompat.requestPermissions(SignupActivity.this,
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
        File mFile = new File(SignupActivity.this.getExternalCacheDir(),
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

    //the function is to register an user, check whether the requirements are fulfilled
    private void saveProfile(){
        //first, updates the variables and checks them
        //then, save the information in sharedpreference
        getCurrentProfile();
        //check the name
        if(Objects.requireNonNull(mNameWidget.getText()).toString().matches("")){
            mNameWidget.requestFocus();
            mNameWidget.setError("This field is required");
        }
        //check the gender
        else if(mGenderWidget.getCheckedRadioButtonId() == -1){
            Toast.makeText(getBaseContext(), "Gender is required!",
                    Toast.LENGTH_SHORT).show();
        }
        //check whether the email is empty
        else if(Objects.requireNonNull(mEmailWidget.getText()).toString().matches("")){
            mEmailWidget.requestFocus();
            mEmailWidget.setError("This field is required");
        }
        //check whether the email is invalid
        else if(!Patterns.EMAIL_ADDRESS.matcher(mProfileEmail).matches()){
            mEmailWidget.setError("This email address is invalid");
        }
        //check whether the password is empty
        else if(Objects.requireNonNull(mPasswordWidget.getText()).toString().matches("")){
            mPasswordWidget.requestFocus();
            mPasswordWidget.setError("This field is required");
        }
        //check whether the password is shorter than 6
        else if(mProfilePassword.length() < 6){
            mPasswordWidget.requestFocus();
            mPasswordWidget.setError("Password must be at least 6 characters");
        }
        else if((mNameWidget.isEnabled() || mDOBWidget.isEnabled() ||
                mUIDWidget.isEnabled() || mDartmouthClassWidget.isEnabled())){
            Toast.makeText(this, "Authentication fails", Toast.LENGTH_LONG).show();
        }
        else {
            //the logic: firstly, store the image
            //and register with firebase auth
            //secondly, get the downloadurl of the pic
            //thirdly, save into firebase realtime db

            //some info we have gotten
            final Profile savingProfile = new Profile();
            savingProfile.setName(mProfileName);
            savingProfile.setDartmouthClass(mProfileDartmouthClass);
            savingProfile.setGender(mProfileGender);
            savingProfile.setEmail(mProfileEmail);
            savingProfile.setMajor(mProfileMajor);
            savingProfile.setPhone(mProfilePhone);
            savingProfile.setPassword(mProfilePassword);
            savingProfile.setDOB(Objects.requireNonNull(mDOBWidget.getText()).toString());
            savingProfile.setUID(Objects.requireNonNull(mUIDWidget.getText()).toString());


            mAuth.createUserWithEmailAndPassword(mProfileEmail, mProfilePassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getBaseContext(), "Authentication successfully: "+
                                                Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getBaseContext(), "Authentication failed: "+
                                                Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            mAuth.signInWithEmailAndPassword(mProfileEmail, mProfilePassword)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    //next, save the image to firebase storage
                    final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
                    progressDialog.setTitle("Uploading Image");
                    progressDialog.show();
                    final StorageReference ProfileImageDir = mStorageRef.child("Dartlife").child("Profile").
                            child(getId() + "_" + System.currentTimeMillis() +".jpg");
                    Log.d("fan", "saveProfile: " + mFileUri.toString());
                    ProfileImageDir.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //next, get the download Url
                            ProfileImageDir.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                                @Override
                                public void onSuccess(@NonNull Uri uri) {
                                    savingProfile.setImageUrl(uri.toString());
                                    //next, add the node to the realtime firebase
                                    DatabaseReference DBRef = mDB.getReference().child("Profile");
                                    progressDialog.setTitle("Uploading Profile");
                                    progressDialog.show();
                                    DBRef.child(getId()).setValue(savingProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            //next, create the user in authentication
                                            Toast.makeText(getBaseContext(), "Successfully Registered",
                                                    Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    //update the variables for saving the user's profile
    private void getCurrentProfile(){
        if(!TextUtils.isEmpty(mNameWidget.getText())) {
            //when the text is not null
            mProfileName = Objects.requireNonNull(mNameWidget.getText()).toString();
        }
        if(!TextUtils.isEmpty(mEmailWidget.getText())) {
            //when the text is not null
            mProfileEmail = Objects.requireNonNull(mEmailWidget.getText()).toString();
        }
        if(!TextUtils.isEmpty(mPasswordWidget.getText())) {
            //when the text is not null
            mProfilePassword = Objects.requireNonNull(mPasswordWidget.getText()).toString();
        }
        if(!TextUtils.isEmpty(mPhoneWidget.getText())) {
            //when the text is not null
            mProfilePhone = Objects.requireNonNull(mPhoneWidget.getText()).toString();
        }
        if(!TextUtils.isEmpty(mMajorWidget.getText())) {
            //when the text is not null
            mProfileMajor = Objects.requireNonNull(mMajorWidget.getText()).toString();
        }
        if(!TextUtils.isEmpty(mDartmouthClassWidget.getText())) {
            //when the text is not null
            mProfileDartmouthClass = Objects.requireNonNull(mDartmouthClassWidget.getText()).toString();
        }
        if(!TextUtils.isEmpty(mDartmouthClassWidget.getText())) {
            //when the text is not null
            mProfileDartmouthClass = Objects.requireNonNull(mDartmouthClassWidget.getText()).toString();
        }
        if (mGenderWidget.getCheckedRadioButtonId() != -1) {
            //one of the buttons is checked
            if(mMaleButton.isChecked()){
                //Male
                mProfileGender = "Male";
            }
            else{
                mProfileGender = "Female";
            }
        }
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
            try {
                final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
                progressDialog.setTitle("Detecting Text...");
                progressDialog.show();
                mImage = FirebaseVisionImage.fromFilePath(getBaseContext(), mFileUri);
                Task<FirebaseVisionText> res =
                        mDetector.processImage(mImage)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getBaseContext(), "Recognition Success!",
                                                Toast.LENGTH_LONG).show();
                                        //get the name and class of the text
                                        for(FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()){
                                            for(FirebaseVisionText.Line line: block.getLines()){
                                                if(line.getText().trim().endsWith(",")) {
                                                    mNameWidget.setText(line.getText().replace(',', ' '));
                                                    mNameWidget.setEnabled(false);
                                                }
                                                else if(line.getText().startsWith("DOB")){
                                                    String DOB = line.getText().trim();
                                                    int i = 0;
                                                    for(; i < DOB.length(); i++){
                                                        if(Character.isDigit(DOB.charAt(i))){
                                                            break;
                                                        }
                                                    }
                                                    DOB = DOB.substring(i);
                                                    mDOBWidget.setText(DOB);
                                                    mDOBWidget.setEnabled(false);
                                                }
                                                else if(line.getText().startsWith("DID")){
                                                    String DID = line.getText().trim();
                                                    int i = 0;
                                                    for(; i < DID.length(); i++){
                                                        if(DID.charAt(i) == ':'){
                                                            break;
                                                        }
                                                    }
                                                    DID = DID.substring(i + 1);
                                                    mUIDWidget.setText(DID);
                                                    mUIDWidget.setEnabled(false);
                                                }
                                                else if(line.getText().toUpperCase().startsWith("ISSUE")){
                                                    String Date = line.getText().trim();
                                                    Log.d("fan", "onSuccess111: " + Date);
                                                    Date = Date.substring(Date.length() - 4);
                                                    try {
                                                        Log.d("fan", "onSuccess: " + Date);
                                                        Integer.parseInt(Date);
                                                        mDartmouthClassWidget.setText(
                                                                String.valueOf(Integer.valueOf(Date) + 4));
                                                        mDartmouthClassWidget.setEnabled(false);
                                                    }
                                                    catch (NumberFormatException ignored){
                                                    }
                                                }
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getBaseContext(), "The picture is too" +
                                                        "blur, please take again!", Toast.LENGTH_LONG).show();
                                            }
                                        });
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.RegisterButton) {
            saveProfile();
        }
        else if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //get user's id
    private String getId() {
        return String.valueOf(mProfileEmail.hashCode());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}
