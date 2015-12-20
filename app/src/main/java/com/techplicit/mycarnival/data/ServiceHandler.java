package com.techplicit.mycarnival.data;

import android.util.Log;

import com.techplicit.mycarnival.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class ServiceHandler implements Constants{

    String charset = "UTF-8";
    HttpURLConnection conn;
    DataOutputStream wr;
    StringBuilder result;
    URL urlObj;
    JSONObject jObj = null;
    StringBuilder sbParams;
    String paramsString;
    String responseStatus;
    public String makeHttpRequest(String url, String method,
                                      HashMap<String, String> params) {

        sbParams = new StringBuilder();
        int i = 0;

        if (params!=null){
            for (String key : params.keySet()) {
                try {
                    if (i != 0){
                        sbParams.append("&");
                    }
                    sbParams.append(key).append("=")
                            .append(URLEncoder.encode(params.get(key), charset));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    responseStatus = ERROR;
                }
                i++;
            }
        }


        if (method.equals("POST")) {
            // request method is POST
            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(true);

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Accept-Charset", charset);

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.connect();

                paramsString = sbParams.toString();

                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();

            } catch (IOException e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }
        }
        else if(method.equals("GET")){
            // request method is GET

            if(params!=null){
                if (sbParams.length() != 0) {
                    url += "?" + sbParams.toString();
                }
            }


            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(false);

                conn.setRequestMethod("GET");

                conn.setRequestProperty("Accept-Charset", charset);

                conn.setConnectTimeout(15000);

                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
                responseStatus = ERROR;
            }

        }

        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
            responseStatus = ERROR;
        }

        conn.disconnect();

        // try parse the string to a JSON object
        /*try {

            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }*/

        if (responseStatus!=null && responseStatus.equalsIgnoreCase(ERROR)){
            return responseStatus;
        }

        // return JSON Object
        return result.toString();
    }
}