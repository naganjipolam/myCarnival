package com.techplicit.mycarnival.ui.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.techplicit.mycarnival.GpsSettingsDialogListener;
import com.techplicit.mycarnival.IntentGenerator;
import com.techplicit.mycarnival.NavigationDrawerFragment;
import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.BandsTabsPageAdapter;
import com.techplicit.mycarnival.adapters.ViewPagerAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.ui.activities.fragments.BandsAlphaSortFragment;
import com.techplicit.mycarnival.ui.activities.fragments.BandsDistanceFragment;
import com.techplicit.mycarnival.ui.activities.fragments.BandsMyFavourites;
import com.techplicit.mycarnival.ui.activities.fragments.BandsViewFragment;
import com.techplicit.mycarnival.utils.Constants;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandTabsActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, android.app.ActionBar.TabListener, Constants, GpsSettingsDialogListener,  android.location.LocationListener {


    private static final int MY_PERMISSIONS_LOCATION = 1;
    private static final String TAG = BandTabsActivity.class.getName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private ViewPager viewPager;
    private BandsTabsPageAdapter mAdapter;

    // Tab titles
    private String[] tabs = {"Top Rated", "Games", "Movies"};
    private int[] tabIcons = {
            R.drawable.distance_ic,
            R.drawable.alpha_sort,
            R.drawable.fav,
            R.drawable.view_ic
    };
    private TabLayout tabLayout;
    private Button btnFetes, btnBands, btnBandLocation, btnBandUpdate, btnSmartUpdate;
    private int bandPosition;
    private String bandName;

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private AlertDialog.Builder alertDialog;
    private AlertDialog changePassDialog;


    public BandTabsActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bands_list);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        btnFetes = (Button)findViewById(R.id.fetes_button_hs);
        btnBands = (Button)findViewById(R.id.band_button_hs);
        btnBandLocation = (Button)findViewById(R.id.band_location_button_hs);
        btnBandUpdate = (Button)findViewById(R.id.band_update_button_hs);
        btnSmartUpdate = (Button)findViewById(R.id.smart_update_button_hs);
        ImageView home_icon = (ImageView)findViewById(R.id.home_icon);
        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(BandTabsActivity.this);
                finish();
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.drawable.ic_drawer,R.string.navigation_drawer_open,R.string.navigation_drawer_close){

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

        ImageView backArrowCarnivalsList = (ImageView)findViewById(R.id.back_arrow_carnivals_list);
        backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

            }
        });

        btnBandUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(UPDATE_LOCATION_FROM, FROM_BAND_UPDATE_BUTTON);
                editor.commit();

                IntentGenerator.startUpdateBandLocation(getApplicationContext(), -1, null);
            }
        });

        btnSmartUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IntentGenerator.startUpdateBandLocation(getApplicationContext(), -1, null);
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BandsDistanceFragment(), "DISTANCE");
        adapter.addFragment(new BandsAlphaSortFragment(), "ALPHA SORT");
        adapter.addFragment(new BandsMyFavourites(), "MY FAVS");
        adapter.addFragment(new BandsViewFragment(), "VIEW");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, BandsAlphaSortFragment.newInstance(position + 1))
//                .commit();

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

    private void closeDrawer(){
        if (drawerLayout!=null && drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }
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

    @Override
    public void onTabSelected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(android.app.ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CarnivalsSingleton.getInstance().clearBandsData();
        Log.e(TAG, "onDestroy called");
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        alertDialog = new AlertDialog.Builder(BandTabsActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                BandTabsActivity.this.startActivity(intent);
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

    @Override
    public void showSettingsDialog(boolean status) {
        if (changePassDialog!=null && changePassDialog.isShowing()){
            changePassDialog.dismiss();
        }

        showSettingsAlert();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (changePassDialog!=null && changePassDialog.isShowing()){
            changePassDialog.dismiss();
        }


        if (!isGPSEnabled && !isNetworkEnabled) {
            getLocation(BandTabsActivity.this);
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
//                if(!Utility.changePassDialog.isShowing()){
                    showSettingsAlert();
//                }
//                mCallbackListener.showSettingsDialog(true);

//                }

                Log.e("Siva", "no network");
            } else {
                Log.e("Siva", "network");
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    Log.e("Siva", "isNetworkEnabled");
                    if (ActivityCompat.checkSelfPermission(BandTabsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BandTabsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        ActivityCompat.requestPermissions(BandTabsActivity.this,
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
            if (ActivityCompat.checkSelfPermission(BandTabsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BandTabsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(BandTabsActivity.this);
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
//                    if (!isGPSEnabled && !isNetworkEnabled) {
//                        getLocation(getActivity());
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
