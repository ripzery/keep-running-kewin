package com.example.ripzery.projectx01.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.app.MapsActivity;


/**
 * Created by Rawipol on 1/18/15 AD.
 */
public class BagAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] mThumbIds = {
            R.drawable.desert_thumb, R.drawable.desert_thumb, R.drawable.desert_thumb, R.drawable.desert_thumb, R.drawable.desert_thumb
            , R.drawable.desert_thumb, R.drawable.desert_thumb, R.drawable.desert_thumb, R.drawable.desert_thumb, R.drawable.desert_thumb
            , R.drawable.desert_thumb, R.drawable.desert_thumb, R.drawable.desert_thumb, R.drawable.desert_thumb, R.drawable.desert_thumb
    };

    public BagAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageButton imageButton;
        if (convertView == null) {
            imageButton = new ImageButton(mContext);
            imageButton.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            imageButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            imageButton.setPadding(8, 8, 8, 8);
            imageButton.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

        } else {
            imageButton = (ImageButton) convertView;
        }


        imageButton.setImageResource(mThumbIds[position]);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MapsActivity) mContext).passAllMonster();
            }
        });
        return imageButton;
    }
}
