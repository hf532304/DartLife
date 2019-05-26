package com.example.dartlife.activity;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dartlife.R;
import com.example.dartlife.model.Entertainment;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.dartlife.Const.Const.REQUEST_ACCOUNT_PICKER;
import static com.example.dartlife.Const.Const.REQUEST_AUTHORIZATION;
import static com.example.dartlife.Const.Const.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.example.dartlife.Const.Const.REQUEST_PERMISSION_GET_ACCOUNTS;
import static com.example.dartlife.Const.Const.SCOPES;

public class EntertainmentInfoActivity extends AppCompatActivity {
    private TextView mEntertainmentDetailStartDateTimeView;
    private TextView mEntertainmentDetailTitleView;
    private TextView mEntertainmentDetailTypeView;
    private TextView mEntertainmentDetailCommentView;
    private ImageView mEntertainmentDetailImageView;
    private TextView mEntertainmentDetailEndDateTimeView;
    private SparkButton mFavoriteBtn;
    private GoogleAccountCredential mCredential;
    private Calendar mService = null;
    private Event event = null;

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment_info);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


        String EntertainmentStr = Objects.requireNonNull(getIntent().getExtras()).getString("entertainment");
        Entertainment theEntertainment = new Gson().fromJson(EntertainmentStr, Entertainment.class);

        //initialization of the widget
        mEntertainmentDetailCommentView = findViewById(R.id.EntertainmentDetailComment);
        mEntertainmentDetailStartDateTimeView = findViewById(R.id.EntertainmentDetailStartDateTimeView);
        mEntertainmentDetailTitleView = findViewById(R.id.EntertainmentDetailTitleView);
        mEntertainmentDetailTypeView = findViewById(R.id.EntertainmentDetailTypeView);
        mEntertainmentDetailImageView = findViewById(R.id.EntertainmentDetailImageView);
        mEntertainmentDetailEndDateTimeView = findViewById(R.id.EntertainmentDetailEndDateTimeView);
        mFavoriteBtn = findViewById(R.id.spark_button);


        //the event listener of the favorite button

        mFavoriteBtn.setEventListener(new SparkEventListener(){
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (buttonState) {
                    getResultsFromApi();
                } else {
                    new removeEvent().execute();
                    // TODO: remove the event from the google calendar
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

        //the setting of toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Free Entertainment Information");
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //the setting of views
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(theEntertainment.getStarttime());
        mEntertainmentDetailStartDateTimeView.setText("Time: " + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").
                format(cal.getTime()));
        cal.setTimeInMillis(theEntertainment.getEndtime());
        mEntertainmentDetailEndDateTimeView.setText("Time: " + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").
                format(cal.getTime()));
        Picasso.get()
                .load(theEntertainment.getImageUrl())
                .into(mEntertainmentDetailImageView);
        mEntertainmentDetailTitleView.setText("Title: " + theEntertainment.getTitle());
        mEntertainmentDetailTypeView.setText("EntertainmentType: " + theEntertainment.getActivityType());
        mEntertainmentDetailCommentView.setText("Description: " + theEntertainment.getDescription());

        //setting the dialog
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getResultsFromApi() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString("Account Name", null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            com.google.api.services.calendar.Calendar.Builder b =
                    new com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory, credential);
            b.setApplicationName(getApplication().getPackageName());
            mService = b.build();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                addEvent();
            } catch (Exception ignored) {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class removeEvent extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                    String calendarId = "primary";
                    mService.events().delete(calendarId, event.getId()).execute();
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                }
                catch (Exception e){
                    Log.d("fan", "addEvent: "+ e.toString());
                }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }
    }


    private void addEvent() {
        event = new Event()
                .setSummary(mEntertainmentDetailTitleView.getText().toString())
                .setDescription(mEntertainmentDetailCommentView.getText().toString());

        event.setId("event" + new Random().nextInt(1000));

        DateTime startDateTime = new DateTime(mEntertainmentDetailStartDateTimeView.getText().toString().substring(6));
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/New_York");
        event.setStart(start);

        DateTime endDateTime = new DateTime(mEntertainmentDetailEndDateTimeView.getText().toString().substring(6));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/New_York");
        event.setEnd(end);

        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
        event.setRecurrence(Arrays.asList(recurrence));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("popup").setMinutes(20),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        try {
            mService.events().insert(calendarId, event).execute();
        } catch (UserRecoverableAuthIOException e) {
            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        }
        catch (Exception e){
            Log.d("fan", "addEvent: "+ e.toString());
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("Account_Name", accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
        }
    }
}
