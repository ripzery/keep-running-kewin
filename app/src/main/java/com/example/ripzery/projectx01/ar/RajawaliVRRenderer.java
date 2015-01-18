package com.example.ripzery.projectx01.ar;

import android.content.Context;
import android.util.Log;

import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import rajawali.Object3D;
import rajawali.math.Matrix4;
import rajawali.math.Quaternion;

/**
 * Created by Rawipol on 1/6/15 AD.
 */
public class RajawaliVRRenderer extends OakrawRenderer {
    protected HeadTracker mHeadTracker;
    protected HeadTransform mHeadTransform;
    protected float[] mHeadViewMatrix;
    protected Matrix4 mHeadViewMatrix4;
    protected Quaternion mCameraOrientation;
    protected Object3D arrow;


    public RajawaliVRRenderer(Context context) {
        super(context);
        mHeadTransform = new HeadTransform();
        mHeadViewMatrix = new float[16];
        mHeadViewMatrix4 = new Matrix4();
        mCameraOrientation = new Quaternion();
    }

    public void setHeadTracker(HeadTracker headTracker) {
        mHeadTracker = headTracker;

    }

    @Override
    public void onRender(double deltaTime) {
        mHeadTracker.getLastHeadView(mHeadViewMatrix, 0);
        mHeadViewMatrix4.setAll(mHeadViewMatrix);

        mCameraOrientation.fromMatrix(mHeadViewMatrix4);
        arrow.setOrientation(mCameraOrientation);

        mCameraOrientation.x *= -1;
        mCameraOrientation.y *= -1;
        mCameraOrientation.z *= -1;
        setCameraOrientation(mCameraOrientation);
        Log.d("rawipol", "VRRenderer onRender : " + mCameraOrientation.toString());


        super.onRender(deltaTime);
    }
}
