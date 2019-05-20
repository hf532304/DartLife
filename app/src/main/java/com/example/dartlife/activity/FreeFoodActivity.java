package com.example.dartlife.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dartlife.R;
import com.example.dartlife.model.Food;
import com.example.dartlife.model.MyLatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.example.dartlife.Const.Const.MY_GET_IMAGE;
import static com.example.dartlife.Const.Const.MY_PERMISSIONS_CAMERA;
import static com.example.dartlife.Const.Const.MY_PERMISSIONS_PICK_PHOTO;
import static com.example.dartlife.Const.Const.MY_PICK_PHOTO;
import static com.example.dartlife.Const.Const.mChoices;

public class
FreeFoodActivity extends AppCompatActivity {

    private TextView mFreeFoodTimeView;
    private TextView mFreeFoodDateView;
    private Spinner mFreeFoodTypeSpinner;
    private EditText mFreeFoodComment;
    private TextInputEditText mFreeFoodTitle;
    private Button mChangeBtn;
    private Uri mFileUri;
    private ImageView mFreeFoodImageView;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDB;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_food);

        //initialization of the widget
        mFreeFoodTimeView = findViewById(R.id.FreeFoodTimeView);
        mFreeFoodDateView = findViewById(R.id.FreeFoodDateView);
        mFreeFoodTypeSpinner = findViewById(R.id.FoodTypeSpinner);
        mFreeFoodComment = findViewById(R.id.FreeFoodCommentEditText);
        mFreeFoodTitle = findViewById(R.id.FreeFoodTitleEditText);
        mChangeBtn = findViewById(R.id.ChangeFoodBtn);
        mFreeFoodImageView = findViewById(R.id.FoodImageView);

        //initialization of firebase stuff
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance();

        //the setting of toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("New Free Food");
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //the setting time/datepickerdialog
        mFreeFoodDateView.setText(new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(new Date()));
        mFreeFoodDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mCalendar = Calendar.getInstance();
                DatePickerDialog mDatePicker = new DatePickerDialog(FreeFoodActivity.this,
                        new DatePickerDialog.OnDateSetListener(){
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, month);
                                mCalendar.set(Calendar.DATE, dayOfMonth);
                                String result = new SimpleDateFormat("yyyy-MM-dd",
                                        Locale.getDefault()).format(mCalendar.getTime());
                                mFreeFoodDateView.setText(result);
                            }
                        },
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.show();
            }
        });

        mFreeFoodTimeView.setText(new SimpleDateFormat("HH:mm").
                format(Calendar.getInstance().getTime()));
        mFreeFoodTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(FreeFoodActivity.this ,new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String result = String.format("%02d:%02d", hourOfDay, minute);
                        mFreeFoodTimeView.setText(result);
                    }
                }, 0, 0, true);
                mTimePicker.show();
            }
        });

        //setting the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.food_type_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mFreeFoodTypeSpinner.setAdapter(adapter);

        //setting the dialog
        mFreeFoodComment.setHint("Comment");

        //setting the change button
        mChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

    }

    //save the information of food in firebase storage and realtime database
    private void saveFood() {
        Food curFood = new Food();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String dateTime = mFreeFoodDateView.getText().toString() + mFreeFoodTimeView.getText().toString();
        try {
            cal.setTime(mFormat.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        curFood.setTime(cal.getTimeInMillis());
        curFood.setComment(mFreeFoodComment.getText().toString());
        curFood.setTitle(Objects.requireNonNull(mFreeFoodTitle.getText()).toString());
        curFood.setmFoodType(mFreeFoodTypeSpinner.getSelectedItem().toString());
        curFood.setFoodSource("FreeFood");
        MyLatLng location = new MyLatLng();
        double La = Objects.requireNonNull(getIntent().getExtras()).getDouble("latitude", 0);
        double Lo = getIntent().getExtras().getDouble("longitude", 0);
        location.setLongitude(Lo);
        location.setLatitude(La);
        curFood.setLocation(location);

        //storage the image file at first, and then upload the rest of information
        storeImage(curFood);
    }

    //to store the iimage to the firebase and then upload the rest to the realtime database
    private void storeImage(final Food curFood){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();
        final StorageReference FoodImageDir = mStorageRef.child("Dartlife").child("Food").
                child(getId() + "_" + System.currentTimeMillis() +".jpg");
        FoodImageDir.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FoodImageDir.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        curFood.setImageUrl(uri.toString());
                        Log.d("fan", "onSuccess: " + uri.toString());
                        //next, add the node to the realtime firebase
                        DatabaseReference DBRef = mDB.getReference().child("Food");
                        progressDialog.setTitle("Uploading Food");
                        progressDialog.show();
                        DBRef.push().setValue(curFood).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "Upload the food successfully!",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.freefood_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.post_btn) {
            saveFood();
        }
        return super.onOptionsItemSelected(item);
    }

    //the function is to take the photo for the profile
    private void takePhoto() {
        Intent mPhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File mFile = new File(FreeFoodActivity.this.getExternalCacheDir(),
                System.currentTimeMillis() + ".jpg");
        mFileUri = Uri.fromFile(mFile);
        mPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        startActivityForResult(mPhotoIntent, MY_GET_IMAGE);
    }

    //to get a new picture from the camera or the gallery
    private void getPicture(){
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        String mTitle = "Profile Picture Picker";
        mDialog.setTitle(mTitle);

        mDialog.setItems(mChoices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    //the user chooses to take a photo by the camera
                    //first, check for the permission
                    if(ContextCompat.checkSelfPermission(FreeFoodActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        //if the app does not have the permission to use the camera
                        if(ActivityCompat.shouldShowRequestPermissionRationale(FreeFoodActivity.this,
                                Manifest.permission.CAMERA)){
                            ActivityCompat.requestPermissions(FreeFoodActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                            //inform the user that the app needs the permission
                            Toast.makeText(getBaseContext(), "DartLife wants to use camera.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //request permission
                            ActivityCompat.requestPermissions(FreeFoodActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                        }
                    }
                    else{
                        //the permission has been granted, we could take photo
                        takePhoto();
                    }
                }
                else if(which == 1){
                    if(ContextCompat.checkSelfPermission(FreeFoodActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //if the app does not have the permission to access external storage
                        if(ActivityCompat.shouldShowRequestPermissionRationale(FreeFoodActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            //inform the user that the app needs the permission
                            ActivityCompat.requestPermissions(FreeFoodActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_PICK_PHOTO);
                            Toast.makeText(getBaseContext(), "DartLife wants to access the external storage",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //request permission
                            ActivityCompat.requestPermissions(FreeFoodActivity.this,
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
                Toast.makeText(getBaseContext(), "Myrun wants to use camera.",
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
                Toast.makeText(getBaseContext(), "Myrun wants to access the external storage.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //to pick a photo from the gallery
    private void pickPhoto(){
        //the intent is for picking a photo from gallery
        Intent mPickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(mPickIntent, MY_PICK_PHOTO);
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

    //to crop the picture
    private void beginCrop(Uri source) {
        Uri destination= Uri.fromFile(new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg"));
        mFileUri = destination;
        Crop.of(source, destination).asSquare().start(this);
    }

    //handle the result of picture
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mFreeFoodImageView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //get user's id
    private String getId() {
        return String.valueOf(Objects.requireNonNull(Objects.requireNonNull(
                mAuth.getCurrentUser()).getEmail()).hashCode());

    }

}
