package com.example.ripzery.projectx01.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.ctrlplusz.anytextview.AnyTextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.easing.Glider;
import com.daimajia.easing.Skill;
import com.example.ripzery.projectx01.R;
import com.example.ripzery.projectx01.adapter.BagAdapter;
import com.example.ripzery.projectx01.ar.MainActivity;
import com.example.ripzery.projectx01.ar.detail.Me;
import com.example.ripzery.projectx01.interface_model.Item;
import com.example.ripzery.projectx01.interface_model.Monster;
import com.example.ripzery.projectx01.model.item.ItemDistancex2;
import com.example.ripzery.projectx01.model.item.ItemDistancex3;
import com.example.ripzery.projectx01.model.item.Potion;
import com.example.ripzery.projectx01.model.monster.KingKong;
import com.example.ripzery.projectx01.model.weapon.Desert;
import com.example.ripzery.projectx01.model.weapon.Gun;
import com.example.ripzery.projectx01.model.weapon.Pistol;
import com.example.ripzery.projectx01.util.CheckConnectivity;
import com.example.ripzery.projectx01.util.CheckLocation;
import com.example.ripzery.projectx01.util.ConnectGoogleApiClient;
import com.example.ripzery.projectx01.util.DistanceCalculator;
import com.example.ripzery.projectx01.util.LatLngInterpolator;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.pavlospt.CircleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import at.markushi.ui.CircleButton;
import at.markushi.ui.RevealColorView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MapsActivity extends ActionBarActivity implements SensorEventListener, LocationListener {

    public static final double THRESHOLD_ROT_CAM = 10; // กำหนดระยะทางที่จะต้องวิ่งอย่างต่ำก่อนที่จะหันกล้องไปในทิศที่เราวิ่ง
    public static final double THRESHOLD_ROT_ARROW = 15; // กำหนดองศาที่หมุนโทรศัพท์อย่างน้อย ก่อนที่จะหมุนลูกศรตามทิศที่หัน (ป้องกันลูกศรสั่น)
    public static final double THRESHOLD_ACC = 300; // กำหนด Accuracy ที่ยอมรับได้
    public static final int DATA_ENABLED_REQ = 1;
    public static final int LOCATION_ENABLED_REQ = 2;
    private static int maxDistance = 1000;
    private double distanceGoal = maxDistance;
    public final int MAX_GHOST_AT_ONCE = 5; // กำหนดจำนวนปีศาจมากที่สุดที่จะปรากฎตัวขึ้นพร้อมๆกัน
    public final int MAX_ITEM_AT_ONCE = 3; // กำหนดจำนวนไอเทมสูงสุดในแผนที่
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public ProgressDialog progress;
    public LatLng mCurrentLatLng, mPreviousLatLng;
    public Marker myArrow;
    public GoogleApiClient mGoogleApiClient;
    public CheckLocation checkLocation;
    public boolean isExpanded = false, isUseItem = false;
    public SlidingUpPanelLayout itemBagLayout;
    SensorManager sensorManager;
    @InjectView(R.id.motherView)
    FrameLayout motherView;
    @InjectView(R.id.btnBag)
    CircleButton mBag;
    @InjectView(R.id.cvTextM)
    CircleView mCvDistanceStatus;
    @InjectView(R.id.tvItemCount)
    AnyTextView tvItemCount;
    @InjectView(R.id.cbHome)
    CircleButton cbHome;
    @InjectView(R.id.playerStatus)
    IconRoundCornerProgressBar playerStatus;
    @InjectView(R.id.itemStatus)
    IconRoundCornerProgressBar itemStatus;
    private ArrayList<String> ALL_SELF_ITEM = new ArrayList<>();
    private ArrayList<String> ALL_MONSTER_ITEM = new ArrayList<>();
    private int max_generate_ghost_timeout = 30; // กำหนดระยะเวลาสูงสุดที่ปีศาจจะโผล่ขึ้นมา หน่วยเป็นวินาที
    private int max_generate_item_timeout = 10; // กำหนดระยะเวลาสูงสุดที่ปีศาจจะโผล่ขึ้นมา หน่วยเป็นวินาที
    private int min_generate_ghost_timeout = 10; // กำหนดระยะเวลาต่ำสุดที่ปีศาจจะโผล่ขึ้นมา หน่วยเป็นวินาที
    private int min_generate_item_timeout = 20; // กำหนดระยะเวลาต่ำสุดที่ปีศาจจะโผล่ขึ้นมา หน่วยเป็นวินาที
    private int[] locationDistance, locationItem;
    private LatLngBounds playground;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ArrayList<String> listMonsterName = new ArrayList<String>();
    private ArrayList<Marker> listMarkerMonster = new ArrayList<Marker>();
    private ArrayList<Marker> listMarkerItems = new ArrayList<Marker>();
    private ItemDistancex2 itemDistancex2;
    private AlertDialog.Builder builder;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;
    private float[] accelerometerData = new float[3];
    private float[] magneticData = new float[3];
    private double oldAzimuth = 0;
    private double countDistanceToRotCam = 0;
    private Handler genGhostHandler, genItemHandler;
    private Runnable keepGenerateGhost, keepGenerateItem;
    private boolean isGameStart = false;
    private LocationRequest locationrequest;
    private ArrayList<Monster> allMonsters = new ArrayList<>();
    private ArrayList<Item> allItems = new ArrayList<>();
    private ArrayList<Runnable> allRunnableMonster = new ArrayList<>();
    private int currentBearing = 0;
    private int timeout = 30000;
    private LocationManager locationManager;
    private boolean gpsFix;
    private long locationTime = 0;
    private RevealColorView revealColorView;
    private int backgroundColor;
    private AnimatorSet animationItemBagSet;
    private BagAdapter mBagAdapter;
    private CheckConnectivity connectivity;
    private RelativeLayout optionBar;
    private FloatingActionButton facUseBtn;
    private FloatingActionButton useBtn;
    private Button dropItemBtn;
    private Button detailItemBtn;
    private GridView gView;
    private ConnectGoogleApiClient connectGoogleApiClient;
    private AnimatorSet animateItemUseSet;
    private Handler handlerItemStatus;
    private Runnable runnableItemStatus;
    private Item itemUse;
    private KingKong mMonster;
    private Item generatedItem;
    private float px;
    private float moveDistance;
    private AnimatorSet animationGetItemSet;
    private GoogleMap.OnMarkerClickListener itemMarkerClickListener;
    private Location mCurrentLocation;
    private AlertDialog.Builder endGameDialog;
    private Calendar startGameTime;
    private Calendar endGameTime;
    private int azimut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // setup sensor เพื่อทำให้ลูกศรหมุนตามทิศที่หัน
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // setup class เช็คสถานะการเชื่อม network
        connectivity = new CheckConnectivity(this);

        // setup class เช็คสถานะ Location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkLocation = new CheckLocation(this, locationManager);
        locationManager.addGpsStatusListener(checkLocation);

        // กำหนดให้ screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d("Location Enabled", checkLocation.isLocationEnabled() + "");
        if (!connectivity.is3gConnected() && !connectivity.isWifiConnected()) {
            AlertDialog.Builder setting = new AlertDialog.Builder(this)
                    .setTitle("Please enable mobile data or wifi")
                    .setMessage("Go to setting")
                    .setCancelable(false)
                    .setNegativeButton("Exit game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MapsActivity.this.finish();
                        }
                    })
                    .setNeutralButton("Wifi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), DATA_ENABLED_REQ);
                        }
                    })
                    .setPositiveButton("Mobile Data", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS), DATA_ENABLED_REQ);
                        }
                    });
            setting.show();
        } else if (!checkLocation.isLocationEnabled()) {
            Log.d("Enter", "No?");
            AlertDialog.Builder setting = new AlertDialog.Builder(this)
                    .setTitle("Set location mode to high accuracy")
                    .setMessage("Go to setting")
                    .setCancelable(false)
                    .setNegativeButton("Exit game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MapsActivity.this.finish();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_ENABLED_REQ);
                        }
                    });
            setting.show();
        }

        if (checkLocation.isLocationEnabled() && (connectivity.is3gConnected() || connectivity.isWifiConnected())) {
            setUpMapIfNeeded();
            initVar();
            initListener();
        }
    }

    private void initVar() {
        ButterKnife.inject(this);

        ALL_SELF_ITEM.add("Distancex2");
        ALL_SELF_ITEM.add("Distancex3");
        ALL_SELF_ITEM.add("Potion");

        ALL_MONSTER_ITEM.add("Pistol");
        ALL_MONSTER_ITEM.add("Desert");
//        ALL_MONSTER_ITEM.add("Shotgun");
//        ALL_MONSTER_ITEM.add("Mine");


        // กำหนดค่าเริ่มต้นให้ static var
        Me.myHP = Me.myMaxHP;
        Me.distanceMultiplier = 1;

        // กำหนดค่าเริ่มต้นให้ item
        Me.guns.add(new Desert(this, 14));
        Me.guns.add(new Pistol(this, 60));
        Me.guns.add(new Desert(this, 60));
        Me.items.add(new ItemDistancex2(this));
        Me.items.add(new ItemDistancex2(this));
        Me.items.add(new ItemDistancex2(this));
        Me.items.add(new ItemDistancex2(this));

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        progress = new ProgressDialog(this);
        progress = ProgressDialog.show(this, "Loading", "Wait while loading map...");

        final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        final float pHeight = displayMetrics.heightPixels;
        final float pWidth = displayMetrics.widthPixels;

        animationItemBagSet = new AnimatorSet();
        animationItemBagSet.playTogether(
                Glider.glide(Skill.CircEaseIn, 1200, ObjectAnimator.ofFloat(mBag, "translationY", 0, pHeight / 2 - 100)),
                Glider.glide(Skill.SineEaseIn, 1200, ObjectAnimator.ofFloat(mBag, "translationX", 0, pWidth / 2 - 100))
        );

        animationItemBagSet.setDuration(500);

        animationGetItemSet = new AnimatorSet();

        mBagAdapter = new BagAdapter(this);
        gView = (GridView) findViewById(R.id.gvBag);
        gView.setAdapter(mBagAdapter);

        revealColorView = (RevealColorView) findViewById(R.id.reveal);
        backgroundColor = Color.parseColor("#bdbdbd");

        optionBar = (RelativeLayout)findViewById(R.id.option_bar);
        //เมื่อกดดู detail ของ item
        dropItemBtn = (Button)findViewById(R.id.dropItemBtn);

        detailItemBtn = (Button) findViewById(R.id.detailItemBtn);

        useBtn = (FloatingActionButton) findViewById(R.id.use_btn);

        handlerItemStatus = new Handler();
        //facUseBtn = (FloatingActionButton)findViewById(R.id.use_btn);


        itemBagLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        endGameDialog = new AlertDialog.Builder(MapsActivity.this)
                .setPositiveButton("Result", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNeutralButton("Play again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(false);
    }

    private void initListener() {

        connectGoogleApiClient = new ConnectGoogleApiClient(this);

        // เมื่อแผนที่โหลดเสร็จเรียบร้อยให้เปลี่ยนข้อความ progress จาก Wait while loading map... เป็น Wait while getting your location
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progress.setMessage("Waiting for GPS ...");
                int response = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapsActivity.this);
                if (response == ConnectionResult.SUCCESS) {
                    mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(connectGoogleApiClient)
                            .addOnConnectionFailedListener(connectGoogleApiClient)
                            .build();
                    mGoogleApiClient.connect();
                }
            }
        });

        // TODO : support all self-items 1.Distancex2 2.Distancex3 3.Shield
        // TODO : support all monster-items 1.Pistol 2.Desert 3.Shotgun 4.Mine

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!listMarkerMonster.contains(marker) && Me.items.size() + Me.guns.size() < Me.bagMaxCapacity) {
                    // Handle self-items
                    if (ALL_SELF_ITEM.contains(marker.getTitle())) {
                        Me.items.add(allItems.get(listMarkerItems.indexOf(marker)));
                    }
                    //Handle Monster-items
                    else if (ALL_MONSTER_ITEM.contains(marker.getTitle())) {
                        Me.guns.add((Gun) allItems.get(listMarkerItems.indexOf(marker)));
                    }
                    allItems.remove(listMarkerItems.indexOf(marker));
                    listMarkerItems.remove(marker);
                    marker.remove();
                    mBagAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        dropItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO drop item
                if (Me.chosenGun < Me.guns.size()) {
                    Me.guns.remove(Me.chosenGun);
                } else {
                    Me.items.remove(Me.chosenGun - Me.guns.size());
                }
                mBagAdapter.notifyDataSetChanged();
            }
        });

        detailItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailItemDialog();
            }
        });

        useBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Me.chosenGun < Me.guns.size()) {
                    passAllMonster();
                } else {
                    isUseItem = true;
                    Item useItem = Me.items.get(Me.chosenGun - Me.guns.size());
                    setItemAnimation(useItem);
                    Me.items.remove(useItem);
                    mBagAdapter.notifyDataSetChanged();
                }
            }
        });

        itemBagLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
