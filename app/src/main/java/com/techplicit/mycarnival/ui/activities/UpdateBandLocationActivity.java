package com.techplicit.mycarnival.ui.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
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
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.NavigationDrawerFragment;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandsPojo;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class UpdateBandLocationActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, Constants, android.location.LocationListener {

    private static final int MIN_VALUE = 1;
    private static final int MY_PERMISSIONS_LOCATION = 101;
    private static final String TAG = UpdateBandLocationActivity.class.getName();
    private static NumberPicker bandsPicker;
    private static String mDurationStr;
    private static int mDurationValue = 1;
    private static String selectedPickerValue;
    private static TextView selectBandText;
    private static TextView updateLocation;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private static String bandNameSelected, bandAddress, bandLatitude, bandLongitude, carnivalName, from;
    private TextView title;

    private static GoogleMap mMap;


    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    static boolean canGetLocation = false, isGPSDialogShowing = false;
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
    private AlertDialog.Builder alertDialog;
    private AlertDialog changePassDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carnivals_list);

//        getLocation(this);

        sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_GPS_DIALOG_SHOWING, false);
        editor.commit();

        Intent i = getIntent();

        if (i != null) {
            bandNameSelected = i.getStringExtra(BAND_NAME);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("");

        ImageView home_icon = (ImageView) findViewById(R.id.home_icon);
        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(UpdateBandLocationActivity.this);
                finish();
            }
        });

        title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.update_location_title));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        }; // Drawer Toggle Object Made
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
        bandNameSelected = sharedPreferences.getString(SELECTED_BAND_NAME, null);
        bandAddress = sharedPreferences.getString(SELECTED_BAND_ADDRESS, null);
        bandLatitude = sharedPreferences.getString(SELECTED_BAND_LATITUDE, null);
        bandLongitude = sharedPreferences.getString(SELECTED_BAND_LONGITUDE, null);
        carnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, null);
        from = sharedPreferences.getString(UPDATE_LOCATION_FROM, null);

        Log.e("Siva", "bandNameSelected--> " + bandNameSelected);

//        plotMarkers(bandLatitude, bandLongitude, bandNameSelected, UpdateBandLocationActivity.this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(0))
                .commit();


    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
*/
        switch (position) {
            case 0:
                closeDrawer();
                break;
            case 1:
                closeDrawer();
                break;
            case 2:
                closeDrawer();
                break;
            case 3:
                closeDrawer();
                break;

        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                closeDrawer();
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                closeDrawer();
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                closeDrawer();
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                closeDrawer();
                break;
        }
    }

    private void closeDrawer() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.carnivals_list, menu);
