package com.example.ripzery.projectx01;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.cengalabs.flatui.views.FlatButton;

/**
 * Created by visit on 10/26/14 AD.
 */
public class WelcomeScreenFragment extends FragmentActivity {

    private MapsFragment mapsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_welcome_screen);

        FlatButton btnCreatePlayground = (FlatButton) this.findViewById(R.id.btnCreatePlayground);
        FlatButton btnStartGame = (FlatButton) this.findViewById(R.id.btnStartGame);

        btnCreatePlayground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapsFragment = new MapsFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.flMainFrame, mapsFragment);
//                transaction.addToBackStack(null);
                transaction.commit();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.fragmentWelcomeScreen);
                linearLayout.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
