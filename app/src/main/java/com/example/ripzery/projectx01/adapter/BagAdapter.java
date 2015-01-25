package com.example.ripzery.projectx01.adapter;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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


        final SquareImageButton imageButton;
        if (convertView == null) {
            imageButton = new SquareImageButton(mContext);
            imageButton.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageButton.setPadding(8, 8, 8, 8);
            //imageButton.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            imageButton.setBackgroundResource(R.drawable.round_corner);
            imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        } else {
            imageButton = (SquareImageButton) convertView;
        }

        // ถ้าเป็นปืน
        if (position < Me.guns.size()) {

            imageButton.setImageResource(Me.guns.get(position).get_thumb());
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Me.chosenGun = position;
                    ((MapsActivity) mContext).passAllMonster();

                }
            });
            //ถ้าเป็นไอเทม
        } else {
            final Item item = Me.items.get(position - Me.guns.size());
            imageButton.setImageResource(item.getThumb());
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (item.getType()) {
                        case "Distancex2":
                            Me.distanceMultiplier = 2;
                            handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Me.distanceMultiplier = 1;
                                }
                            }, 10000);
                            mContext.isUseItem = true;
                            mContext.itemBagLayout.collapsePanel();
                            mContext.setItemAnimation(item.getThumb());
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
                    Me.items.remove(item);
                }
            });
        }


        return imageButton;
    }


}