//            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.right_menu_icon) {
            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static ListView carnivalsList;
        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        private Button btnFetes, btnBands, btnBandLocation, btnBandUpdate, btnSmartUpdate;
        private ImageView updateLocationBtn;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 final Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.update_band_location_fragment, container, false);

            if (!Utility.isNetworkConnectionAvailable(getActivity())) {
                Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL);
            }

            RelativeLayout layout_select_band = (RelativeLayout) rootView.findViewById(R.id.layout_select_band);

            layout_select_band.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayDurationDialog(getActivity());
                }
            });

            btnFetes = (Button) rootView.findViewById(R.id.fetes_button_hs);
            btnBands = (Button) rootView.findViewById(R.id.band_button_hs);
            btnBandLocation = (Button) rootView.findViewById(R.id.band_location_button_hs);
            btnBandUpdate = (Button) rootView.findViewById(R.id.band_update_button_hs);
            btnSmartUpdate = (Button) rootView.findViewById(R.id.smart_update_button_hs);
            updateLocationBtn = (ImageView) rootView.findViewById(R.id.update_location_btn);

            updateLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                    String from = sharedPreferences.getString(UPDATE_LOCATION_FROM, null);
                    carnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, null);

                    if (!selectBandText.getText().toString().trim().equalsIgnoreCase(getActivity().getResources().getString(R.string.select_band))) {
                        getBandsDetails(bandNameSelected, getActivity());
                        if (!Utility.isNetworkConnectionAvailable(getActivity())) {
                            Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL);
                        } else {
                            if (latitude!=0.0 && longitude!=0.0){
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "Failed to convert Address from Latitude and Longitude");
                                }

                                StringBuilder builder = new StringBuilder();

                                if (addresses.get(0).getAddressLine(0)!=null){
                                    builder.append(addresses.get(0).getAddressLine(0)+", ");
                                }

                                if (addresses.get(0).getLocality()!=null){
                                    builder.append(addresses.get(0).getLocality()+", ");
                                }

                                if (addresses.get(0).getAdminArea()!= null){
                                    builder.append(addresses.get(0).getAdminArea()+", ");
                                }

                                if (addresses.get(0).getCountryName() !=null){
                                    builder.append(addresses.get(0).getCountryName()+", ");
                                }

                                if (addresses.get(0).getPostalCode() !=null){
                                    builder.append(addresses.get(0).getPostalCode());
                                }

                                /*if (addresses.get(0).getFeatureName() !=null){
                                    builder.append(addresses.get(0).getFeatureName());
                                }*/

                                if (addresses.get(0).getLongitude() !=0){
                                    bandLongitude = ""+addresses.get(0).getLongitude();
                                }

                                if (addresses.get(0).getLatitude() !=0){
                                    bandLatitude = ""+addresses.get(0).getLatitude();
                                }

                                bandAddress = builder.toString();

                                new PlaceholderFragment.GetAsync(getActivity()).execute();

                            }else{
                                Toast.makeText(getActivity(), "Problem in fetching current Location!", Toast.LENGTH_LONG).show();
                            }

                        }
                    } else {
                        Toast.makeText(getActivity(), "Please select Band!", Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnFetes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            btnBands.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            btnBandLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            btnBandUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(UPDATE_LOCATION_FROM, FROM_BAND_UPDATE_BUTTON);
                    editor.commit();

//                    selectBandText.setText(getActivity().getResources().getString(R.string.select_band));
                }
            });

            btnSmartUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                IntentGenerator.startUpdateBandLocation(getApplicationContext(), -1, null);
                }
            });

            ImageView backArrowCarnivalsList = (ImageView) rootView.findViewById(R.id.back_arrow_carnivals_list);
            backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            MapsInitializer.initialize(getActivity());

            setUpMap();

            Log.e("Siva", "fragment bandNameSelected--> " + bandNameSelected);

            if (bandNameSelected != null) {
                getBandsDetails(bandNameSelected, getActivity());
            }

            selectBandText = (TextView) rootView.findViewById(R.id.text_select_band);
            updateLocation = (TextView) rootView.findViewById(R.id.update_location);

            if (from != null && from.equalsIgnoreCase(FROM_BAND_UPDATE_BUTTON)) {
                selectBandText.setText(getActivity().getResources().getString(R.string.select_band));
            } else if (from != null && from.equalsIgnoreCase(FROM_BANDS_LIST)) {
                selectBandText.setText(bandNameSelected);
            }

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
//            ((UpdateBandLocationActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        private void setUpMap() {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();

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


        public static class GetAsync extends AsyncTask<String, String, String> {

            ServiceHandler jsonParser = new ServiceHandler();

            private static final String TAG_SUCCESS = "success";
            private static final String TAG_MESSAGE = "message";

            private Activity mContext;
            private ProgressDialog progressDialog;
            private String responseStatus;
            private String response;

            public GetAsync(Activity context) {
                mContext = context;
            }

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Please Wait! Updating Band location. ");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... args) {

                try {

                    String updateLocationUrl = Constants.BASE_URL + "updatebandlocation?carnival=" + carnivalName +
                            "&band=" + bandNameSelected + "&address=" + bandAddress.replace(" ", "%20") + "&latitude=" + bandLatitude + "&longitude=" + bandLongitude;

                    if (updateLocationUrl.contains(" & ") || updateLocationUrl.contains(" ")) {
                        updateLocationUrl = updateLocationUrl.replace(" & ", "+%26+").replace(" ", "%20").trim();
                    }

                    Log.e("Siva", "updateLocationUrl--> " + updateLocationUrl);

                    response = jsonParser.makeHttpRequest(
                            updateLocationUrl, "GET", null);

                    if (response != null && !response.equalsIgnoreCase(ERROR)) {
                        JSONObject jsonObject = new JSONObject(response);
                        responseStatus = (String) jsonObject.get(STATUS);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    responseStatus = ERROR;
                }

                return responseStatus;
            }

            protected void onPostExecute(String response) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (response != null && !response.equalsIgnoreCase(ERROR)) {

                    if (response != null && response.equalsIgnoreCase("Success")) {
                        displayUpdateLocationStatus(mContext, "Success");
                        editor.putBoolean(IS_LOCATION_UPDATED, true);
                        editor.putBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, true);
                        editor.putBoolean(IS_DISATNCE_NEEDS_TO_LOAD, true);
                        editor.putBoolean(IS_FAVS_NEEDS_TO_LOAD, true);
                        editor.putBoolean(IS_VIEW_NEEDS_TO_LOAD, true);
                    } else {
                        displayUpdateLocationStatus(mContext, "Fail");
                        editor.putBoolean(IS_LOCATION_UPDATED, false);
                        editor.putBoolean(IS_ALPH_SORT_NEEDS_TO_LOAD, false);
                        editor.putBoolean(IS_DISATNCE_NEEDS_TO_LOAD, false);
                        editor.putBoolean(IS_FAVS_NEEDS_TO_LOAD, false);
                        editor.putBoolean(IS_VIEW_NEEDS_TO_LOAD, false);
                    }

                    editor.putString(SELECTED_BAND_NAME, null);
                    editor.putString(SELECTED_BAND_ADDRESS, null);
                    editor.putString(SELECTED_BAND_LATITUDE, null);
                    editor.putString(SELECTED_BAND_LONGITUDE, null);
                    editor.putString(UPDATE_LOCATION_FROM, null);
                    editor.commit();

                } else {
                    Utility.displayNetworkFailDialog(mContext, ERROR);
                }

            }

        }


    }

    private static void displayUpdateLocationDialog(final Activity context, String fromBandUpdateButton) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_location);
