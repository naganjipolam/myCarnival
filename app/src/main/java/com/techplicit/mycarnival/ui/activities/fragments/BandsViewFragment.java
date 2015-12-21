package com.techplicit.mycarnival.ui.activities.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techplicit.mycarnival.GpsSettingsDialogListener;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandsPojo;
import com.techplicit.mycarnival.data.model.SortedDistanceBandsPojo;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandsViewFragment extends Fragment implements Constants, LocationListener {

    private static final String TAG = BandsViewFragment.class.getName();
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static GoogleMap mMap;
    private static HashMap<Marker, SortedDistanceBandsPojo> mMarkersHashMap;
    View v = null;

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    public static double latitude; // latitude
    public static double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private static ArrayList<BandsPojo> quizModelArrayList;
    private static SharedPreferences sharedPreferences;
    private boolean isLocationUpdated;
    private ProgressBar carnivalsProgress;
    private AlertDialog changePassDialog;
    private GpsSettingsDialogListener mCallbackListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {
            v = inflater.inflate(R.layout.bands_view_fragment, container, false);
            mMarkersHashMap = new HashMap<Marker, SortedDistanceBandsPojo>();
        }

        sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        isLocationUpdated = sharedPreferences.getBoolean(IS_VIEW_NEEDS_TO_LOAD, false);

        setUpMap();

        if (Utility.isNetworkConnectionAvailable(getActivity())) {

            if (CarnivalsSingleton.getInstance().getBandsPojoArrayList() != null) {

                if (CarnivalsSingleton.getInstance().getBandsPojoArrayList() != null) {
                    quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

//                    CarnivalsSingleton.getInstance().setDistanceSortedBandsPojoArrayList(null);
                    ArrayList<SortedDistanceBandsPojo> sortedDistanceBandsPojosList = new ArrayList<SortedDistanceBandsPojo>();

                    for (BandsPojo pojo : quizModelArrayList) {
                        double distance = Utility.calculateLatLangDistances(latitude, longitude, Double.parseDouble(pojo.getLatitude()), Double.parseDouble(pojo.getLongitude()));

                        SortedDistanceBandsPojo pojoSorted = new SortedDistanceBandsPojo(null);
                        pojoSorted.setDistance(distance);

                        pojoSorted.setAddress(pojo.getAddress());
                        pojoSorted.setImage(pojo.getImage());
                        pojoSorted.setActiveFlag(pojo.getActiveFlag());
                        pojoSorted.setLastUpdated(pojo.getLastUpdated());
                        pojoSorted.setLatitude(pojo.getLatitude());
                        pojoSorted.setLongitude(pojo.getLongitude());
                        pojoSorted.setName(pojo.getName());
                        pojoSorted.setUpdates(pojo.getUpdates());

                        sortedDistanceBandsPojosList.add(pojoSorted);

                    }


                    Collections.sort(sortedDistanceBandsPojosList, new Comparator<SortedDistanceBandsPojo>() {
                        public int compare(SortedDistanceBandsPojo dc1, SortedDistanceBandsPojo dc2) {
                            return (int) (dc1.getDistance() - (dc2.getDistance()));
                        }
                    });

                    CarnivalsSingleton.getInstance().setDistanceSortedBandsPojoArrayList(sortedDistanceBandsPojosList);


                    plotMarkers(null, null, null, getActivity());

                    Log.e("Siva", "sortedDistanceBandsPojosList size1 --> " + sortedDistanceBandsPojosList.size());

                }
            } else {
                new GetAsync(getActivity(), null).execute();

            }


        } else {
            Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL);
        }

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity());

    }

    private void setUpMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map_view)).getMap();

            // Check if we were successful in obtaining the map.

            if (mMap != null) {
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private static void plotMarkers(String lat, String lng, String name, Activity context) {
        Log.e("Siva", "lat--> " + lat);
        Log.e("Siva", "lng--> " + lng);
        Log.e("Siva", "name--> " + name);

        double latitude = 0, longitude = 0;

        if (CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList() != null && CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList().size() > 0) {

            for (int i = 0; i < CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList().size(); i++) {
                SortedDistanceBandsPojo pojo = CarnivalsSingleton.getInstance().getSortedDistanceBandsPojoArrayList().get(i);

                Log.e("Siva", "lat--> " + pojo.getLatitude() + " long--> " + pojo.getLongitude());

                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(Double.parseDouble(pojo.getLatitude()), Double.parseDouble(pojo.getLongitude())));
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));

                Marker currentMarker = mMap.addMarker(markerOption);

                if (i == 0) {
                    latitude = Double.parseDouble(pojo.getLatitude());
                    longitude = Double.parseDouble(pojo.getLongitude());

                }

                mMarkersHashMap.put(currentMarker, pojo);

                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(name, context));

            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 2));

        }
    }

    public static class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private String mName;
        private Activity mContext;

        public MarkerInfoWindowAdapter(String name, Activity context) {
            mName = name;
            mContext = context;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v = mContext.getLayoutInflater().inflate(R.layout.custom_marker_icon, null);

            ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

            TextView markerLabel = (TextView) v.findViewById(R.id.marker_text);

            SortedDistanceBandsPojo bandsPojo = mMarkersHashMap.get(marker);

//                markerIcon.setImageResource(R.drawable.);

            markerLabel.setText(bandsPojo.getName());

            return v;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
//            getActivity().getFragmentManager().beginTransaction()
//                    .remove(getActivity().getFragmentManager().findFragmentById(R.id.map_view)).commit();
            mMap = null;
        }
    }

    static class GetAsync extends AsyncTask<String, String, JSONArray> {

        private boolean isResponseSucceed;
        ServiceHandler jsonParser = new ServiceHandler();

        private ProgressDialog pDialog;
        private ProgressBar carnivalsProgress;

        private static final String BANDS_URL = Constants.BASE_URL + "getbandslocationoverview?carnival=";

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";


        private Activity mContext;
        private String responseStatus;
        private String jsonData;

        public GetAsync(Activity context, ProgressBar carnivalsProgress) {
            mContext = context;
            this.carnivalsProgress = carnivalsProgress;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                String selectedCarnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, "");

                String selectedCarnivalNameTrimmed = null;

                if (selectedCarnivalName.contains(" & ")) {
                    selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" & ", "+%26+").trim();
                } else if (selectedCarnivalName.contains(" ")) {
                    selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" ", "%20").trim();
                }

                responseStatus = jsonParser.makeHttpRequest(
                        BANDS_URL + selectedCarnivalNameTrimmed, "GET", null);


                if (responseStatus != null && !responseStatus.equalsIgnoreCase(ERROR)) {
                    jsonData = jsonParser.makeHttpRequest(
                            BANDS_URL + selectedCarnivalNameTrimmed, "GET", null);


                    JSONArray jsonArray = null;

                    jsonArray = new JSONArray(jsonData);

                    if (jsonArray != null) {
                        Log.d("JSON result", jsonArray.toString());

                        return jsonArray;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

            return null;
        }

        protected void onPostExecute(JSONArray jsonArray) {

            if (responseStatus.equalsIgnoreCase(ERROR)) {
                Utility.displayNetworkFailDialog(mContext, ERROR);
            }

            if (jsonArray != null) {

                CarnivalsSingleton.getInstance().setBandsJsonResponse(jsonArray);

                if (CarnivalsSingleton.getInstance().getBandsPojoArrayList() != null) {
                    quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

//                    CarnivalsSingleton.getInstance().setDistanceSortedBandsPojoArrayList(null);
                    ArrayList<SortedDistanceBandsPojo> sortedDistanceBandsPojosList = new ArrayList<SortedDistanceBandsPojo>();

                    for (BandsPojo pojo : quizModelArrayList) {
                        double distance = Utility.calculateLatLangDistances(latitude, longitude, Double.parseDouble(pojo.getLatitude()), Double.parseDouble(pojo.getLongitude()));

                        SortedDistanceBandsPojo pojoSorted = new SortedDistanceBandsPojo(null);
                        pojoSorted.setDistance(distance);

                        pojoSorted.setAddress(pojo.getAddress());
                        pojoSorted.setImage(pojo.getImage());
                        pojoSorted.setActiveFlag(pojo.getActiveFlag());
                        pojoSorted.setLastUpdated(pojo.getLastUpdated());
                        pojoSorted.setLatitude(pojo.getLatitude());
                        pojoSorted.setLongitude(pojo.getLongitude());
                        pojoSorted.setName(pojo.getName());
                        pojoSorted.setUpdates(pojo.getUpdates());

                        sortedDistanceBandsPojosList.add(pojoSorted);

                    }


                    Collections.sort(sortedDistanceBandsPojosList, new Comparator<SortedDistanceBandsPojo>() {
                        public int compare(SortedDistanceBandsPojo dc1, SortedDistanceBandsPojo dc2) {
                            return (int) (dc1.getDistance() - (dc2.getDistance()));
                        }
                    });

                    CarnivalsSingleton.getInstance().setDistanceSortedBandsPojoArrayList(sortedDistanceBandsPojosList);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(IS_VIEW_NEEDS_TO_LOAD, false);
                    editor.commit();


                    plotMarkers(null, null, null, mContext);

                }
            }

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            Log.e(TAG, "latitude-->" + latitude + " longitude--> " + longitude);
        }
        Log.e(TAG, "onLocationChanged called-->");

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocation(Activity activityContext) {
        try {
            Log.e("Siva", "getLocation called");
            locationManager = (LocationManager) activityContext
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
//                if(!sharedPreferences.getBoolean(IS_GPS_DIALOG_SHOWING, false)){

//                showSettingsAlert();

//                mCallbackListener.showSettingsDialog(true);

//                }

                Log.e("Siva", "no network");
            } else {
                Log.e("Siva", "network");
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.e("Siva", "isNetworkEnabled");
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_LOCATION);

                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    Log.e("Siva", "isGPSEnabled");
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Siva", "getlocation Error-- > " + e.toString());
        }

        return location;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(BandsViewFragment.this);
        }
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivity(intent);
                dialog.cancel();

