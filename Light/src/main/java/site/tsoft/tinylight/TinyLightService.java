package site.tsoft.tinylight;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * Toggle torch on/off
 * helper methods.
 */
public class TinyLightService extends IntentService {
    private static final String ACTION_TOGGLE_TORCH = "site.tsoft.tinylight.action.toggle";

    private CameraManager mCameraManager;
    private String mCameraId;
    private boolean mFlashEnabled;
    private boolean mInitialized;

    public TinyLightService() {
        super("TinyLightService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        boolean isCameraFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isCameraFlash) {
            Log.e("TinyLightService", "Camera flashlight not available in this Android device!");
        }

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        mCameraManager.registerTorchCallback(new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeUnavailable(String cameraId) {
                super.onTorchModeUnavailable(cameraId);
            }
            @Override
            public void onTorchModeChanged(String cameraId, boolean enabled) {
                mFlashEnabled = enabled;
                mInitialized = true;
            }

        }, null);// (callback, handler)
    }

    private void toggleFlashLight() {

        try {
            for(int i=0; i<10 && mInitialized == false; i++)
                Thread.sleep(100);

            if(mInitialized) {
                mFlashEnabled = !mFlashEnabled;
                mCameraManager.setTorchMode(mCameraId, mFlashEnabled);
            }
            else {
                Log.e("TinyLightService", "Failed to get Torch Mode");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionToggle(Context context) {
        Intent intent = new Intent(context, TinyLightService.class);
        intent.setAction(ACTION_TOGGLE_TORCH);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_TOGGLE_TORCH.equals(action)) {
                handleActionToggle();
            }
        }
    }

    /**
     * Handle action Toggle in the provided background thread with the provided
     * parameters.
     */
    private void handleActionToggle() {
        toggleFlashLight();
    }

}
