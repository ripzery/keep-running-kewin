package com.example.ripzery.projectx01;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cengalabs.flatui.views.FlatButton;

/**
 * Created by visit on 10/26/14 AD.
 */
public class WelcomeScreenActivity extends Activity {

    private MapsActivity mapsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
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
//                mapsActivity = new MapsActivity();
                Intent intent = new Intent(WelcomeScreenActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);

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