//        dialog.setCancelable(false);

        selectBandText = (TextView) dialog.findViewById(R.id.text_select_band);
        updateLocation = (TextView) dialog.findViewById(R.id.update_location);
        ImageView selectBandImage = (ImageView) dialog.findViewById(R.id.image_select_band);

        RelativeLayout layout_select_band = (RelativeLayout) dialog.findViewById(R.id.layout_select_band);
        if (fromBandUpdateButton != null && fromBandUpdateButton.equalsIgnoreCase(FROM_BAND_UPDATE_BUTTON)) {
            selectBandText.setText(context.getResources().getString(R.string.select_band));
        } else if (bandNameSelected != null) {
            selectBandText.setText(bandNameSelected);
        }


        layout_select_band.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDurationDialog(context);
            }
        });

        dialog.show();

    }

    private static void displayUpdateLocationStatus(final Activity context, String status) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_location_status);
//        dialog.setCancelable(false);

        TextView status_title = (TextView) dialog.findViewById(R.id.status_title);
        TextView status_message = (TextView) dialog.findViewById(R.id.status_message);
        TextView ok_text = (TextView) dialog.findViewById(R.id.ok_text);

        if (status != null && status.equalsIgnoreCase("Success")) {
            status_title.setText("" + context.getResources().getString(R.string.success_title));
            status_message.setText("" + context.getResources().getString(R.string.status_message_update_success));
        } else {
            status_title.setText("" + context.getResources().getString(R.string.success_title));
            status_message.setText("" + context.getResources().getString(R.string.status_message_update_fail));
        }

        ok_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private static void getBandsDetails(String bandName, Activity context) {
        final ArrayList<BandsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

        for (int i = 0; i < carnivalsPojoArrayList.size(); i++) {

            BandsPojo carnivalsPojo = (BandsPojo) carnivalsPojoArrayList.get(i);
            if (carnivalsPojo.getName().contains(bandName)) {
                Log.e("Siva", "getBandsDetails ifff");
                bandNameSelected = carnivalsPojo.getName();
                bandAddress = carnivalsPojo.getAddress();
                bandLatitude = carnivalsPojo.getLatitude();
                bandLongitude = carnivalsPojo.getLongitude();

                Log.e("Siva", "bandNameSelected-->" + bandNameSelected);
                Log.e("Siva", "bandAddress-->" + bandAddress);
                Log.e("Siva", "bandLatitude -->" + bandLatitude);
                Log.e("Siva", "bandLongitude -->" + bandLongitude);

//                    plotMarkers(bandLatitude, bandLongitude, bandName, context);

                return;
            }

        }


    }

    private static void displayDurationDialog(final Activity context) {

        final Dialog mDurationDialog = new Dialog(context);
        mDurationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDurationDialog.setContentView(R.layout.dialog_band_picker);
//        mDurationDialog.setCancelable(false);

        bandsPicker = (NumberPicker) mDurationDialog.findViewById(R.id.band_picker);
        TextView selectPicker = (TextView) mDurationDialog.findViewById(R.id.select_band_picker);
        TextView cancelPicker = (TextView) mDurationDialog.findViewById(R.id.cancel_band_picker);

        bandsPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final ArrayList<BandsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();
        final ArrayList<String> listBands = new ArrayList<String>();
        for (int i = 0; i < carnivalsPojoArrayList.size(); i++) {
            BandsPojo carnivalsPojo = (BandsPojo) carnivalsPojoArrayList.get(i);
            listBands.add(carnivalsPojo.getName());
        }

        selectPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDurationDialog.dismiss();

                bandNameSelected = "" + listBands.get(mDurationValue - 1);
                if (bandNameSelected != null) {
                    selectBandText.setText(bandNameSelected);
                    getBandsDetails(bandNameSelected, context);
                }

                mDurationValue = 1;
            }
        });

        cancelPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDurationDialog.dismiss();

            }
        });

        String[] textDurationValues = new String[listBands.size()];
        textDurationValues = listBands.toArray(textDurationValues);

        bandsPicker.setMinValue(MIN_VALUE);
        bandsPicker.setMaxValue(listBands.size());
        bandsPicker.setWrapSelectorWheel(false);
        bandsPicker.setDisplayedValues(textDurationValues);
        bandsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                bandsPicker.getContentDescription();
                mDurationValue = newVal;
                bandNameSelected = listBands.get(newVal - 1);
                if (bandNameSelected != null) {
                    selectBandText.setText(bandNameSelected);
                }

