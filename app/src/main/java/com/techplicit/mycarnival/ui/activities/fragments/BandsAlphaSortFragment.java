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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.BandsGridAdapter;
import com.techplicit.mycarnival.adapters.BandsSortedGridAdapter;
import com.techplicit.mycarnival.adapters.CarnivalsListAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandsPojo;
import com.techplicit.mycarnival.data.model.CarnivalsPojo;
import com.techplicit.mycarnival.data.model.SortedDistanceBandsPojo;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.ui.activities.UpdateBandLocationActivity;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandsAlphaSortFragment extends Fragment implements Constants{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static final String TAG = BandsAlphaSortFragment.class.getName();
    private static ListView carnivalsList;
    private static ArrayList<BandsPojo> quizModelArrayList;
    private static SharedPreferences sharedPreferences;
    private boolean isLocationUpdated;
    private ProgressBar carnivalsProgress;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BandsAlphaSortFragment newInstance(int sectionNumber) {
        BandsAlphaSortFragment fragment = new BandsAlphaSortFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public BandsAlphaSortFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bands_list, container, false);
        carnivalsList = (ListView) rootView.findViewById(R.id.list_carnivals);
        carnivalsProgress = (ProgressBar) rootView.findViewById(R.id.progress_carnivals_list);
        carnivalsProgress.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

        sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        isLocationUpdated = sharedPreferences.getBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);

        carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<BandsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();
                BandsPojo carnivalsPojo = (BandsPojo) carnivalsPojoArrayList.get(position);

                String bandName = carnivalsPojo.getName();
                String address = carnivalsPojo.getAddress();
                String latitude = carnivalsPojo.getLatitude();
                String longitude = carnivalsPojo.getLongitude();

                if (bandName != null && address != null && latitude != null && longitude != null) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SELECTED_BAND_NAME, bandName);
                    editor.putString(SELECTED_BAND_ADDRESS, address);
                    editor.putString(SELECTED_BAND_LATITUDE, latitude);
                    editor.putString(SELECTED_BAND_LONGITUDE, longitude);

                    editor.putString(UPDATE_LOCATION_FROM, FROM_BANDS_LIST);

                    editor.commit();
                }

                if (carnivalsPojo.getActiveFlag()) {
                    IntentGenerator.startUpdateBandLocation(getActivity().getApplicationContext(), position, carnivalsPojo.getName());
                }
            }
        });

        if (Utility.isNetworkConnectionAvailable(getActivity())) {

            if (CarnivalsSingleton.getInstance().getBandsPojoArrayList()!=null){
                carnivalsProgress.setVisibility(View.GONE);
                if (CarnivalsSingleton.getInstance().getBandsPojoArrayList()!=null){
                    quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

                    carnivalsList.setAdapter(new BandsGridAdapter(getActivity(),quizModelArrayList));
                    carnivalsProgress.setVisibility(View.GONE);
                }
            }else{
                new GetAsync(getActivity(), carnivalsProgress).execute();
            }

        } else {
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
        private String selectedCarnivalNameTrimmed;

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


                if (selectedCarnivalName.contains(" & ") || selectedCarnivalName.contains(" ")) {
                    selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" & ", "+%26+").replace(" ", "%20").trim();
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

            int success = 0;
            String message = "";

            JSONObject jsonObject = null;

            if (responseStatus.equalsIgnoreCase(ERROR)) {
                Utility.displayNetworkFailDialog(mContext, ERROR);
            }

            if (jsonArray != null) {

                CarnivalsSingleton.getInstance().setBandsJsonResponse(jsonArray);

                if (CarnivalsSingleton.getInstance().getBandsJsonResponse() != null) {
                    quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

                    carnivalsList.setAdapter(new BandsGridAdapter(mContext, quizModelArrayList));
                    this.carnivalsProgress.setVisibility(View.GONE);
                }
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);
            editor.commit();

            if (success == 1) {
                Log.d("Success!", message);
            } else {
                Log.d("Failure", message);
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        isLocationUpdated = sharedPreferences.getBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);

        if (isLocationUpdated){
            new GetAsync(getActivity(), carnivalsProgress).execute();
        }

    }

}
