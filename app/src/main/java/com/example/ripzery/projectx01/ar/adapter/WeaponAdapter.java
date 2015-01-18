package com.example.ripzery.projectx01.ar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oakraw.testmagnetic.MainActivity;
import com.oakraw.testmagnetic.R;
import com.oakraw.testmagnetic.detail.weapon.Gun;

import java.util.ArrayList;

/**
 * Created by Rawipol on 1/12/15 AD.
 */
public class WeaponAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Gun> guns;

    public WeaponAdapter(Context mContext, ArrayList<Gun> guns) {
        this.mContext = mContext;
        this.guns = guns;
    }

    @Override
    public int getCount() {
        return guns.size();
    }

    @Override
    public Object getItem(int position) {
        return guns.get(position);
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
                ((MainActivity)mContext).addView(guns.get(position));
            }
        });

        img.setImageResource(guns.get(position).getGun_img());
        name.setText(guns.get(position).getName());
        return convertView;
    }
}
