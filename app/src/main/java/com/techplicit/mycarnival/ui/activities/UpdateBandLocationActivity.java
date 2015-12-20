package com.techplicit.mycarnival.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
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
import com.techplicit.mycarnival.data.model.CarnivalsPojo;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;


public class UpdateBandLocationActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, Constants {

    private static final int MIN_VALUE = 1;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carnivals_list);

        Intent i = getIntent();

        if (i != null) {
            bandNameSelected = i.getStringExtra(BAND_NAME);
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("");

        ImageView home_icon = (ImageView)findViewById(R.id.home_icon);
        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(UpdateBandLocationActivity.this);
            }
        });

        title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.update_location_title));
        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));

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

        Log.e("Siva", "bandNameSelected--> "+bandNameSelected);

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
        if (drawerLayout!=null && drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
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

            if (from!=null && from.equalsIgnoreCase(FROM_BAND_UPDATE_BUTTON)){
                displayUpdateLocationDialog(getActivity(), FROM_BAND_UPDATE_BUTTON);
            }else if (from!=null && from.equalsIgnoreCase(FROM_BANDS_LIST)){
                displayUpdateLocationDialog(getActivity(), FROM_BANDS_LIST);
            }


            if (!Utility.isNetworkConnectionAvailable(getActivity())) {
                Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL);
            }

            btnFetes = (Button) rootView.findViewById(R.id.fetes_button_hs);
            btnBands = (Button) rootView.findViewById(R.id.band_button_hs);
            btnBandLocation = (Button) rootView.findViewById(R.id.band_location_button_hs);
            btnBandUpdate = (Button) rootView.findViewById(R.id.band_update_button_hs);
            btnSmartUpdate = (Button) rootView.findViewById(R.id.smart_update_button_hs);

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

                    displayUpdateLocationDialog(getActivity(), FROM_BAND_UPDATE_BUTTON);
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


            if (bandNameSelected!=null){
                getBandsDetails(bandNameSelected, getActivity());
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

            private boolean isResponseSucceed;
            ServiceHandler jsonParser = new ServiceHandler();

            private ProgressDialog pDialog;
            private ProgressBar carnivalsProgress;


            private static final String TAG_SUCCESS = "success";
            private static final String TAG_MESSAGE = "message";
            private ArrayList<CarnivalsPojo> quizModelArrayList;

            private Activity mContext;
            private ProgressDialog progressDialog;
            private String responseStatus;
            private String response;

            public GetAsync(Activity context, ProgressBar carnivalsProgress) {
                mContext = context;
                this.carnivalsProgress = carnivalsProgress;
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
                    } /*else if (updateLocationUrl.contains(" ")) {
                        updateLocationUrl = updateLocationUrl.replace(" ", "%20");
                    }*/

                    Log.e("Siva", "updateLocationUrl--> "+updateLocationUrl);

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

                if (response!=null && !response.equalsIgnoreCase(ERROR)) {
                    if (response != null && response.equalsIgnoreCase("Success")) {
                        displayUpdateLocationStatus(mContext, "Success");
                    } else {
                        displayUpdateLocationStatus(mContext, "Fail");
                    }

                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
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

        RelativeLayout layout_select_band = (RelativeLayout)dialog.findViewById(R.id.layout_select_band);
        if (fromBandUpdateButton!=null && fromBandUpdateButton.equalsIgnoreCase(FROM_BAND_UPDATE_BUTTON)){
            selectBandText.setText(context.getResources().getString(R.string.select_band));
        }else if (bandNameSelected != null) {
            selectBandText.setText(bandNameSelected);
        }


        layout_select_band.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDurationDialog(context);
            }
        });

        /*selectBandImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDurationDialog(context);
            }
        });*/

        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                String from = sharedPreferences.getString(UPDATE_LOCATION_FROM, null);
                carnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, null);

                if (!selectBandText.getText().toString().trim().equalsIgnoreCase(context.getResources().getString(R.string.select_band))){
                    getBandsDetails(bandNameSelected, context);
                    if (!Utility.isNetworkConnectionAvailable(context)) {
                        Utility.displayNetworkFailDialog(context, NETWORK_FAIL);
                    } else {
                        new PlaceholderFragment.GetAsync(context, null).execute();
                    }
                    dialog.dismiss();
                }else{
                    Toast.makeText(context, "Please select Band!", Toast.LENGTH_LONG).show();
                }

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
                if (carnivalsPojo.getName().contains(bandName)){
                    Log.e("Siva", "getBandsDetails ifff");
                    bandNameSelected = carnivalsPojo.getName();
                    bandAddress = carnivalsPojo.getAddress();
                    bandLatitude = carnivalsPojo.getLatitude();
                    bandLongitude = carnivalsPojo.getLongitude();

                    Log.e("Siva", "bandNameSelected-->"+bandNameSelected);
                    Log.e("Siva", "bandAddress-->"+bandAddress);
                    Log.e("Siva", "bandLatitude -->"+bandLatitude);
                    Log.e("Siva", "bandLongitude -->"+bandLongitude);

                    plotMarkers(bandLatitude, bandLongitude, bandName, context);

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


    private static void plotMarkers(String lat, String lng, String name, Activity context)
    {
        Log.e("Siva", "lat--> "+lat);
        Log.e("Siva", "lng--> "+lng);
        Log.e("Siva", "name--> "+name);

        // Create user marker with custom icon and other options
        MarkerOptions markerOption = new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.app_icon));

        Marker currentMarker = mMap.addMarker(markerOption);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 15));
        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(name, context));

    }

    public static class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        private String mName;
        private Activity mContext;
        public MarkerInfoWindowAdapter(String name, Activity context)
        {
            mName = name;
            mContext = context;
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View v  = mContext.getLayoutInflater().inflate(R.layout.custom_marker_icon, null);

            ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

            TextView markerLabel = (TextView)v.findViewById(R.id.marker_text);

//                markerIcon.setImageResource(manageMarkerIcon(myMarker.getmIcon()));

            markerLabel.setText(mName);

            return v;
        }
    }

}
