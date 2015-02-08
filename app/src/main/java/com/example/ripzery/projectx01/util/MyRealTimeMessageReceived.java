package com.example.ripzery.projectx01.util;

import android.util.Log;

import com.example.ripzery.projectx01.app.MapsMultiplayerActivity;
import com.example.ripzery.projectx01.app.MultiplayerMapsActivity;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

/**
 * Created by visit on 2/8/15 AD.
 */
public class MyRealTimeMessageReceived implements RealTimeMessageReceivedListener {

    private static final String TAG = "MyRealtimeMessage";
    private MapsMultiplayerActivity mapsMultiplayerActivity;
    private MultiplayerMapsActivity multiplayerMapsActivity;

    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] buf = realTimeMessage.getMessageData();
        String sender = realTimeMessage.getSenderParticipantId();
        byte[] distanceByte = new byte[4];
        for (int i = 0; i < distanceByte.length; i++)
            distanceByte[i] = buf[i + 1];

        String distance = "Distance : " + byteArrayToInt(distanceByte);
        Log.d(TAG, "Message received: " + (char) buf[0] + "/" + byteArrayToInt(distanceByte));

        multiplayerMapsActivity.getFragmentMultiplayerStatus().getTextView("p2", 1).setText(distance);
    }

    public void setMapsMultiplayerActivity(MapsMultiplayerActivity mapsMultiplayerActivity) {
        this.mapsMultiplayerActivity = mapsMultiplayerActivity;
    }

    public void setMultiplayerMapsActivity(MultiplayerMapsActivity multiplayerMapsActivity) {
        this.multiplayerMapsActivity = multiplayerMapsActivity;
    }
}
