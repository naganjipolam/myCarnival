package com.techplicit.mycarnival.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.model.BandsPojo;
import com.techplicit.mycarnival.utils.BandsDateFormatsConverter;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.ImageLoader;
import com.techplicit.mycarnival.utils.RoundedCornersImage;
import com.techplicit.mycarnival.utils.Utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by pnaganjane001 on 14/11/15.
 */
public class BandsGridAdapter extends BaseAdapter implements Constants {

    private Context context;
    int[] galleryImages;
    LayoutInflater inflater;
    private ArrayList<BandsPojo> bandsPojoArrayList;
    ImageLoader imageLoader;
    private BandsPojo bandsPojo;
    private String[] strArr;
    private String startMonth;
    private String[] endArr;
    private String endMonth;

    public BandsGridAdapter(Context context, ArrayList<BandsPojo> carnivalsPojoArrayList) {
        this.context = context;
        this.bandsPojoArrayList = carnivalsPojoArrayList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return this.bandsPojoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder = new ViewHolder();
        final View rootView;
        rootView = inflater.inflate(R.layout.bands_list_item, null);
        holder.galleryImage = (ImageView)rootView.findViewById(R.id.image_band);
//        holder.galleryImage.setImageResource(galleryImages[position]);

        holder.titleBand = (TextView)rootView.findViewById(R.id.title_band);
        holder.subTitleBand = (TextView)rootView.findViewById(R.id.sub_title_band);
        holder.timeBand = (TextView)rootView.findViewById(R.id.time_band);
        holder.updatesBand = (TextView)rootView.findViewById(R.id.updates_band);

        bandsPojo = (BandsPojo)this.bandsPojoArrayList.get(position);

        String lastAccessOn = Utility.getDate(Long.valueOf(bandsPojo.getLastUpdated()), "MM/dd/yyyy HH:mm:ss");

        Log.e("Siva", "lastAccessOn --> " +lastAccessOn);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String currentDate = format.format(c.getTime());
        Log.e("Siva", "currentDate --> " +currentDate);
        Date d1 = new Date();
        Date d2 = null;

        try {
            d1 = format.parse(currentDate);
            d2 = format.parse(lastAccessOn);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // in milliseconds
        long diff = d1.getTime() - d2.getTime();

        String lastUpdateStatus = BandsDateFormatsConverter.printDateDifferenceInUIWithDefinedFormat(diff);

        if (lastUpdateStatus!=null && !lastUpdateStatus.equalsIgnoreCase("")){
            holder.timeBand.setText(lastUpdateStatus);
        }


/*
        String endDate = Utility.getDate(Long.valueOf(bandsPojo.getEndDate()), "dd/MM/yyyy");

        if (endDate!=null && endDate.contains("/")){
            endArr = endDate.split("/");
            endMonth = Utility.getMonth(Integer.valueOf(endArr[1]));
        }

        if (startMonth.equalsIgnoreCase(endMonth)){
            holder.timeBand.setText(startMonth +" "+strArr[0]+" - "+endArr[0]);
        }else{
            holder.timeBand.setText(startMonth +" "+strArr[0]+" - "+endMonth+" "+endArr[0]);
        }*/

        holder.titleBand.setText(bandsPojo.getName());
        holder.subTitleBand.setText(bandsPojo.getAddress());
        holder.updatesBand.setText(bandsPojo.getUpdates()+" UPDATES");

        Log.e("Siva", "bands image--> "+bandsPojo.getImage());

        rootView.setId(position);
        Bitmap bitmap = null;
        if (bandsPojo.getImage()!=null){
            bitmap = imageLoader.DisplayImage(bandsPojo.getImage(), holder.galleryImage);
        }

        /*if (bitmap!=null){
            Bitmap circularBitmap = RoundedCornersImage.getRoundedCornerBitmap(bitmap);
            holder.galleryImage.setImageBitmap(circularBitmap);
        }*/

        return rootView;
    }

    public class ViewHolder{
        ImageView galleryImage;
        TextView titleBand;
        TextView subTitleBand;
        TextView timeBand;
        TextView updatesBand;
    }
}
