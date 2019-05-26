package com.example.dartlife.Const;


import com.google.api.services.calendar.CalendarScopes;

public class Const {

    //number const
    public static final int MY_GET_IMAGE = 0;
    public static final int MY_PERMISSIONS_CAMERA = 1;
    public static final int MY_PERMISSIONS_PICK_PHOTO = 2;
    public static final int MY_PICK_PHOTO = 3;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 4;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 5;
    public static final int REQUEST_ACCOUNT_PICKER = 6;
    public static final int REQUEST_AUTHORIZATION = 7;

    public static final String[] SCOPES = {"https://www.googleapis.com/auth/calendar"};

    //string const
    public static final String mChoices[] = new String[]{"Take from camera", "Select from gallery"};

}
