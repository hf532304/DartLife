package com.example.dartlife.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.example.dartlife.tools.IconBorder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

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

public class FoodDetailActivity extends AppCompatActivity {
    private TextView mFoodDetailDateTimeView;
    private TextView mFoodDetailTitleView;
    private TextView mFoodDetailFoodTypeView;
    private TextView mFoodDetailCommentView;
    private ImageView mFoodDetailImageView;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        String foodStr = Objects.requireNonNull(getIntent().getExtras()).getString("Food");
        Food theFood = new Gson().fromJson(foodStr, Food.class);

        //initialization of the widget
        mFoodDetailCommentView = findViewById(R.id.FoodDetailComment);
        mFoodDetailDateTimeView = findViewById(R.id.FoodDetailDateTimeView);
        mFoodDetailTitleView = findViewById(R.id.FoodDetailTitleView);
        mFoodDetailFoodTypeView = findViewById(R.id.FoodDetailFoodTypeView);
        mFoodDetailImageView = findViewById(R.id.FoodDetailImageView);

        //the setting of toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Free Food Information");
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //the setting of views
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(theFood.getTime());
        mFoodDetailDateTimeView.setText("Time: " + new SimpleDateFormat("yyyy-MM-dd : HH:mm").
                format(cal.getTime()));
        Picasso.get()
                .load(theFood.getImageUrl())
                .into(mFoodDetailImageView);
        mFoodDetailTitleView.setText("Title: " + theFood.getTitle());
        mFoodDetailFoodTypeView.setText("FoodType: "+ theFood.getmFoodType());
        mFoodDetailCommentView.setText("Comment: " + theFood.getComment());

        //setting the dialog
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
