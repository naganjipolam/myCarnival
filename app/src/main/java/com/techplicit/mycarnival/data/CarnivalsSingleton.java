package com.techplicit.mycarnival.data;

import android.util.Log;

import com.techplicit.mycarnival.data.model.BandsPojo;
import com.techplicit.mycarnival.data.model.CarnivalsPojo;
import com.techplicit.mycarnival.data.model.SortedDistanceBandsPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by pnaganjane001 on 17/12/15.
 */
public class CarnivalsSingleton {

    private static final String TAG = CarnivalsSingleton.class.getName();
    private static CarnivalsSingleton instance = null;
    private JSONArray mCarnivalsJsonResponse, mBandsJsonResponse;
    private ArrayList<CarnivalsPojo> mCarnivalsPojoArrayList;
    private ArrayList<BandsPojo> mBandsPojoArrayList;
    private ArrayList<SortedDistanceBandsPojo> mDistanceSortedBandsPojoArrayList;

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
        return mCarnivalsPojoArrayList;
    }

    public void setCarnivalsJsonResponse(JSONArray mJsonResponse) {
        this.mCarnivalsJsonResponse = mJsonResponse;
        mCarnivalsPojoArrayList = new ArrayList<CarnivalsPojo>();
        for (int i=0; i<mCarnivalsJsonResponse.length();i++ ){
            try {
                JSONObject jsonObject = (JSONObject) mCarnivalsJsonResponse.get(i);
                CarnivalsPojo pojo = new CarnivalsPojo(jsonObject);
                mCarnivalsPojoArrayList.add(pojo);
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
        return mBandsPojoArrayList;
    }

    public ArrayList<SortedDistanceBandsPojo> getSortedDistanceBandsPojoArrayList() {
        return mDistanceSortedBandsPojoArrayList;
    }

    public void setDistanceSortedBandsPojoArrayList(ArrayList<SortedDistanceBandsPojo> mDistanceSortedBandsPojoArrayList) {
        this.mDistanceSortedBandsPojoArrayList = mDistanceSortedBandsPojoArrayList;
        Log.e("Singleton", "mDistanceSortedBandsPojoArrayList size--> "+mDistanceSortedBandsPojoArrayList.size());
    }

    public void setBandsJsonResponse(JSONArray mJsonResponse) {
        this.mBandsJsonResponse = mJsonResponse;
        mBandsPojoArrayList = new ArrayList<BandsPojo>();
        for (int i=0; i<mBandsJsonResponse.length();i++ ){
            try {
                JSONObject jsonObject = (JSONObject) mBandsJsonResponse.get(i);
                BandsPojo pojo = new BandsPojo(jsonObject);
                mBandsPojoArrayList.add(pojo);


            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "JSON Exception");
            }
        }

    }

    public void clear (){
        mBandsJsonResponse = null;
        mBandsPojoArrayList = null;
        mCarnivalsJsonResponse = null;
        mCarnivalsPojoArrayList = null;
    }

    public void clearCarnivalsData (){
        mCarnivalsJsonResponse = null;
        mCarnivalsPojoArrayList = null;
    }

    public void clearBandsData (){
        mBandsJsonResponse = null;
        mBandsPojoArrayList = null;
        mDistanceSortedBandsPojoArrayList = null;
    }
}
