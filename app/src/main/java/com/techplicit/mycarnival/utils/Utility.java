package com.techplicit.mycarnival.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.techplicit.mycarnival.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class Utility implements Constants {

    private static AlertDialog alertDialog;

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
}
