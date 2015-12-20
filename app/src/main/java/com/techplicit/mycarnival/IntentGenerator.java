package com.techplicit.mycarnival;

import android.content.Context;
import android.content.Intent;

import com.techplicit.mycarnival.ui.activities.UpdateBandLocationActivity;
import com.techplicit.mycarnival.utils.Constants;

/**
 * Created by pnaganjane001 on 19/12/15.
 */
public class IntentGenerator implements Constants{


    public static void startUpdateBandLocation(Context context, int position, String bandName){
        Intent i = new Intent(context, UpdateBandLocationActivity.class);
        i.putExtra(POSITION, position);
        i.putExtra(BAND_NAME, bandName);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
