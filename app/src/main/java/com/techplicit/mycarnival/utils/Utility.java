package com.techplicit.mycarnival.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.ui.activities.UpdateBandLocationActivity;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class Utility implements Constants {

    private static final String TAG = Utility.class.getName();
    private static AlertDialog alertDialog;
    private static AlertDialog.Builder alertDialogSettings;
    public static AlertDialog changePassDialog;

    // added as an instance method to an Activity
    public static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static void displayNetworkFailDialog(final Activity context, String type) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        if (type.equalsIgnoreCase(NETWORK_FAIL)) {
            alertDialogBuilder.setMessage(context.getResources().getString(R.string.network_fail_message));
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.network_status));
        } else if (type.equalsIgnoreCase(ERROR)) {
            alertDialogBuilder.setMessage(context.getResources().getString(R.string.error_message));
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.error_status));
        }

        final AlertDialog.Builder ok = alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    context.finish();
                }
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static double calculateLatLangDistances(double lat1, double long1, double lat2, double long2) {
        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(long1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(long2);

        return locationA.distanceTo(locationB);
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public static AlertDialog showSettingsAlert(final Activity activity) {
        alertDialogSettings = new AlertDialog.Builder(activity);
        // Setting Dialog Title
        alertDialogSettings.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialogSettings.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialogSettings.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
                dialog.cancel();
            }
        });

        // on pressing cancel button
        alertDialogSettings.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message

        changePassDialog = alertDialogSettings.create();

        try{
            changePassDialog.dismiss();
        }catch (Exception e){
            Log.e(TAG, "Problem with GPS Settings Alert --> " + e.toString());
        }

        changePassDialog.show();

        /*if (!isGPSDialogShowing){
            alertDialog.show();
            isGPSDialogShowing = true;
        }*/


        return changePassDialog;
    }

}
