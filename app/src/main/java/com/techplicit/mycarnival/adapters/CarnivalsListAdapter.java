package com.techplicit.mycarnival.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.techplicit.mycarnival.R;
import com.techplicit.mycarnival.data.model.CarnivalsPojo;
import com.techplicit.mycarnival.utils.Constants;
import com.techplicit.mycarnival.utils.ImageLoader;
import com.techplicit.mycarnival.utils.RoundedCornersImage;
import com.techplicit.mycarnival.utils.Utility;

import java.util.ArrayList;


/**
 * Created by pnaganjane001 on 14/11/15.
 */
public class CarnivalsListAdapter extends BaseAdapter implements Constants {

    private Context context;
    int[] galleryImages;
    LayoutInflater inflater;
    private ArrayList<CarnivalsPojo> carnivalsPojoArrayList;
    ImageLoader imageLoader;
    private CarnivalsPojo carnivalsPojo;
    private String[] strArr;
    private String startMonth;
    private String[] endArr;
    private String endMonth;

    public CarnivalsListAdapter(Context context, ArrayList<CarnivalsPojo> carnivalsPojoArrayList) {
        this.context = context;
        this.carnivalsPojoArrayList = carnivalsPojoArrayList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return this.carnivalsPojoArrayList.size();
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
        rootView = inflater.inflate(R.layout.carnivals_list_item, null);
        holder.galleryImage = (ImageView)rootView.findViewById(R.id.image_carnival);
//        holder.galleryImage.setImageResource(galleryImages[position]);

        holder.titleCarnival = (TextView)rootView.findViewById(R.id.title_carnival_list);
        holder.dateCarnival = (TextView)rootView.findViewById(R.id.date_carnival_list);

        carnivalsPojo = (CarnivalsPojo)this.carnivalsPojoArrayList.get(position);

        String startDate = Utility.getDate(Long.valueOf(carnivalsPojo.getStartDate()), "dd/MM/yyyy");

        if (startDate!=null && startDate.contains("/")){
            strArr = startDate.split("/");
            startMonth = Utility.getMonth(Integer.valueOf(strArr[1]));
        }


        String endDate = Utility.getDate(Long.valueOf(carnivalsPojo.getEndDate()), "dd/MM/yyyy");

        if (endDate!=null && endDate.contains("/")){
            endArr = endDate.split("/");
            endMonth = Utility.getMonth(Integer.valueOf(endArr[1]));
        }

        if (startMonth.equalsIgnoreCase(endMonth)){
            holder.dateCarnival.setText(startMonth +" "+strArr[0]+" - "+endArr[0]);
        }else{
            holder.dateCarnival.setText(startMonth +" "+strArr[0]+" - "+endMonth+" "+endArr[0]);
        }

        holder.titleCarnival.setText(carnivalsPojo.getName());

        rootView.setId(position);
        Bitmap bitmap = null;
        if (carnivalsPojo.getImage()!=null){
            bitmap = imageLoader.DisplayImage(carnivalsPojo.getImage(), holder.galleryImage);
        }

        if (bitmap!=null){
            Bitmap circularBitmap = RoundedCornersImage.getRoundedCornerBitmap(bitmap);
            holder.galleryImage.setImageBitmap(circularBitmap);
        }

        return rootView;
    }

    public class ViewHolder{
        ImageView galleryImage;
        TextView titleCarnival;
        TextView dateCarnival;
    }
}
