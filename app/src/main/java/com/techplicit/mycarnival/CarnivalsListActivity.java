package com.techplicit.mycarnival;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
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
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.techplicit.mycarnival.adapters.CarnivalsListAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.CarnivalsPojo;
import com.techplicit.mycarnival.ui.activities.BandTabsActivity;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class CarnivalsListActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, Constants {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_carnivals_list);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle("");

        ImageView home_icon = (ImageView)findViewById(R.id.home_icon);
        home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentGenerator.startHomeActivity(CarnivalsListActivity.this);
            }
        });

        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){

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
                .commit();*/

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
//                closeDrawer();
                break;
            case 2:
//                closeDrawer();
                break;
            case 3:
//                closeDrawer();
                break;
            case 4:
//                closeDrawer();
                break;
        }
    }

    private void closeDrawer(){
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

            Log.e("Siva", "onCreateView called");

            View rootView = inflater.inflate(R.layout.fragment_carnivals_list, container, false);
            carnivalsList = (ListView)rootView.findViewById(R.id.list_carnivals);
            ProgressBar carnivalsProgress = (ProgressBar)rootView.findViewById(R.id.progress_carnivals_list);
            carnivalsProgress.setVisibility(View.VISIBLE);
            carnivalsProgress.getIndeterminateDrawable().setColorFilter(0xFFFF0000, android.graphics.PorterDuff.Mode.MULTIPLY);

            carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<CarnivalsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getCarnivalsPojoArrayList();
                    CarnivalsPojo carnivalsPojo = (CarnivalsPojo)carnivalsPojoArrayList.get(position);
                    if (carnivalsPojo.getActiveFlag()){
                        Intent bandIntent = new Intent(getActivity(), BandTabsActivity.class);
                        startActivity(bandIntent);
                    }

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_CARNIVAL, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SELECTED_CARNIVAL_NAME, carnivalsPojo.getName());
                    editor.commit();
                    Log.e("Siva", "SELECTED_CARNIVAL_NAME--> "+carnivalsPojo.getName());

                }
            });

            if (Utility.isNetworkConnectionAvailable(getActivity())){
//                carnivalsProgress.setVisibility(View.GONE);
                new GetAsync(getActivity(), carnivalsProgress).execute();
            }else{
                carnivalsProgress.setVisibility(View.VISIBLE);
                Utility.displayNetworkFailDialog(getActivity(), NETWORK_FAIL);
            }



            ImageView backArrowCarnivalsList = (ImageView)rootView.findViewById(R.id.back_arrow_carnivals_list);
            backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((CarnivalsListActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


        static class GetAsync extends AsyncTask<String, String, JSONArray> {

            private boolean isResponseSucceed;
            ServiceHandler jsonParser = new ServiceHandler();

            private ProgressDialog pDialog;
            private ProgressBar carnivalsProgress;

            private static final String CARNIVALS_URL = Constants.BASE_URL+"getcarnivals";

            private static final String TAG_SUCCESS = "success";
            private static final String TAG_MESSAGE = "message";
            private ArrayList<CarnivalsPojo> quizModelArrayList;

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

                    responseStatus = jsonParser.makeHttpRequest(
                            CARNIVALS_URL, "GET", null);
                    Log.d("Siva", "CARNIVALS_URL---> "+CARNIVALS_URL);
                    if (responseStatus!=null && !responseStatus.equalsIgnoreCase(ERROR)){
                        jsonData = jsonParser.makeHttpRequest(
                                CARNIVALS_URL, "GET", null);

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

                    CarnivalsSingleton.getInstance().setCarnivalsJsonResponse(jsonArray);

                    if (CarnivalsSingleton.getInstance().getCarnivalsJsonResponse()!=null){
                        quizModelArrayList = CarnivalsSingleton.getInstance().getCarnivalsPojoArrayList();

                        carnivalsList.setAdapter(new CarnivalsListAdapter(mContext,quizModelArrayList));
                        this.carnivalsProgress.setVisibility(View.GONE);
                    }
                }

            }

        }


    }




}
