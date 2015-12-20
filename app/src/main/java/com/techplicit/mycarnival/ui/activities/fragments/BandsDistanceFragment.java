package com.techplicit.mycarnival.ui.activities.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.BandsGridAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandsPojo;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandsDistanceFragment extends Fragment implements Constants, android.location.LocationListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static ListView carnivalsList;
    private ProgressDialog pDialog;
    private AlertDialog alertDialog;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BandsDistanceFragment newInstance(int sectionNumber) {
        BandsDistanceFragment fragment = new BandsDistanceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public BandsDistanceFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bands_list, container, false);
        carnivalsList = (ListView)rootView.findViewById(R.id.list_carnivals);
        ProgressBar carnivalsProgress = (ProgressBar)rootView.findViewById(R.id.progress_carnivals_list);
        carnivalsProgress.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);


        carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<BandsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();
                BandsPojo carnivalsPojo = (BandsPojo)carnivalsPojoArrayList.get(position);

                String bandName = carnivalsPojo.getName();
                String address = carnivalsPojo.getAddress();
                String latitude = carnivalsPojo.getLatitude();
                String longitude = carnivalsPojo.getLongitude();

                if (bandName!=null && address!=null && latitude!=null && longitude!=null){
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SELECTED_BAND_NAME, bandName);
                    editor.putString(SELECTED_BAND_ADDRESS, address);
                    editor.putString(SELECTED_BAND_LATITUDE, latitude);
                    editor.putString(SELECTED_BAND_LONGITUDE, longitude);
                    editor.putString(UPDATE_LOCATION_FROM, FROM_BANDS_LIST);
                    editor.commit();
                }

                if (carnivalsPojo.getActiveFlag()){
                    IntentGenerator.startUpdateBandLocation(getActivity().getApplicationContext(), position, carnivalsPojo.getName());
                }else{

                }
            }
        });

        if (Utility.isNetworkConnectionAvailable(getActivity())){
//                carnivalsProgress.setVisibility(View.GONE);
            new GetAsync(getActivity(), carnivalsProgress).execute();
        }else{
            carnivalsProgress.setVisibility(View.VISIBLE);
            Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL);

        }


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//            ((CarnivalsListActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
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


    static class GetAsync extends AsyncTask<String, String, JSONArray> {

        private boolean isResponseSucceed;
        ServiceHandler jsonParser = new ServiceHandler();

        private ProgressDialog pDialog;
        private ProgressBar carnivalsProgress;

        private static final String BANDS_URL = Constants.BASE_URL+"getbandslocationoverview?carnival=";

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private ArrayList<BandsPojo> quizModelArrayList;

        private Activity mContext;
        private String responseStatus;
        private String jsonData;

        public GetAsync(Activity context, ProgressBar carnivalsProgress) {
            mContext = context;
            this.carnivalsProgress = carnivalsProgress;
        }

        @Override
        protected void onPreExecute() {
            this.carnivalsProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONArray doInBackground(String... args) {

            try {

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                String selectedCarnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, "");

                String selectedCarnivalNameTrimmed = null;

                if (selectedCarnivalName.contains(" & ")){
                    selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" & ", "+%26+").trim();
                }else if (selectedCarnivalName.contains(" ")){
                    selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" ", "%20").trim();
                }

                responseStatus = jsonParser.makeHttpRequest(
                        BANDS_URL+selectedCarnivalNameTrimmed, "GET", null);


                if (responseStatus!=null && !responseStatus.equalsIgnoreCase(ERROR)){
                    jsonData = jsonParser.makeHttpRequest(
                            BANDS_URL+selectedCarnivalNameTrimmed, "GET", null);


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

            if (responseStatus.equalsIgnoreCase(ERROR)){
                Utility.displayNetworkFailDialog(mContext, ERROR);
            }

            if (jsonArray != null) {

                CarnivalsSingleton.getInstance().setBandsJsonResponse(jsonArray);

                if (CarnivalsSingleton.getInstance().getBandsJsonResponse()!=null){
                    quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

                    carnivalsList.setAdapter(new BandsGridAdapter(mContext,quizModelArrayList));
                    this.carnivalsProgress.setVisibility(View.GONE);
                }
            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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



}
