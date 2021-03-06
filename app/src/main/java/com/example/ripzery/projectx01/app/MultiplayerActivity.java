package com.example.ripzery.projectx01.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.util.MyRealTimeMessageReceived;
import com.example.ripzery.projectx01.util.MyUpdateRoomListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;

public class MultiplayerActivity extends ActionBarActivity implements SignInFragment.OnFragmentInteractionListener, MainMultiplayerFragment.OnFragmentInteractionListener, MapsFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnInvitationReceivedListener, FragmentGameMultiplayerStatus.OnFragmentInteractionListener {

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "KeepRunningMultiplayer";
    private static final int SCREEN_MAIN = 0;
    private static final int SCREEN_SIGN_IN = 1;
    private static final int SCREEN_WAIT = 2;
    private static final int SCREEN_GAME = 3;
    private static final int RC_WAITING_ROOM = 10002;
    private static final int REQ_PLAY_GAME = 10003;
    // The participants in the currently active game
    public ArrayList<Participant> mParticipants = null;
    GoogleApiClient mGoogleApiClient;
    String mIncomingInvitationId = null;
    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    String mRoomId = null;
    // My participant ID in the currently active game
    String mMyId = null;
    private MainMultiplayerFragment mainPlayerFragment;
    private SignInFragment signInFragment;
    private FragmentTransaction transaction;
    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;
    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;
    // Set to true to automatically start the sign in flow when the Activity starts.
    // Set to false to require the user to click the button in order to sign in.
    private boolean mAutoStartSignInFlow = true;
    private WaitFragment waitFragment;
    private int mCurScreen = -1;
    private Button btnAcceptInvitation;
    private FragmentGame fragmentGame;
    private MapsFragment mapsFragment;
    private FragmentGameMultiplayerStatus fragmentGameMultiplayerStatus;
    private Singleton mSing;
    private MyUpdateRoomListener roomUpdateListener;
    private MyRealTimeMessageReceived myRealTimeMessageReceived;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

//        mSing = Singleton.getInstance();

        if (signInFragment == null)
            signInFragment = SignInFragment.newInstance("signIn", "signIn");
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, signInFragment);
        transaction.commit();

        roomUpdateListener = new MyUpdateRoomListener(this);
        Singleton.myRealTimeMessageReceived = new MyRealTimeMessageReceived();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        Singleton.mGoogleApiClient = mGoogleApiClient;

        btnAcceptInvitation = (Button) findViewById(R.id.button_accept_popup_invitation);
        btnAcceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptInviteToRoom(mIncomingInvitationId);
                mIncomingInvitationId = null;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multiplayer, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSignInPressed() {
// Create the Google Api Client with access to Plus and Games
        Log.d(TAG, "Sign-in button clicked");
        if (!BaseGameUtils.verifySampleSetup(this, R.string.app_id)) {
            Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
        }
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected() called. Sign in successful!");

        Log.d(TAG, "Sign-in succeeded.");

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        if (connectionHint != null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null && inv.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Log.d(TAG, "onConnected: connection hint has a room invite!");
                acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        }
        switchToMainScreen();
    }

    public void switchToMainScreen() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            switchToScreen(SCREEN_MAIN);
        } else {
            switchToScreen(SCREEN_SIGN_IN);
        }
    }

    void switchToScreen(int screenId) {
        Log.d(TAG, "Screen ID : " + screenId);
        mCurScreen = screenId;
        switch (screenId) {
            case SCREEN_MAIN:
                if (mainPlayerFragment == null)
                    mainPlayerFragment = MainMultiplayerFragment.newInstance("main", "main");
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, mainPlayerFragment);
//                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
                break;
            case SCREEN_SIGN_IN:

                if (signInFragment == null)
                    signInFragment = SignInFragment.newInstance("signIn", "signIn");
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, signInFragment);
//                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
                break;
            case SCREEN_WAIT:
                if (waitFragment == null)
                    waitFragment = WaitFragment.newInstance("wait", "wait");
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, waitFragment);
                transaction.commitAllowingStateLoss();
                break;
            case SCREEN_GAME:
//                if (fragmentGame == null)
//                    mapsFragment = MapsFragment.newInstance("test","das");
//                transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, mapsFragment);
//                transaction.commit();
//                startActivityForResult(new Intent(this, MapsMultiplayerActivity.class), REQ_PLAY_GAME);
                startActivityForResult(new Intent(this, MultiplayerMapsActivity.class), REQ_PLAY_GAME);
                break;
        }
        // should we show the invitation popup?
        boolean showInvPopup;
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvPopup = false;
        } else {
            // if in multiplayer, only show invitation on main screen
            showInvPopup = (mCurScreen == SCREEN_MAIN);
        }
        findViewById(R.id.invitation_popup).setVisibility(showInvPopup ? View.VISIBLE : View.GONE);
    }

    public void startGame() {
        switchToScreen(SCREEN_GAME);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                // we got the result from the "select players" UI -- ready to create the room
                handleSelectPlayersResult(responseCode, intent);
                break;
            case RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI.
                if (responseCode == Activity.RESULT_OK) {
                    // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).");
                    startGame();
                } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that they want to leave the room
                    leaveRoom();
                } else if (responseCode == Activity.RESULT_CANCELED) {
                    // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                    leaveRoom();
                }
                break;
            case RC_SIGN_IN:
                Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
                        + responseCode + ", intent=" + intent);
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (responseCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    BaseGameUtils.showActivityResultError(this, requestCode, responseCode, R.string.signin_other_error);
                }
                break;
            case REQ_PLAY_GAME:
                switchToMainScreen();
                break;
        }
        super.onActivityResult(requestCode, responseCode, intent);
    }

    // Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.
    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Log.d(TAG, "Creating room...");
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(roomUpdateListener);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(Singleton.myRealTimeMessageReceived);
        rtmConfigBuilder.setRoomStatusUpdateListener(roomUpdateListener);
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        switchToScreen(SCREEN_WAIT);
        keepScreenOn();
