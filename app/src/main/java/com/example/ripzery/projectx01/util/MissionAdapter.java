package com.example.ripzery.projectx01.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ripzery.projectx01.MapsActivity;
import com.example.ripzery.projectx01.R;

import java.util.ArrayList;

/**
 * Created by oakraw90 on 11/4/2014.
 */
public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> {

    private ArrayList<MissionData> missionDatas;
    private Activity parentActivity;

    public MissionAdapter(ArrayList<MissionData> missionDatas, Activity context) {
        this.missionDatas = missionDatas;
        this.parentActivity = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mission_card, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mTextView.setText(missionDatas.get(i).getName());
        viewHolder.mThumbnail.setImageResource(missionDatas.get(i).getThumbnail());
        viewHolder.mThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parentActivity, MapsActivity.class);
                parentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return missionDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageButton mThumbnail;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.info_text);
            mThumbnail = (ImageButton) v.findViewById(R.id.thumbnail);
        }
    }
}
