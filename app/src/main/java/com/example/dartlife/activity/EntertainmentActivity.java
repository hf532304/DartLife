package com.example.dartlife.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.example.dartlife.R;
import com.example.dartlife.model.Entertainment;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class EntertainmentActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    private TextView mEntertainmentPeriodView;
    private TextView mEntertainmentTypeView;
    private EditText mEntertainmentDes;
    private TextInputEditText mEntertainmentTitle;
    private Button mChangeBtn;
    private Uri mFileUri;
    private ImageView mEntertainmentImageView;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDB;
    private Calendar mCalendar;
    private String mBeginDate;
    private String mBeginTime;
    private String mEndDate;
    private String mEndTime;
    private TimePickerDialog timeDialog;
    private DatePickerDialog dateDialog;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment);

        //initialization of the widget
        mEntertainmentPeriodView = findViewById(R.id.entertainmentPeriod);
        mEntertainmentTypeView = findViewById(R.id.EntertainmentTypeView);
        mEntertainmentDes = findViewById(R.id.EntertainmentCommentEditText);
        mEntertainmentTitle = findViewById(R.id.EntertainmentTitleEditText);
        mChangeBtn = findViewById(R.id.ChangeEntertainmentBtn);
        mEntertainmentImageView = findViewById(R.id.EntertainmentImageView);

        //initialization of the calendar
        mCalendar = Calendar.getInstance();


        //initialization of the calendar picker
        timeDialog = TimePickerDialog.newInstance(
                EntertainmentActivity.this,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                false
        );

        dateDialog = DatePickerDialog.newInstance(
                EntertainmentActivity.this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
        );

        //initialization of firebase stuff
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance();

        //the setting of toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("New Entertainment");
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //the setting time/datepickerdialog

        String defaultPeroid = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date()) + " To " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date());
        mEntertainmentPeriodView.setText(defaultPeroid);
        mEntertainmentPeriodView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

