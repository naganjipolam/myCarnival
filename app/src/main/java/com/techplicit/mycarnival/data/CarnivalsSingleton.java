package com.techplicit.mycarnival.data;

import android.util.Log;

import com.techplicit.mycarnival.data.model.BandsPojo;
import com.techplicit.mycarnival.data.model.CarnivalsPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class CarnivalsSingleton {

    private static final String TAG = CarnivalsSingleton.class.getName();
    private static CarnivalsSingleton instance = null;
    private JSONArray mCarnivalsJsonResponse, mBandsJsonResponse;
    private ArrayList<CarnivalsPojo> carnivalsPojoArrayList;
    private ArrayList<BandsPojo> bandsPojoArrayList;

    private CarnivalsSingleton() {
    }

    public static CarnivalsSingleton getInstance() {
        if (instance == null) {
            synchronized (CarnivalsSingleton.class) {
                if (instance == null) {
                    instance = new CarnivalsSingleton();
                }
            }
        }
        return instance;
    }

    public JSONArray getCarnivalsJsonResponse() {
        return mCarnivalsJsonResponse;
    }

    public ArrayList<CarnivalsPojo> getCarnivalsPojoArrayList() {
        return carnivalsPojoArrayList;
    }

    public void setCarnivalsJsonResponse(JSONArray mJsonResponse) {
        this.mCarnivalsJsonResponse = mJsonResponse;
        carnivalsPojoArrayList = new ArrayList<CarnivalsPojo>();
        for (int i=0; i<mCarnivalsJsonResponse.length();i++ ){
            try {
                JSONObject jsonObject = (JSONObject) mCarnivalsJsonResponse.get(i);
                CarnivalsPojo pojo = new CarnivalsPojo(jsonObject);
                carnivalsPojoArrayList.add(pojo);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "JSON Exception");
            }
        }

    }

    public JSONArray getBandsJsonResponse() {
        return mCarnivalsJsonResponse;
    }

    public ArrayList<BandsPojo> getBandsPojoArrayList() {
        return bandsPojoArrayList;
    }

    public void setBandsJsonResponse(JSONArray mJsonResponse) {
        this.mBandsJsonResponse = mJsonResponse;
        bandsPojoArrayList = new ArrayList<BandsPojo>();
        for (int i=0; i<mBandsJsonResponse.length();i++ ){
            try {
                JSONObject jsonObject = (JSONObject) mBandsJsonResponse.get(i);
                BandsPojo pojo = new BandsPojo(jsonObject);
                bandsPojoArrayList.add(pojo);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "JSON Exception");
            }
        }

    }

}
