package cz.easytv.billboard.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import cz.easytv.billboard.MainApp;

public class HdmiListener extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals("android.intent.action.HDMI_PLUGGED")) {
            boolean state = intent.getBooleanExtra("state", false);

            if (state == true) {
                MainApp.onHDMIConnected(context, intent);
            } else {
                MainApp.onHDMIDisconnected(context, intent);
            }
        }
    }
}
