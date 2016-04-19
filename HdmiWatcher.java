package cz.easytv.billboard.service.hdmi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.FileObserver;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.Scanner;

import cz.easytv.billboard.R;
import cz.easytv.billboard.receiver.HdmiListener;

public class HdmiWatcher extends Service {
    private static final String TAG = HdmiWatcher.class.getSimpleName();
    private static FileObserver observer;
    private static boolean last_state;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "HDMI Watcher created");

        last_state = isHdmiSwitchSet();

        Log.d(TAG, "Setting wake lock");

        keepAwake(getBaseContext());
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        String file_path = "/sys/devices/virtual/switch/hdmi";

        if (new File(file_path).exists()) {
            observer = new FileObserver(file_path, FileObserver.ALL_EVENTS) {
                @Override
                public void onEvent(int event, String file) {
                    boolean state = isHdmiSwitchSet();

                    if (last_state != state) {
                        last_state = state;

                        Log.d(TAG, String.format("HDMI STATE CHANGED: %s", state));

                        Intent i = new Intent(HdmiListener.HDMIINTENT);
                        i.putExtra("state", last_state);
                        getBaseContext().sendBroadcast(i);
                    }
                }
            };

            observer.startWatching();
        } else {
            Log.d(TAG, "HDMI sys file does not exists.");

            stopSelf();
        }

        Log.d(TAG, "HDMI Watcher started");

        return START_STICKY_COMPATIBILITY | START_STICKY;
    }

    private static void keepAwake(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getResources().getString(R.string.app_name));
        wakeLock.acquire();

        PowerManager.WakeLock wakeLock_deamScreen = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Screen Deam or screen stays on for a little longer");
        wakeLock_deamScreen.acquire();

        PowerManager.WakeLock wakeLock_Full_wake_lock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "");
        wakeLock_Full_wake_lock.acquire();

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiManager.WifiLock lock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "LockTag");
        lock.acquire();
    }

    private boolean isHdmiSwitchSet() {
        File switchFile = new File("/sys/devices/virtual/switch/hdmi/state");

        if (!switchFile.exists()) {
            switchFile = new File("/sys/class/switch/hdmi/state");

            try {
                Scanner switchFileScanner = new Scanner(switchFile);
                int switchValue = switchFileScanner.nextInt();
                switchFileScanner.close();
                return switchValue > 0;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
