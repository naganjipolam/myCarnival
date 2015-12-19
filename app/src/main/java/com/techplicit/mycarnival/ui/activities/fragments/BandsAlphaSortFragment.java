package com.techplicit.mycarnival.ui.activities.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.adapters.BandsGridAdapter;
import com.techplicit.mycarnival.adapters.CarnivalsListAdapter;
import com.techplicit.mycarnival.data.CarnivalsSingleton;
import com.techplicit.mycarnival.data.ServiceHandler;
import com.techplicit.mycarnival.data.model.BandsPojo;
import com.techplicit.mycarnival.data.model.CarnivalsPojo;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 18/12/15.
 */
public class BandsAlphaSortFragment extends Fragment implements Constants{
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
            carnivalsList = (ListView)rootView.findViewById(R.id.list_carnivals);
            ProgressBar carnivalsProgress = (ProgressBar)rootView.findViewById(R.id.progress_carnivals_list);


            carnivalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<CarnivalsPojo> carnivalsPojoArrayList = CarnivalsSingleton.getInstance().getCarnivalsPojoArrayList();
                    CarnivalsPojo carnivalsPojo = (CarnivalsPojo)carnivalsPojoArrayList.get(position);
                    if (carnivalsPojo.getActiveFlag()){
                        Toast.makeText(getActivity().getApplicationContext(), "True", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "False", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (Utility.isNetworkConnectionAvailable(getActivity())){
//                carnivalsProgress.setVisibility(View.GONE);
                new GetAsync(getActivity().getApplicationContext(), carnivalsProgress).execute();
            }else{
                carnivalsProgress.setVisibility(View.VISIBLE);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
                alertDialogBuilder.setMessage(getResources().getString(R.string.network_fail_message));
                alertDialogBuilder.setTitle(getResources().getString(R.string.network_status));
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (alertDialog != null) {
                            alertDialog.dismiss();
                            getActivity().finish();
                        }
                    }
                });

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }



            /*ImageView backArrowCarnivalsList = (ImageView)rootView.findViewById(R.id.back_arrow_carnivals_list);
            backArrowCarnivalsList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });*/

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

            private static final String BANDS_URL = Constants.BASE_URL+"getbandslocationoverview?carnival=";

            private static final String TAG_SUCCESS = "success";
            private static final String TAG_MESSAGE = "message";
            private ArrayList<BandsPojo> quizModelArrayList;

            private Context mContext;
            public GetAsync(Context context, ProgressBar carnivalsProgress) {
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

//                HashMap<String, String> params = new HashMap<>();
//                params.put("name", args[0]);
//                params.put("password", args[1]);
//
//                Log.d("request", "starting");

                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_CARNIVAL, Context.MODE_PRIVATE);
                    String selectedCarnivalName = sharedPreferences.getString(SELECTED_CARNIVAL_NAME, "");

                    String selectedCarnivalNameTrimmed = null;

                    if (selectedCarnivalName.contains(" & ")){
                        selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" & ", "+%26+").trim();
                    }else if (selectedCarnivalName.contains(" ")){
                        selectedCarnivalNameTrimmed = selectedCarnivalName.replace(" ", "%20").trim();
                    }
                        String jsonData = jsonParser.makeHttpRequest(
                            BANDS_URL+selectedCarnivalNameTrimmed, "GET", null);

                    Log.e("Siva", "selectedCarnivalName --> "+selectedCarnivalName);
                    Log.e("Siva", "URL --> "+BANDS_URL+selectedCarnivalNameTrimmed);

                    JSONArray jsonArray = null;

                    jsonArray = new JSONArray(jsonData);



                    if (jsonArray != null) {
                        Log.d("JSON result", jsonArray.toString());

                        return jsonArray;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    isResponseSucceed = false;
                }

                return null;
            }

            protected void onPostExecute(JSONArray jsonArray) {

                int success = 0;
                String message = "";

                JSONObject jsonObject = null;
                if (jsonArray != null) {
                /*for (int i=0; i<jsonArray.length();i++ ){
                    try {
                        jsonObject = (JSONObject)jsonArray.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        isResponseSucceed = false;
                    }
                }*/

                    CarnivalsSingleton.getInstance().setBandsJsonResponse(jsonArray);

                    if (CarnivalsSingleton.getInstance().getBandsJsonResponse()!=null){
                        quizModelArrayList = CarnivalsSingleton.getInstance().getBandsPojoArrayList();

                        carnivalsList.setAdapter(new BandsGridAdapter(mContext,quizModelArrayList));
                        this.carnivalsProgress.setVisibility(View.GONE);
                    }
                }


//            if (pDialog != null && pDialog.isShowing()) {
//                pDialog.dismiss();
//            }

            /*if (json != null) {
//                Toast.makeText(getApplicationContext(), json.toString(),
//                        Toast.LENGTH_LONG).show();
                Log.e("Siva", "JSON---> "+json.toString());
                try {
                    success = json.getInt(TAG_SUCCESS);
                    message = json.getString(TAG_MESSAGE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/

                if (success == 1) {
                    Log.d("Success!", message);
                }else{
                    Log.d("Failure", message);
                }
            }

        }





}