//
//        mEntertainmentStartTimeView.setText(new SimpleDateFormat("HH:mm").
//                format(Calendar.getInstance().getTime()));
//        mEntertainmentStartTimeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TimePickerDialog mTimePicker = new TimePickerDialog(EntertainmentActivity.this ,new TimePickerDialog.OnTimeSetListener() {
//                    @SuppressLint("DefaultLocale")
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        String result = String.format("%02d:%02d", hourOfDay, minute);
//                        mEntertainmentStartTimeView.setText(result);
//                    }
//                }, 0, 0, true);
//                mTimePicker.show();
//            }
//        });
//
//
//        mEntertainmentEndDateView.setText(new SimpleDateFormat("yyyy-MM-dd",
//                Locale.getDefault()).format(new Date()));
//        mEntertainmentEndDateView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Calendar mCalendar = Calendar.getInstance();
//                DatePickerDialog mDatePicker = new DatePickerDialog(EntertainmentActivity.this,
//                        new DatePickerDialog.OnDateSetListener(){
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                                mCalendar.set(Calendar.YEAR, year);
//                                mCalendar.set(Calendar.MONTH, month);
//                                mCalendar.set(Calendar.DATE, dayOfMonth);
//                                String result = new SimpleDateFormat("yyyy-MM-dd",
//                                        Locale.getDefault()).format(mCalendar.getTime());
//                                mEntertainmentEndDateView.setText(result);
//                            }
//                        },
//                        mCalendar.get(Calendar.YEAR),
//                        mCalendar.get(Calendar.MONTH),
//                        mCalendar.get(Calendar.DAY_OF_MONTH));
//                mDatePicker.show();
//            }
//        });
//
//        mEntertainmentEndTimeView.setText(new SimpleDateFormat("HH:mm").
//                format(Calendar.getInstance().getTime()));
//        mEntertainmentEndTimeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TimePickerDialog mTimePicker = new TimePickerDialog(EntertainmentActivity.this ,new TimePickerDialog.OnTimeSetListener() {
//                    @SuppressLint("DefaultLocale")
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        String result = String.format("%02d:%02d", hourOfDay, minute);
//                        mEntertainmentEndTimeView.setText(result);
//                    }
//                }, 0, 0, true);
//                mTimePicker.show();
//            }
//        });
//


        //setting the spinner
        String EntertainmentType = Objects.requireNonNull(getIntent().getExtras()).getString("type");
        mEntertainmentTypeView.setText(EntertainmentType);
        mEntertainmentTypeView.setEnabled(false);

        //setting the dialog
        mEntertainmentDes.setHint("Comment");

        //setting the change button
        mChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

        mFileUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/mipmap/ic_launcher1");

    }

    //save the information of entertainment in firebase storage and realtime database
    private void saveEntertainment() {
        Entertainment curEntertainment = new Entertainment();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String startDateTime = mBeginDate + " " + mBeginTime;
        try {
            cal.setTime(mFormat.parse(startDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        curEntertainment.setStarttime(cal.getTimeInMillis());

        String endDateTime = mEndDate + " " + mEndTime;
        try {
            cal.setTime(mFormat.parse(endDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        curEntertainment.setEndtime(cal.getTimeInMillis());
        curEntertainment.setDescription(mEntertainmentDes.getText().toString());
        curEntertainment.setTitle(Objects.requireNonNull(mEntertainmentTitle.getText()).toString());
        curEntertainment.setActivityType(mEntertainmentTypeView.getText().toString());
        //storage the image file at first, and then upload the rest of information
        storeImage(curEntertainment);
    }

    //to store the image to the firebase and then upload the rest to the realtime database
    private void storeImage(final Entertainment curEntertainment){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();
        final StorageReference EntertainmentImageDir = mStorageRef.child("Dartlife").child("Entertainment").
                child(getId() + "_" + System.currentTimeMillis() +".jpg");
        EntertainmentImageDir.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                EntertainmentImageDir.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        curEntertainment.setImageUrl(uri.toString());
                        Log.d("fan", "onSuccess: " + uri.toString());
                        //next, add the node to the realtime firebase
                        DatabaseReference DBRef = mDB.getReference().child("Entertainment");
                        progressDialog.setTitle("Uploading Entertainment");
                        progressDialog.show();
                        DBRef.push().setValue(curEntertainment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "Upload the entertainment successfully!",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }




    //the function is to take the photo for the profile
    private void takePhoto() {
        Intent mPhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File mFile = new File(EntertainmentActivity.this.getExternalCacheDir(),
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
                    if(ContextCompat.checkSelfPermission(EntertainmentActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        //if the app does not have the permission to use the camera
                        if(ActivityCompat.shouldShowRequestPermissionRationale(EntertainmentActivity.this,
                                Manifest.permission.CAMERA)){
                            ActivityCompat.requestPermissions(EntertainmentActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                            //inform the user that the app needs the permission
                            Toast.makeText(getBaseContext(), "DartLife wants to use camera.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //request permission
                            ActivityCompat.requestPermissions(EntertainmentActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
                        }
                    }
                    else{
                        //the permission has been granted, we could take photo
                        takePhoto();
                    }
                }
                else if(which == 1){
                    if(ContextCompat.checkSelfPermission(EntertainmentActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        //if the app does not have the permission to access external storage
                        if(ActivityCompat.shouldShowRequestPermissionRationale(EntertainmentActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            //inform the user that the app needs the permission
                            ActivityCompat.requestPermissions(EntertainmentActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_PICK_PHOTO);
                            Toast.makeText(getBaseContext(), "DartLife wants to access the external storage",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //request permission
                            ActivityCompat.requestPermissions(EntertainmentActivity.this,
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
            mEntertainmentImageView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //get user's id
    private String getId() {
        return String.valueOf(Objects.requireNonNull(Objects.requireNonNull(
                mAuth.getCurrentUser()).getEmail()).hashCode());
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.entertainment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.addEntertainmentBtn) {
            saveEntertainment();
        }
        else if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //listen event for calendar picker
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        //String date = "You picked the following date: From- "+dayOfMonth+"/"+(++monthOfYear)+"/"+year+" To "+dayOfMonthEnd+"/"+(++monthOfYearEnd)+"/"+yearEnd;
        mBeginDate = year + "-" + (++monthOfYear) + "-" + dayOfMonth;
        mEndDate = yearEnd + "-" + (++monthOfYearEnd) + "-" + dayOfMonthEnd;
        timeDialog.show(getFragmentManager(), "TimepickerDialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0"+hourOfDayEnd : ""+hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0"+minuteEnd : ""+minuteEnd;
        //String time = "You picked the following time: From - "+hourString+"h"+minuteString+" To - "+hourStringEnd+"h"+minuteStringEnd;

        mBeginTime = hourString + ":" + minuteString;
        mEndTime = hourStringEnd + ":" + minuteStringEnd;

        String periodStr = mBeginDate + " " + mBeginTime + " To " + mEndDate + " " + mEndTime;

        mEntertainmentPeriodView.setText(periodStr);
    }


}