//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean(IS_GPS_DIALOG_SHOWING, false);
//                editor.commit();

            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean(IS_GPS_DIALOG_SHOWING, false);
//                editor.commit();

            }
        });
        // Showing Alert Message
//        alertDialog.show();


        changePassDialog = alertDialog.create();

        try{
            changePassDialog.cancel();
        }catch (Exception e){
            Log.e(TAG, "Problem with GPS Settings Alert --> "+e.toString());
        }

        changePassDialog.show();


//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean(IS_GPS_DIALOG_SHOWING, true);
//        editor.commit();

    }


    @Override
    public void onResume() {
        super.onResume();

        if (changePassDialog!=null && changePassDialog.isShowing()){
            changePassDialog.dismiss();
        }

        if (!isGPSEnabled && !isNetworkEnabled) {
            getLocation(getActivity());
        }

        isLocationUpdated = sharedPreferences.getBoolean(IS_VIEW_NEEDS_TO_LOAD, false);

        if (isLocationUpdated) {
            new GetAsync(getActivity(), carnivalsProgress).execute();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                Log.e(TAG, "onRequestPermissionsResult");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isGPSEnabled && !isNetworkEnabled) {
//                        getLocation(getActivity());
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        getLocation(getActivity());
        try{
            mCallbackListener = (BandTabsActivity)context;
        }catch (Exception e){
            Log.e(TAG, BandTabsActivity.class.getName()+" has to implement "+GpsSettingsDialogListener.class.getName());
        }
    }

}
