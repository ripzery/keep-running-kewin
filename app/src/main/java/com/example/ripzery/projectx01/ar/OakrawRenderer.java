package com.example.ripzery.projectx01.ar;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import rajawali.Camera;
import rajawali.materials.Material;
import rajawali.materials.textures.ATexture;
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3;
import rajawali.primitives.ScreenQuad;
import rajawali.renderer.RajawaliRenderer;
import rajawali.renderer.RenderTarget;
import rajawali.scene.RajawaliScene;

/**
 * Created by Rawipol on 1/6/15 AD.
 */
public class OakrawRenderer extends RajawaliRenderer {
    /**
     * Stores the camera's orientation. This is set from the
     * activity by the rotation vector sensor.
     */
    private Quaternion mCameraOrientation = new Quaternion();
    /**
     * Scratch quaternion
     */
    private Quaternion mScratchQuaternion1 = new Quaternion();
    /**
     * Scratch quaternion
     */
    private Quaternion mScratchQuaternion2 = new Quaternion();
    /**
     * Camera orientation lock. Used to chaneg the camera's
     * orientation in a thread-safe manner.
     */
    private final Object mCameraOrientationLock = new Object();
    /**
     * The camera for the left eye
     */
    private Camera mCameraLeft;
    /**
     * The camera for the right eye
     */
    private Camera mCameraRight;
    /**
     * Half the width of the viewport. The screen will be split in two.
     * One half for the left eye and one half for the right eye.
     */
    private int mViewportWidthHalf;
    /**
     * The texture that will be used to render the scene into from the
     * perspective of the left eye.
     */
    private RenderTarget mLeftRenderTarget;
    /**
     * The texture that will be used to render the scene into from the
     * perspective of the right eye.
     */
    private RenderTarget mRightRenderTarget;
    /**
     * Used to store a reference to the user's scene.
     */
    private RajawaliScene mUserScene;
    /**
     * The side by side scene is what will finally be shown onto the screen.
     * This scene contains two quads. The left quad is the scene as viewed
     * from the left eye. The right quad is the scene as viewed from the
     * right eye.
     */
    private RajawaliScene mSideBySideScene;
    /**
     * This screen quad will contain the scene as viewed from the left eye.
     */
    private ScreenQuad mLeftQuad;
    /**
     * This screen quad will contain the scene as viewed from the right eye.
     */
    private ScreenQuad mRightQuad;
    /**
     * The material for the left quad
     */
    private Material mLeftQuadMaterial;
    /**
     * The material for the right quad
     */
    private Material mRightQuadMaterial;
    /**
     * The distance between the pupils. This is used to offset the cameras.
     */
    private double mPupilDistance = .06;

    public OakrawRenderer(Context context)
    {
        super(context);
    }

    public OakrawRenderer(Context context, double pupilDistance)
    {
        this(context);
        mPupilDistance = pupilDistance;
    }

    @Override
    public void initScene() {
        mCameraLeft = new Camera();
        mCameraLeft.setNearPlane(.01f);
        mCameraLeft.setFieldOfView(getCurrentCamera().getFieldOfView());
        mCameraLeft.setNearPlane(getCurrentCamera().getNearPlane());
        mCameraLeft.setFarPlane(getCurrentCamera().getFarPlane());

    /*    mCameraRight = new Camera();
        mCameraRight.setNearPlane(.01f);
        mCameraRight.setFieldOfView(getCurrentCamera().getFieldOfView());
        mCameraRight.setNearPlane(getCurrentCamera().getNearPlane());
        mCameraRight.setFarPlane(getCurrentCamera().getFarPlane());*/

        //setPupilDistance(mPupilDistance);

        mLeftQuadMaterial = new Material();
        mLeftQuadMaterial.setColorInfluence(0);
        /*mRightQuadMaterial = new Material();
        mRightQuadMaterial.setColorInfluence(0);*/

        mSideBySideScene = new RajawaliScene(this);

        mLeftQuad = new ScreenQuad();
        //mLeftQuad.setScaleX(.5);
        //mLeftQuad.setX(-.25);
        mLeftQuad.setMaterial(mLeftQuadMaterial);
        mSideBySideScene.addChild(mLeftQuad);

        /*mRightQuad = new ScreenQuad();
        mRightQuad.setScaleX(.5);
        mRightQuad.setX(.25);
        mRightQuad.setMaterial(mRightQuadMaterial);
        mSideBySideScene.addChild(mRightQuad);*/

        addScene(mSideBySideScene);

        mViewportWidthHalf = (int) (mViewportWidth);

        mLeftRenderTarget = new RenderTarget("sbsLeftRT", mViewportWidth, mViewportHeight);
        mLeftRenderTarget.setFullscreen(true);
        //mRightRenderTarget = new RenderTarget("sbsRightRT", mViewportWidthHalf, mViewportHeight);
        //mRightRenderTarget.setFullscreen(false);

        mCameraLeft.setProjectionMatrix(mViewportWidth, mViewportHeight);
       // mCameraRight.setProjectionMatrix(mViewportWidthHalf, mViewportHeight);

        addRenderTarget(mLeftRenderTarget);
        //addRenderTarget(mRightRenderTarget);

        try {
            mLeftQuadMaterial.addTexture(mLeftRenderTarget.getTexture());
            //mRightQuadMaterial.addTexture(mRightRenderTarget.getTexture());
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onRender(final double deltaTime) {
        mUserScene = getCurrentScene();

        setRenderTarget(mLeftRenderTarget);
        getCurrentScene().switchCamera(mCameraLeft);
        GLES20.glViewport(0, 0, mViewportWidth, mViewportHeight);
        mCameraLeft.setProjectionMatrix(mViewportWidth, mViewportHeight);
        mCameraLeft.setOrientation(mCameraOrientation);

        render(deltaTime);


        /*setRenderTarget(mRightRenderTarget);

        getCurrentScene().switchCamera(mCameraRight);
        mCameraRight.setProjectionMatrix(mViewportWidthHalf, mViewportHeight);
        mCameraRight.setOrientation(mCameraOrientation);

        render(deltaTime);*/

        //switchSceneDirect(mSideBySideScene);
        //GLES20.glViewport(0, 0, mViewportWidth, mViewportHeight);

        setRenderTarget(null);

        render(deltaTime);

        switchSceneDirect(mUserScene);

        Log.d("rawipol","OakrawRenderer onRender");
    }

    public void setCameraOrientation(Quaternion cameraOrientation) {
        synchronized (mCameraOrientationLock) {
            mCameraOrientation.setAll(cameraOrientation);
        }
    }

    public void setSensorOrientation(float[] quaternion)
    {
        synchronized (mCameraOrientationLock) {
            mCameraOrientation.x = quaternion[1];
            mCameraOrientation.y = quaternion[2];
            mCameraOrientation.z = quaternion[3];
            mCameraOrientation.w = quaternion[0];

            mScratchQuaternion1.fromAngleAxis(Vector3.Axis.X, -90);
            mScratchQuaternion1.multiply(mCameraOrientation);

            mScratchQuaternion2.fromAngleAxis(Vector3.Axis.Z, -90);
            mScratchQuaternion1.multiply(mScratchQuaternion2);

            mCameraOrientation.setAll(mScratchQuaternion1);

        }
    }

    public void setPupilDistance(double pupilDistance)
    {
        mPupilDistance = pupilDistance;
        if (mCameraLeft != null)
            mCameraLeft.setX(pupilDistance * -.5);
        if (mCameraLeft != null)
            mCameraRight.setX(pupilDistance * .5);
    }

    public double getPupilDistance()
    {
        return mPupilDistance;
    }
}

