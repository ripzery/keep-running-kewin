package com.example.ripzery.projectx01.ar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.ar.MainActivity;
import com.example.ripzery.projectx01.ar.detail.Me;
import com.example.ripzery.projectx01.ar.detail.weapon.Gun;

import java.util.ArrayList;

/**
 * Created by Rawipol on 1/12/15 AD.
 */
public class WeaponAdapter extends BaseAdapter {

    private Context mContext;

    public WeaponAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return Me.guns.size();
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.weapon_detail, parent, false);

        ImageView img = (ImageView)convertView.findViewById(R.id.gun_img);
        TextView name = (TextView)convertView.findViewById(R.id.name);
        convertView.findViewById(R.id.weapon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)mContext).addView(Me.guns.get(position));
            }
        });

        img.setImageResource(Me.guns.get(position).get_img());
        name.setText(Me.guns.get(position).getName());
        return convertView;
    }
}
