package com.vanmove.passesger.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class RangeTimePicker extends TimePicker implements
        TimePicker.OnTimeChangedListener {

    private int mMinHour = -1;

    private int mMinMinute = -1;

    private int mMaxHour = 25;

    private int mMaxMinute = 60;

    private int mCurrentHour;

    private int mCurrentMinute;


    public RangeTimePicker(Context context) {
        super(context);
        setOnTimeChangedListener(this);
    }

    public RangeTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTimeChangedListener(this);
    }

    public RangeTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTimeChangedListener(this);
    }

    public void setMaxTime(String date,int hourIn24, int minute) {
        Date currentTime, pickedTime;
        mMaxHour = hourIn24;
        mMaxMinute = minute;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = sdf.format(new Date());
        try {
            currentTime = sdf.parse(currentTimeString);
            pickedTime = sdf.parse(date + " " + hourIn24+":"+minute+":00");


            Log.e("Ketan", "Current Date: " + currentTime);
            Log.e("Ketan", "Picked Date: " + pickedTime);
            if (currentTime.after(pickedTime)){
                this.setCurrentHour(mCurrentHour = hourIn24);
                this.setCurrentMinute(mCurrentMinute = minute);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void setMinTime(String date,int hourIn24,int minute) {
        Date currentTime, pickedTime;
        mMinHour = hourIn24;
        mMinMinute = minute;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        String currentTimeString = sdf.format(new Date());

        try {
            currentTime = sdf.parse(currentTimeString);
            pickedTime = sdf.parse(date + " " + hourIn24+":"+minute+":00");


            Log.e("Ketan", "Current Date: " + currentTime);
            Log.e("Ketan", "Picked Date: " + pickedTime);
            if (currentTime.before(pickedTime)){
                this.setCurrentHour(mCurrentHour = hourIn24);
                this.setCurrentMinute(mCurrentMinute = minute);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static boolean isTimeGreater(String date, String time) {
        Date currentTime, pickedTime;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String currentTimeString = sdf.format(new Date());

        try {
            currentTime = sdf.parse(currentTimeString);
            pickedTime = sdf.parse(date + " " + time);


            Log.e("Ketan", "Current Date: " + currentTime);
            Log.e("Ketan", "Picked Date: " + pickedTime);


        /*compare > 0, if date1 is greater than date2
        compare = 0, if date1 is equal to date2
        compare < 0, if date1 is smaller than date2*/

            if (pickedTime.compareTo(currentTime) > 0) {
                Log.e("Ketan", "Picked Date is Greater than Current Date");
                return true;
            } else {
                Log.e("Ketan", "Picked Date is Less than Current Date");
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        boolean validTime = true;
        if (hourOfDay < mMinHour || (hourOfDay == mMinHour
                && minute < mMinMinute)) {
            validTime = false;
        }

        if (hourOfDay > mMaxHour || (hourOfDay == mMaxHour
                && minute > mMaxMinute)) {
            validTime = false;
        }

        if (validTime) {
            mCurrentHour = hourOfDay;
            mCurrentMinute = minute;
        }

        setCurrentHour(mCurrentHour);
        setCurrentMinute(mCurrentMinute);

    }
}