//        resetGameVars();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    public void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(roomUpdateListener);
        rtmConfigBuilder.setMessageReceivedListener(Singleton.myRealTimeMessageReceived);
        rtmConfigBuilder.setRoomStatusUpdateListener(roomUpdateListener);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        switchToScreen(SCREEN_WAIT);
        keepScreenOn();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
    }


    // Accept the given invitation.
    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invId);
        if (invId != null) {
            RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(roomUpdateListener);
            // TODO : invitation id is null don't know why!
            roomConfigBuilder.setInvitationIdToAccept(invId)
                    .setMessageReceivedListener(Singleton.myRealTimeMessageReceived)
                    .setRoomStatusUpdateListener(roomUpdateListener);
            switchToScreen(SCREEN_WAIT);
            keepScreenOn();
            Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
        }

    }

    // Show the waiting room UI to track the progress of other players as they enter the
    // room and get connected.
    public void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);

        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    // Activity is going to the background. We have to leave the current room.
    @Override
    public void onStop() {
        Log.d(TAG, "**** got onStop");

        // if we're in a room, leave it.
        if (mCurScreen != SCREEN_GAME)
            leaveRoom();

        // stop trying to keep the screen on
        stopKeepingScreenOn();

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            switchToScreen(SCREEN_SIGN_IN);
        }
        super.onStop();
    }

    // Activity just got to the foreground. We switch to the wait screen because we will now
    // go through the sign-in flow (remember that, yes, every time the Activity comes back to the
    // foreground we go through the sign-in flow -- but if the user is already authenticated,
    // this flow simply succeeds and is imperceptible).
    @Override
    public void onStart() {
        if (mCurScreen != SCREEN_GAME)
//            switchToScreen(SCREEN_WAIT);
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                Log.w(TAG,
                        "GameHelper: client was already connected on onStart()");
            } else {
                Log.d(TAG, "Connecting client.");
                mGoogleApiClient.connect();
            }
        super.onStart();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, R.string.signin_other_error);
        }

        switchToScreen(SCREEN_SIGN_IN);
    }

    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in
        // mIncomingInvitationId
        // and show the popup on the screen.

        mIncomingInvitationId = invitation.getInvitationId();
        Log.d(TAG, "Recieved Invitation : " + mIncomingInvitationId);
        ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                invitation.getInviter().getDisplayName() + " is challenging you into the game!");
        switchToScreen(mCurScreen); // This will show the invitation popup
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        Log.d(TAG, "Removed Invitation : " + mIncomingInvitationId);
        if (mIncomingInvitationId.equals(invitationId)) {
            mIncomingInvitationId = null;
            switchToScreen(mCurScreen); // This will hide the invitation popup
        }
    }

    // Leave the room.
    void leaveRoom() {
        Log.d(TAG, "Leaving room.");
        stopKeepingScreenOn();
        if (Singleton.mRoomId != null) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, roomUpdateListener, Singleton.mRoomId);
            Singleton.mRoomId = null;
            switchToScreen(SCREEN_WAIT);
        } else {
            switchToMainScreen();
        }
    }

    // Show error message about game being cancelled and return to main screen.
    public void showGameError() {
        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        switchToMainScreen();
    }

    // Handle back key to make sure we cleanly leave a game if we are in the middle of one
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mCurScreen == SCREEN_GAME) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    leaveRoom();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.setTitle("Stop playing?");
            dialog.setMessage("Your current progress won't saved");
            dialog.show();

            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMainMultiplayerButtonClicked(int id) {
        Intent intent;
        switch (id) {
            case R.id.signOutButton:
                Log.d(TAG, "Sign-out button clicked");
                if (mGoogleApiClient.isConnected()) {
                    mSignInClicked = false;
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    switchToScreen(SCREEN_SIGN_IN);
                }
                break;
            case R.id.inviteButton:
                intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
                switchToScreen(SCREEN_WAIT);
                startActivityForResult(intent, RC_SELECT_PLAYERS);
                break;
            case R.id.quickMatchButton:
                startQuickGame();
                break;
        }
    }

    @Override
    public void onUpdate() {
        Log.d(TAG,"update");
    }

    @Override
    public void onBroadcastPlayerStatus() {

    }

//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }
}