//                Log.d("slide",""+v);
            }

            @Override
            public void onPanelCollapsed(View view) {
                isExpanded = false;
                mBag.setVisibility(View.VISIBLE);
                gView.setVisibility(View.GONE);
                final Point p = getLocationInView(revealColorView, mBag);
                revealColorView.hide(p.x, p.y, backgroundColor, 0, 300, null);
                mBag.setTranslationX(0);
                mBag.setTranslationY(0);

                if (isUseItem) {
                    isUseItem = false;
                    /// Converts 14 dip into its equivalent px
                    itemStatus.setIconImageResource(itemUse.getThumb());
                    itemStatus.setAlpha(0.7f);
                    final int effectTime = itemUse.getEffectTimeOut();
                    if (itemUse != null && itemUse.getType() != "Potion") {
                        itemStatus.setMax(effectTime);
                        itemStatus.setProgress(effectTime);
                        YoYo.with(Techniques.FadeInLeft)
                                .duration(300)
                                .withListener(new Animator.AnimatorListener() {

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        itemStatus.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        itemStatus.setAlpha(0.7f);

                                        Me.distanceMultiplier = itemUse.getType().equals("Distancex2") ? 2 : 3;
                                        runnableItemStatus = new Runnable() {
                                            int count = 0;

                                            @Override
                                            public void run() {
                                                count++;
                                                itemStatus.setProgress((float) (effectTime - count));
                                                if (count >= effectTime) {
                                                    Me.distanceMultiplier = 1;
                                                    handlerItemStatus.removeCallbacks(this);
                                                    itemStatus.setVisibility(View.INVISIBLE);
                                                } else {
                                                    handlerItemStatus.postDelayed(this, 1000);
                                                }
                                            }
                                        };
                                        handlerItemStatus.postDelayed(runnableItemStatus, 1000);

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .playOn(itemStatus);
                    } else {
                        if (Me.myHP + ((Potion) itemUse).getHeal() <= Me.myMaxHP)
                            Me.myHP += 20;
                        else {
                            Me.myHP = Me.myMaxHP;
                        }
                    }
                }
            }

            @Override
            public void onPanelExpanded(View view) {

                isExpanded = true;
                animationItemBagSet.start();
                animationItemBagSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        final int color = getColor(mBag);
                        final Point p = getLocationInView(revealColorView, mBag);

                        revealColorView.reveal(p.x, p.y, color, mBag.getHeight() / 2, 340, new android.animation.Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(android.animation.Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(android.animation.Animator animator) {
                                gView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(android.animation.Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(android.animation.Animator animator) {

                            }
                        });
                        mBag.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        cbHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MapsActivity.this.finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setTitle("Stop playing?");
                dialog.setMessage("Your current progress won't saved");
                dialog.show();
            }
        });

        // กำหนดค่าเริ่มต้นของ UpdateTime ไว้เป็นเวลาปัจจุบัน
//        previousUpdateTime = System.currentTimeMillis();

    }

    private int getColor(View view) {
        return Color.parseColor((String) view.getTag());
    }

    private Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }

    // TODO : Bug marker move away from player
    public void animateMarker(final Monster monster, final Marker marker, final Location toPosition,
                              final boolean hideMarker, final double speed) {

        // กำหนดเวลาเริ่มต้น
//        final long start = SystemClock.uptimeMillis();

        // ใช้การ animate แบบ Linear (v คงที่)
        final LatLngInterpolator.Linear spherical = new LatLngInterpolator.Linear();

        // แปลงตำแหน่งของ marker จาก Location เป็น onScreen
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        monster.setPoint(startPoint);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        // กำหนดระยะเวลาที่จะ animate โดยขึ้นอยู่กับความเร็วของปีศาจนั้นๆ
        final long initDuration = (long) (DistanceCalculator.getDistanceBetweenMarkersInMetres(marker, toPosition) / (speed / 1000.0));

        // สร้าง interpolator
        final Interpolator interpolator = new LinearInterpolator();

        // สร้าง runnable สำหรับเลื่อตำแหน่งของปีศาจ
        runnable = new Runnable() {
            // กำหนดเวลาวิ่งเริ่มต้น ซึ่งแต่ละตัวจะใช้เวลาวิ่งต่างกัน
            int run_time = 1;

            // กำหนด flag ว่า marker ตัวนี้ได้แจ้งสั่นผู้ใช้เมื่อเข้าใกล้ไปแล้วหรือยัง
            boolean isVibrate = false;

            LatLng newStartLatLng = startLatLng;

            //ปรับเวลาเริ่มต้นการเคลื่อนที่ marker ถ้าผู้ใช้เลื่อนตำแหน่ง
            long adjustStartTime = 0;

            // กำหนดค่าเริ่มต้นของ adjustDuration (จะต้องปรับค่านี้ถ้าผู้เล่นเคลื่อนที่) ให้เท่ากับค่าเริ่มต้น
            long adjustDuration = initDuration;
            PolylineOptions polylineOptions = new PolylineOptions();

            @Override
            public void run() {

                // ถ้าปีศาจตายก็ให้ลบออกจากแผนที่
                if (monster.isDie()) {
                    handler.removeCallbacks(this);
                    if (!listMarkerMonster.isEmpty()) {
                        listMarkerMonster.remove(marker);
                    }
                    if (!listMonsterName.isEmpty())
                        listMonsterName.remove(marker.getTitle());
                    if (!allMonsters.isEmpty()) {
                        allMonsters.remove(monster);
                        Singleton.setAllMonsters(allMonsters);
                    }
                    updateMonsterId();
                    allRunnableMonster.remove(this);
                    if (hideMarker || monster.isDie()) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }

                } else {
                    // ให้เวลาที่ผ่านไป = เวลาปัจจุบัน - เวลาเริ่มต้น animate
                    long elapsed = (run_time * 16) - adjustStartTime;

                    // ถ้าตำแหน่งปัจจุบันของผู้ใช้ != ตำแหน่งที่ปีศาจจะเลื่อนไป
                    if (mCurrentLatLng.latitude != toPosition.getLatitude() || mCurrentLatLng.longitude != toPosition.getLongitude()) {

                        // ถ้าระยะห่างของผีกับตำแหน่งที่จะเลื่อนไปหา มากกว่า ระยะห่างของผีกับตำแหน่งผู้ใช้ ให้ปรับ adjustDuration มีค่าน้อยลง
                        if (DistanceCalculator.getDistanceBetweenMarkersInMetres(marker, toPosition) > DistanceCalculator.getDistanceBetweenMarkersInMetres(marker.getPosition(), mCurrentLatLng)) {

                            adjustDuration = adjustDuration - (((long) (DistanceCalculator.getDistanceBetweenMarkersInMetres(marker, toPosition) / (speed / 1000.0))) - ((long) (DistanceCalculator.getDistanceBetweenMarkersInMetres(marker.getPosition(), mCurrentLatLng) / (speed / 1000.0))));

                        } else { // ถ้าระยะห่างของผีกับตำแหน่งที่จะเลื่อนไปหา มากกว่า ระยะห่างของผีกับตำแหน่งผู้ใช้ ให้ปรับ adjustDuration มีค่ามากขึ้น

                            adjustDuration = adjustDuration + (((long) (DistanceCalculator.getDistanceBetweenMarkersInMetres(marker.getPosition(), mCurrentLatLng) / (speed / 1000.0))) - ((long) (DistanceCalculator.getDistanceBetweenMarkersInMetres(marker, toPosition) / (speed / 1000.0))));
                        }

                        // ปรับตำแหน่งปัจจุบันของผู้ใช้ == ตำแหน่งที่ปีศาจจะเลื่อนไป
                        toPosition.setLatitude(mCurrentLatLng.latitude);
                        toPosition.setLongitude(mCurrentLatLng.longitude);

                        // แก้ไขตำแหน่งเริ่มต้นของ marker ปีศาจเมื่อผู้ใช้เคลื่อนที่ ทำการปรับเวลา elapse เป็น 0 (เริ่มต้นใหม่) และปรับ adjustDuration ให้ลดลง
                        newStartLatLng = marker.getPosition();
                        adjustStartTime = run_time * 16;
                        adjustDuration = adjustDuration - elapsed;
                        elapsed = 0;
//
//                    Log.d("adjustDuration",""+adjustDuration);
//                    Log.d("elapse",""+elapsed);
                    }


                    // แสดงเวลาที่ผีต้องเลื่อนไปหาผู้ใช้
//                mGhost4Status.setText("time left : " + (adjustDuration - elapsed));

                    // คำนวณค่า t ที่ใช้ในการเลื่อนตำแหน่งของผีโดยคำนวณจาก elapsed และ adjustDuration และปรับ tranparency ของผี
                    float t = interpolator.getInterpolation((float) elapsed
                            / adjustDuration);
//                Log.d("t",""+t);
                    marker.setPosition(spherical.interpolate(t, newStartLatLng, new LatLng(toPosition.getLatitude(), toPosition.getLongitude())));
                    Point monsterPoint = mMap.getProjection().toScreenLocation(marker.getPosition());
                    Point userPoint = mMap.getProjection().toScreenLocation(myArrow.getPosition());
//                Log.d("id",""+monster.getId());
                    monster.setPoint(new Point(monsterPoint.x - userPoint.x, userPoint.y - monsterPoint.y));
                    monster.setLatLng(marker.getPosition());

//                Singleton.getInstance().setAllMonsters(allMonsters);
//                marker.setSnippet("x:" + (ghostPoint.x - userPoint.x) + ", y: " + (userPoint.y - ghostPoint.y));
//                marker.showInfoWindow();
//                marker.setAlpha(t);
                    int distanceBetweenMonsterAndPlayer = (int) DistanceCalculator.getDistanceBetweenMarkersInMetres(toPosition, marker.getPosition());

                    if (distanceBetweenMonsterAndPlayer < 50 && !isVibrate) {
                        Vibrator v = (Vibrator) MapsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v.vibrate(500);
                        isVibrate = true;
                    }

                    //ถ้า ปีศาจยังมาไม่ใกล้ผู้เล่นมากกว่า xxx เมตร ก็ให้วิ่งต่อ
                    if (distanceBetweenMonsterAndPlayer > 10) {
                        handler.postDelayed(this, 16);
                        run_time++;
                        if (((KingKong) monster).isRaged()) {
                            ((KingKong) monster).setIcon(R.drawable.monster_ic);
                            marker.setIcon(monster.getIcon());
                        }

                    } else { // เลื่อนจนถึงผู้เล่นแล้ว (โจมตีได้)

                    /* ชุดโค้ดที่ทำการลบ marker ออกจาก maps
                    if (!listMarkerMonster.isEmpty() && !listMonsterName.isEmpty()) {

                            listMarkerMonster.remove(0);
                            listMonsterName.remove(marker.getTitle());
                            allMonsters.remove(monster);
                            updateMonsterId();
                            allRunnableMonster.remove(this);

                            if (hideMarker) {
                                marker.setVisible(false);
                            } else {
                                marker.setVisible(true);
                            }

                    }
                    */

                        if (!((KingKong) monster).isRaged()) {
                            ((KingKong) monster).setIcon(R.drawable.monster_rage_ic);
                            marker.setIcon(monster.getIcon());
                        }
                        Log.d("Attack!", "Monster No." + allMonsters.indexOf(monster));
                        if (Me.myHP >= monster.getAttackPower())
                            Me.myHP -= monster.getAttackPower();


                        if (Me.myHP <= 0) {

                            int[] duration = calculateGameDuration();
                            endGameDialog.setMessage("Total duration : " + duration[0] + " : " + duration[1] + " : " + duration[2]
                                    + "\n Average speed : " + Me.averageSpeed + " km/hr."
                                    + "\n Burn Calories : " + Me.averageSpeed * Me.averageSpeed * Me.weight);

                            endGameDialog.show();
                            unRegisterAllListener();

                        } else {
                            if (Me.myHP > 50) {
                                playerStatus.setProgressColor(getResources().getColor(R.color.hp_good));
                                playerStatus.setHeaderColor(getResources().getColor(R.color.hp_good_dark));
                            } else if (Me.myHP > 30 && playerStatus.getProgressColor() == getResources().getColor(R.color.hp_good)) {
                                playerStatus.setProgressColor(getResources().getColor(R.color.hp_fair));
                                playerStatus.setHeaderColor(getResources().getColor(R.color.hp_fair_dark));
                            } else if (Me.myHP <= 20 && playerStatus.getProgressColor() == getResources().getColor(R.color.hp_fair)) {
                                playerStatus.setProgressColor(getResources().getColor(R.color.hp_poor));
                                playerStatus.setHeaderColor(getResources().getColor(R.color.hp_poor_dark));
                            }
                            playerStatus.setProgress(Me.myHP);
//                    Log.d("HpProgress",""+playerStatus.getProgress());
                            handler.postDelayed(this, 3000); // หลังจากโจมตีใส่ผู้เล่นแล้ว จะต้องรอ 3 วินาที
                        }
                    }
                }

            }
        };
        /*
             สิ่งที่ต้องเก็บ adjustDuration,adjustStartTime,toPosition
         */

        allRunnableMonster.add(runnable);

        handler.post(runnable);
    }

    public void keepGeneratingGhost() {
        genGhostHandler = new Handler();
        keepGenerateGhost = new Runnable() {
            @Override
            public void run() {

                if (listMarkerMonster.size() < MAX_GHOST_AT_ONCE) {
                    int range = max_generate_ghost_timeout - min_generate_ghost_timeout + 1;
                    timeout = (int) ((Math.random() * range) + min_generate_ghost_timeout);
                    timeout = timeout * 1000; // convert to millisec
                    mMonster = new KingKong(MapsActivity.this);
                    mMonster.setSpeed(5);
                    addMonster(mMonster);
                } else {
                    timeout = 1000;
                }
                genGhostHandler.postDelayed(this, timeout);
            }
        };
        genGhostHandler.postDelayed(keepGenerateGhost, 30000);
    }

    public void keepGeneratingItem() {
        genItemHandler = new Handler();
        int range = max_generate_item_timeout - min_generate_item_timeout + 1;
        timeout = (int) ((Math.random() * range) + min_generate_item_timeout);
        timeout = timeout * 1000; // convert to millisec
        keepGenerateItem = new Runnable() {
            @Override
            public void run() {

                if (listMarkerItems.size() < MAX_ITEM_AT_ONCE) {
                    int range = max_generate_item_timeout - min_generate_item_timeout + 1;
                    timeout = (int) ((Math.random() * range) + min_generate_item_timeout);
                    timeout = timeout * 1000; // convert to millisec
                    int random_item = (int) (Math.random() * (ALL_SELF_ITEM.size() + ALL_MONSTER_ITEM.size()));
                    if (random_item < ALL_SELF_ITEM.size()) {
                        switch (ALL_SELF_ITEM.get(random_item)) {
                            case "Distancex2":
                                generatedItem = new ItemDistancex2(MapsActivity.this);
                                break;
                            case "Distancex3":
                                generatedItem = new ItemDistancex3(MapsActivity.this);
                                break;
                            case "Potion":
                                generatedItem = new Potion(MapsActivity.this);
                                break;
                        }
                    } else {
                        switch (ALL_MONSTER_ITEM.get(random_item - ALL_SELF_ITEM.size())) {
                            case "Pistol":
                                generatedItem = new Pistol(MapsActivity.this, 20);
                                break;
                            case "Desert":
                                generatedItem = new Desert(MapsActivity.this, 20);
                                break;
                        }
                    }
                    addItem(generatedItem);
                } else {
                    timeout = 30000;
                }
                genItemHandler.postDelayed(this, timeout);
            }
        };
        genItemHandler.postDelayed(keepGenerateItem, timeout);

    }

    // เลื่อนตำแหน่งของกล้อง
    public void setCameraPosition(LatLng Location, int zoomLevel, int tilt) {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(Location)
                .zoom(zoomLevel)
                .tilt(tilt)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                if (!isGameStart) {
                    locationrequest = LocationRequest.create();
                    locationrequest.setInterval(1000);
                    locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationrequest, MapsActivity.this);
                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    // เลื่อนตำแหน่งของกล้องโดยตั้งค่า bearing ด้วย
    public void setCameraPosition(LatLng Location, int zoomLevel, int tilt, int bearing) {

        CameraPosition camPos = new CameraPosition.Builder()
                .target(Location)
                .zoom(zoomLevel)
                .tilt(tilt)
                .bearing(bearing)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    // สุ่ม marker monster
    public MarkerOptions getRandomMarker(LatLngBounds bound, Monster monster) {
        double latMin = bound.southwest.latitude;
        double latRange = bound.northeast.latitude - latMin;
        double lonMin = bound.southwest.longitude;
        double lonRange = bound.northeast.longitude - lonMin;

        LatLng ghostLatLng = new LatLng(latMin + (Math.random() * latRange), lonMin + (Math.random() * lonRange));
        MarkerOptions ghostMarkerPosition = new MarkerOptions().position(ghostLatLng).icon(monster.getIcon());
        monster.setLatLng(ghostLatLng);
        return ghostMarkerPosition;
    }

    // สุ่ม marker item
    public MarkerOptions getRandomMarker(LatLngBounds bound, Item item) {
        double latMin = bound.southwest.latitude;
        double latRange = bound.northeast.latitude - latMin;
        double lonMin = bound.southwest.longitude;
        double lonRange = bound.northeast.longitude - lonMin;

        LatLng itemLatLng = new LatLng(latMin + (Math.random() * latRange), lonMin + (Math.random() * lonRange));
        MarkerOptions itemMarkerPosition = new MarkerOptions().position(itemLatLng).icon(item.getMarkerIcon());
        item.setLatLng(itemLatLng);
        return itemMarkerPosition;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        locationClient.removeLocationUpdates(MapsActivity.this);
//                        MapsActivity.this.finish();
//                    }
//                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                dialog.setTitle("Stop playing?");
//                dialog.setMessage("Your current progress won't saved");
//                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // เพิ่มปีศาจ
    private void addMonster(final Monster ghost) {

        //กำหนดชื่อให้ปีศาจแต่ละตัว
        String name = "Ghost";
        for (int i = 1; i <= 5; i++) {
            if (!listMonsterName.contains(name + i)) {
                ghost.setType(name + i);
                listMonsterName.add(name + i);
                break;
            }
        }

        final Marker mGhost = mMap.addMarker(getRandomMarker(playground, mMonster).title(ghost.getType()));
        Location location = new Location("Start");
        location.setLatitude(mCurrentLatLng.latitude);
        location.setLongitude(mCurrentLatLng.longitude);
        location.setTime(new Date().getTime());
        allMonsters.add(mMonster);
        updateMonsterId();

        Log.d("Monster Count", "" + allMonsters.size());
        for (int i = 0; i < allMonsters.size(); i++) {
            Log.d("id", allMonsters.get(i).getId() + "");
        }
        animateMarker(allMonsters.get(allMonsters.size() - 1), mGhost, location, true, ghost.getSpeed());
        listMarkerMonster.add(mGhost);
    }

    private void addItem(Item item) {
        allItems.add(item);
        Marker mItem = mMap.addMarker(getRandomMarker(playground, item).title(item.getType()));
        listMarkerItems.add(mItem);
    }

    public void unRegisterAllListener() {
        sensorManager.unregisterListener(this, accelerometerSensor);
        sensorManager.unregisterListener(this, magneticFieldSensor);
        for (Runnable r : allRunnableMonster) {
            handler.removeCallbacks(r);
        }
        if (keepGenerateGhost != null)
            genGhostHandler.removeCallbacks(keepGenerateGhost);
        if (keepGenerateItem != null)
            genItemHandler.removeCallbacks(keepGenerateItem);
        if (locationManager != null)
            locationManager.removeGpsStatusListener(checkLocation);
        if (isGameStart)
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    public void registerAllListener() {

            if (sensorManager != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            if (locationManager != null) {
                locationManager.addGpsStatusListener(checkLocation);
            }

            if (Singleton.getAllMonsters() != null) {
                allMonsters = Singleton.getAllMonsters();
            }

            for (Runnable r : allRunnableMonster) {
                handler.post(r);
            }
            Log.d("Timeout", timeout + "");
            if (keepGenerateGhost != null) {
                genGhostHandler.postDelayed(keepGenerateGhost, timeout);
            }

            if (keepGenerateItem != null)
                genItemHandler.postDelayed(keepGenerateItem, 1000);

            if (mBagAdapter != null) {
                mBagAdapter.notifyDataSetChanged();
            }

            if (isGameStart && locationrequest != null)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationrequest, MapsActivity.this);

        if(isGameStart) {
            if (Me.myHP > 50) {
                playerStatus.setProgressColor(getResources().getColor(R.color.hp_good));
                playerStatus.setHeaderColor(getResources().getColor(R.color.hp_good_dark));
            } else if (Me.myHP > 20 && playerStatus.getProgressColor() == getResources().getColor(R.color.hp_good)) {
                playerStatus.setProgressColor(getResources().getColor(R.color.hp_fair));
                playerStatus.setHeaderColor(getResources().getColor(R.color.hp_fair_dark));
            } else if (Me.myHP <= 10 && playerStatus.getProgressColor() == getResources().getColor(R.color.hp_fair)) {
                playerStatus.setProgressColor(getResources().getColor(R.color.hp_poor));
                playerStatus.setHeaderColor(getResources().getColor(R.color.hp_poor_dark));
            }
            playerStatus.setProgress(Me.myHP);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterAllListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerAllListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (allRunnableMonster.size() > 0 && keepGenerateGhost != null) {
            for (Runnable r : allRunnableMonster) {
                handler.removeCallbacks(r);
            }
            genGhostHandler.removeCallbacks(keepGenerateGhost);
        }
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        if (locationManager != null) {
            locationManager.removeGpsStatusListener(checkLocation);
        }


        Me.guns = new ArrayList<Gun>();
        Me.items = new ArrayList<Item>();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (myArrow != null) {
            int sensorType = event.sensor.getType();
            if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                accelerometerData = event.values;
            } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticData = event.values;
            }

            if (accelerometerData != null && magneticData != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, accelerometerData,
                        magneticData);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    azimut = (int) Math.round(Math.toDegrees(orientation[0]));
//                    Log.d("orientation",orientation[1]+"");
                    if (Math.abs(azimut - oldAzimuth) >= THRESHOLD_ROT_ARROW && isGameStart && orientation[1] > -1.25 && orientation[1] < 1.25) {
//                        Log.d("azimuth", "" + azimut);
//                        myArrow.setRotation(azimut);
//                        setCameraPosition(mCurrentLatLng,18,0,azimut);
                        CameraPosition camPos = new CameraPosition.Builder()
                                .target(mCurrentLatLng)
                                .zoom(18)
                                .tilt(0)
                                .bearing(azimut)
                                .build();

                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));

                        oldAzimuth = azimut;
                    }

                    // ถ้าวิ่งจนได้ระยะทางเกินค่าที่กำหนดไว้ให้อัพเดตหมุนกล้องให้ตรงกับทิศที่วิ่ง
//                    if (countDistanceToRotCam >= THRESHOLD_ROT_CAM) {
////                        setCameraPosition(mCurrentLatLng, 18, 0, azimut);
//                        currentBearing = azimut;
////                        myArrow.setPosition(mCurrentLatLng);
////                        myArrow.setRotation(azimut);
//                        countDistanceToRotCam = 0;
//                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MapsActivity.super.onBackPressed();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setTitle("Stop playing?");
        dialog.setMessage("Your current progress won't saved");
        dialog.show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map. instance
        if (mMap == null) {
            mMap = (((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map)).getMap());
            // Check if we were successful in obtaining the map.
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        // แสดงความเร็วและความแม่นยำ
//        mGhost1Status.setText("v : " + location.getSpeed());
//        mGhost2Status.setText("Acc : " + location.getAccuracy() + " m.");
//        Log.d("Accuracy Grade", getGrade((int) location.getAccuracy()));
//        toolbar.setSubtitle();
        tvItemCount.setText("Accuracy : " + checkLocation.getGrade((int) location.getAccuracy()));
//        mCvVelocityStatus.setTitleText(String.format("%.2f", location.getSpeed() * 3.6));


        if (progress.isShowing() && builder == null) {
            builder = new AlertDialog.Builder(MapsActivity.this).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override

                public void onClick(DialogInterface dialog, int which) {

                    //เมื่อทำการคลิก "yes" ให้กำหนดขอบเขตการเล่นและเพิ่ม Ghost มาวิ่งไล่ผู้เล่น
                    playground = mMap.getProjection().getVisibleRegion().latLngBounds;
                    keepGeneratingGhost();
                    keepGeneratingItem();
                    isGameStart = true;
                    startGameTime = Calendar.getInstance();

                    // เมื่อเริ่มเกมให้เล่นอนิเมชั่น
                    startGameViewAnimation();

                }
            });

            // ให้ ProgressDialog หายไปและแสดง AlertDialog แทน
            progress.dismiss();
            builder.setMessage("Are you ready?");
            builder.setTitle("Mission 1 start");
            builder.setCancelable(false);
            builder.show();
        }

        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (isGameStart) {
            checkLocation.setLocationTime(location.getTime());
            // อัพเดตระยะทางที่ต้องวิ่ง
            double distance = DistanceCalculator.getDistanceBetweenMarkersInMetres(mCurrentLatLng, mPreviousLatLng);
            countDistanceToRotCam += distance;

            if (location.getSpeed() > Me.highestSpeed)
                Me.highestSpeed = location.getSpeed();

            distanceGoal -= distance * Me.distanceMultiplier;
            mCvDistanceStatus.setTitleText((int) distanceGoal + " m");

            // เลื่อนตำแหน่งของลูกษรใหม่
            myArrow.setPosition(mCurrentLatLng);

            // กำหนดตำแหน่งของกล้องใหม่
            setCameraPosition(mCurrentLatLng, 18, 0, azimut);

            mPreviousLatLng = mCurrentLatLng;

            // ถ้าจบเกม
            if (distanceGoal <= 0) {
                distanceGoal = 0;
                unRegisterAllListener();
                int[] duration = calculateGameDuration();
                endGameDialog.setMessage("Total duration : " + duration[0] + " : " + duration[1] + " : " + duration[2]
                        + "\n Average speed : " + Me.averageSpeed + " km/hr."
                        + "\n Burn Calories : " + Me.averageSpeed * Me.averageSpeed * Me.weight);
                endGameDialog.show();

            }

        }
    }

    public void passAllMonster(boolean isChecked,ToggleButton toggleButton) {

        if(isChecked){
            uncheckAllChildrenCascade(gView);
            toggleButton.setChecked(true);
            optionBar.setVisibility(View.VISIBLE);

        }else{
            optionBar.setVisibility(View.GONE);

        }
    }

    private void uncheckAllChildrenCascade(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            ViewGroup v = (ViewGroup)vg.getChildAt(i);
            View vv = v.getChildAt(0);
            if (vv instanceof ToggleButton) {
                ((ToggleButton) vv).setChecked(false);
            } else if (vv instanceof ViewGroup) {
                uncheckAllChildrenCascade((ViewGroup) v);
            }
        }
    }

    private Item getItemFromPosition(int position) {
        Item selected;
        if (Me.chosenGun < Me.guns.size()) {
            selected = Me.guns.get(position);
        } else {
            selected = Me.items.get(position - Me.guns.size());
        }

        return selected;
    }

    private void showDetailItemDialog() {
        Item selected = getItemFromPosition(Me.chosenGun);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.detail_item_dialog);
        dialog.setCancelable(true);

        ImageView image = (ImageView) dialog.findViewById(R.id.detail_img);
        image.setImageResource(selected.getThumb());

        ImageView exit = (ImageView) dialog.findViewById(R.id.detail_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        TextView name = (TextView) dialog.findViewById(R.id.detail_name);
        name.setText(selected.getName());

        TextView desciption = (TextView) dialog.findViewById(R.id.detail_text);
        desciption.setText(selected.getDescription());


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();

        dialog.getWindow().setAttributes(lp);
    }

    public void startGameViewAnimation() {
        cbHome.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.Landing)
                .duration(700)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCvDistanceStatus.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.Landing)
                                .duration(700)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        playerStatus.setVisibility(View.VISIBLE);
                                        YoYo.with(Techniques.Landing)
                                                .duration(800)
                                                .withListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        playerStatus.setAlpha(0.7f);
                                                        mBag.setVisibility(View.VISIBLE);
                                                        YoYo.with(Techniques.SlideInLeft)
                                                                .duration(500)
                                                                .withListener(new Animator.AnimatorListener() {
                                                                    @Override
                                                                    public void onAnimationStart(Animator animation) {

                                                                    }

                                                                    @Override
                                                                    public void onAnimationEnd(Animator animation) {
                                                                        YoYo.with(Techniques.RubberBand)
                                                                                .duration(400)
                                                                                .withListener(new Animator.AnimatorListener() {
                                                                                    @Override
                                                                                    public void onAnimationStart(Animator animation) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onAnimationEnd(Animator animation) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onAnimationCancel(Animator animation) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onAnimationRepeat(Animator animation) {

                                                                                    }
                                                                                })
                                                                                .playOn(mBag);
                                                                    }

                                                                    @Override
                                                                    public void onAnimationCancel(Animator animation) {

                                                                    }

                                                                    @Override
                                                                    public void onAnimationRepeat(Animator animation) {

                                                                    }
                                                                })
                                                                .playOn(mBag);
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animator animation) {

                                                    }
                                                })
                                                .playOn(playerStatus);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }).playOn(mCvDistanceStatus);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(cbHome);
    }

    public void setItemAnimation(Item item) {
        itemUse = item;
        itemStatus.setVisibility(View.INVISIBLE);
        if (runnableItemStatus != null) {
            handlerItemStatus.removeCallbacks(runnableItemStatus);
            Log.d("remove", "!");
        }
        itemBagLayout.collapsePanel();
    }

    public int[] calculateGameDuration() {
        if (endGameTime == null) {
            endGameTime = Calendar.getInstance();
        }
        int endTotalSec = endGameTime.get(Calendar.HOUR) * 3600 + endGameTime.get(Calendar.MINUTE) * 60 + endGameTime.get(Calendar.SECOND);
        int startTotalSec = startGameTime.get(Calendar.HOUR) * 3600 + startGameTime.get(Calendar.MINUTE) * 60 + startGameTime.get(Calendar.SECOND);
        int diff = endTotalSec - startTotalSec;
        Me.totalDuration = endTotalSec - startTotalSec;
        Me.averageSpeed = maxDistance * 3.6 / Me.totalDuration;
        return new int[]{diff / 3600, diff / 60, diff % 60};
    }

    public void passAllMonster() {
        Intent i = new Intent(this, MainActivity.class);
        Singleton.getInstance().setAllMonsters(allMonsters);
        startActivity(i);
    }

    public void updateMonsterId() {
        for (int i = 0; i < allMonsters.size(); i++) {
            allMonsters.get(i).setId(i);
            Log.d("updateMonsterId1st", "" + allMonsters.get(i).getId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        AlertDialog.Builder setting_mobile_data = new AlertDialog.Builder(this)
                .setTitle("Mobile data or Wifi is not enabled yet")
                .setMessage("Go to setting again")
                .setCancelable(false)
                .setNegativeButton("Exit game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsActivity.this.finish();
                    }
                })
                .setNeutralButton("Wifi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), DATA_ENABLED_REQ);
                    }
                })
                .setPositiveButton("Mobile data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS), DATA_ENABLED_REQ);
                    }
                });

        AlertDialog.Builder setting_location = new AlertDialog.Builder(this)
                .setTitle("Set location mode to high accuracy")
                .setMessage("Go to setting again")
                .setCancelable(false)
                .setNegativeButton("Exit game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapsActivity.this.finish();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_ENABLED_REQ);
                    }
                });

        // if recieve from data_enabled request
        if (requestCode == DATA_ENABLED_REQ) {

            if (!connectivity.is3gConnected() && !connectivity.isWifiConnected()) {
                // Mobile data isn't enable yet.
                Log.d("Mobile data status : ", "disabled");
                setting_mobile_data.show();
            } else if (!checkLocation.isLocationEnabled()) {
                setting_location.show();
            } else {
                setUpMapIfNeeded();
                initVar();
                initListener();
            }
        } else if (requestCode == LOCATION_ENABLED_REQ) {
            if (!checkLocation.isLocationEnabled()) {
                //Location is not enable yet.

                Log.d("Location status : ", "disabled");
                setting_location.show();
            } else if (!connectivity.is3gConnected() && !connectivity.isWifiConnected()) {
                setting_mobile_data.show();
            } else {
                setUpMapIfNeeded();
                initVar();
                initListener();
            }
        }
    }
}

