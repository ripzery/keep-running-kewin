package com.example.ripzery.projectx01.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.game.MissionDetail;
import com.example.ripzery.projectx01.util.MissionAdapter;

public class CampaignActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        MissionDetail missionDetail = new MissionDetail();
        mAdapter = new MissionAdapter(missionDetail.getMissionDatas(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

}
