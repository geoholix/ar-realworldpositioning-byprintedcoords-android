package com.esri.apl.ar_world_positioning.util;

import android.support.annotation.Nullable;
import android.util.Log;

import com.esri.arcgisruntime.mapping.view.DeviceMotionDataSource;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;

public class ARCoreSource extends DeviceMotionDataSource {
  private static final String TAG = "ARCoreSource";

  private static final Quaternion ROTATION_COMPENSATION =
          new Quaternion((float)Math.sin(45 / (180 / Math.PI)), 0, 0, (float)Math.cos(45 / (180 / Math.PI)));

  private Scene mArScene;
  private Session mArSession;
  private ARCoreSceneUpdateCallable mSceneUpdateCallback;

  /**
   *
   * @param arScene Android ArCore Scene.
   * @param arSession Android ArCore Session.
   * @param camStart Initial geographic position of the viewer.
   * @param sceneUpdateCallback Callback object for frame-update messages and errors.
   */
  public ARCoreSource(
          Scene arScene,
          Session arSession,
          @Nullable com.esri.arcgisruntime.mapping.view.Camera camStart,
          @Nullable ARCoreSceneUpdateCallable sceneUpdateCallback) {
    this.mArScene = arScene;
    this.mArSession = arSession;
    if (camStart != null) moveInitialPosition(
        camStart.getLocation().getX(),
        camStart.getLocation().getY(),
        camStart.getLocation().getZ());

    this.mSceneUpdateCallback = sceneUpdateCallback;
  }

  @Override
  public void startAll() {
    if (mArSession != null) {
      try {
        // Compensate for user holding device upright, rather than flat on a horizontal surface.
        // Taken from Xamarin iOS ARKit motion data source code.
        mArSession.resume();
        mArScene.setOnUpdateListener(new Scene.OnUpdateListener() {
          @Override
          public void onUpdate(FrameTime frameTime) {
            Frame frame;
            try {
              frame = mArSession.update();
              if (frame == null) return;
              if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                Log.d(TAG, "Not tracking frame");
                return;
              }

              Pose pose = frame.getCamera().getPose();
              float[] aryRot = pose.getRotationQuaternion();
              Quaternion qRotRaw = new Quaternion(aryRot[0], aryRot[1], aryRot[2], aryRot[3]);
              // Order of multiplier and multiplicand is very important here!
              Quaternion qRot = Quaternion.multiply(ROTATION_COMPENSATION, qRotRaw);
              setRelativePosition(pose.tx(), -pose.tz(), pose.ty(),
                      qRot.x, qRot.y, qRot.z, qRot.w, true
              );

              // Allow the caller to do something with the updated scene if it wants to
              if (mSceneUpdateCallback != null)
                mSceneUpdateCallback.onSceneUpdate(mArScene, mArSession, frame, frameTime);
            } catch (CameraNotAvailableException e) {
              mSceneUpdateCallback.onSceneError(e);
            }
          }
        });
      } catch (CameraNotAvailableException e) {
        ARUtils.displayError(mArScene.getView().getContext(), "Could not get the camera on resuming AR Session", e);
      }
    }
  }

  @Override
  public void stopAll() {
    if (mArSession != null) mArSession.pause();
  }
}