//                mDurationDialog.setTitle("[" + String.valueOf(mNumberPickerValues[mNumbersValue - 1]) + " " + mConditionsList.get(mDurationValue - 1) + "]");

            }
        });

//        bandsPicker.setValue(mDurationValue);

        mDurationDialog.show();
    }


    private static void plotMarkers(String lat, String lng, String name, Activity context) {
        Log.e("Siva", "lat--> " + lat);
        Log.e("Siva", "lng--> " + lng);
        Log.e("Siva", "name--> " + name);

        // Create user marker with custom icon and other options
        MarkerOptions markerOption = new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.app_icon));

        Marker currentMarker = mMap.addMarker(markerOption);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 15));
        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(name, context));
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

//                markerIcon.setImageResource(manageMarkerIcon(myMarker.getmIcon()));

            markerLabel.setText(mName);

            return v;
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
                showSettingsAlert();
                Log.e("Siva", "no network");
            } else {
                Log.e("Siva", "network");
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.e("Siva", "isNetworkEnabled");
                    if (ActivityCompat.checkSelfPermission(UpdateBandLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UpdateBandLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        ActivityCompat.requestPermissions(UpdateBandLocationActivity.this,
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
            if (ActivityCompat.checkSelfPermission(UpdateBandLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UpdateBandLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(UpdateBandLocationActivity.this);
        }
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        alertDialog = new AlertDialog.Builder(UpdateBandLocationActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                UpdateBandLocationActivity.this.startActivity(intent);
                dialog.cancel();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message

        changePassDialog = alertDialog.create();

        try{
            changePassDialog.cancel();
        }catch (Exception e){
            Log.e(TAG, "Problem with GPS Settings Alert --> "+e.toString());
        }

        changePassDialog.show();


        /*if (!isGPSDialogShowing){
            alertDialog.show();
            isGPSDialogShowing = true;
        }*/


    }



    /*@Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                isGPSDialogShowing = false;
                Log.d(TAG, "Action was DOWN");
                break;
        }

        return super.onTouchEvent(event);
    }*/

    @Override
    public void onResume() {
        super.onResume();

        if (changePassDialog!=null && changePassDialog.isShowing()){
            changePassDialog.dismiss();
        }

        if (!isGPSEnabled && !isNetworkEnabled) {
            getLocation(UpdateBandLocationActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isGPSDialogShowing = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isGPSDialogShowing = true;
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
//                    if (!isGPSEnabled && !isNetworkEnabled) {
//                        getLocation(UpdateBandLocationActivity.this);
//                    }

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




}
