package com.example.ripzery.projectx01.adapter;

import android.app.Dialog;
import android.content.Context;
import android.database.MatrixCursor;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.app.MapsActivity;
import com.example.ripzery.projectx01.ar.detail.Me;
import com.example.ripzery.projectx01.custom.SquareImageButton;
import com.example.ripzery.projectx01.interface_model.Item;


/**
 * Created by Rawipol on 1/18/15 AD.
 */
public class BagAdapter extends BaseAdapter {
    private Handler handler1;
    //private final Desert gun;
    private MapsActivity mContext;
    /*private Integer[] mThumbIds = {
            R.drawable.desert_eagle, R.drawable.pistol, R.drawable.knife
    };*/

    public BagAdapter(MapsActivity c) {
        mContext = c;
        handler1 = new Handler();
        //gun = new Desert(mContext, 40);
    }

    @Override
    public int getCount() {

        if(Me.guns.size() + Me.items.size() >= 12)
            return 12;
        else
            return Me.guns.size() + Me.items.size();

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



        if (convertView == null) {
           /* imageButton = new SquareImageButton(mContext);
            imageButton.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageButton.setPadding(8, 8, 8, 8);
            imageButton.setBackgroundResource(R.drawable.round_corner_btn);
            imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);*/
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.slot_bag, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.img);
        final ToggleButton toggleButton = (ToggleButton) convertView.findViewById(R.id.toggleButton);
        TextView number = (TextView) convertView.findViewById(R.id.number_weapon);


        // ถ้าเป็นปืน
        if (position < Me.guns.size()) {

            image.setImageResource(Me.guns.get(position).getThumb());
            number.setText(Me.guns.get(position).getBullet()+"");
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Me.selectGun = true;
                        Me.chosenGun = position;
                        ((MapsActivity) mContext).passAllMonster(true,toggleButton);

                    }else{
                        Me.selectGun = false;
                        ((MapsActivity) mContext).passAllMonster(false,null);

                    }
                }
            });

              /*      .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Me.chosenGun = position;
                    ((MapsActivity) mContext).passAllMonster();

                }
            });*/
            //ถ้าเป็นไอเทม
        } else {
            final Item item = Me.items.get(position - Me.guns.size());
            image.setImageResource(item.getThumb());
            image.setImageResource(Me.items.get(position - Me.guns.size()).getThumb());
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (item.getType()) {
                        case "Distancex2":
                            Me.distanceMultiplier = 2;
                            break;
                        case "Distancex3":
                            Me.distanceMultiplier = 3;
                            handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Me.distanceMultiplier = 1;
                                }
                            }, 10000);
                            break;
                        case "Shield":
                            break;
                    }
                    mContext.isUseItem = true;
                    mContext.itemBagLayout.collapsePanel();
                    mContext.setItemAnimation(item);
                    Me.items.remove(item);
                    notifyDataSetChanged();
                }
            });
        }


        return convertView;
    }





}
