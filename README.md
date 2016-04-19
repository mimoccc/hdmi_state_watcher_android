# hdmi_state_watcher_android

usage : 

receiver:

```java
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
        } else if (action.equals(ACTION_HDMISTATUS_CHANGED)) {
            onHdmiPlugChanged(context, intent);
        }
    }
}

manifest:

<service
	android:name=".service.hdmi.HdmiWatcher"
	android:process=":remote"
	android:enabled="true"
	android:exported="true"
	android:stopWithTask="false" />
    
<receiver android:name=".receiver.HdmiListener">
	<intent-filter>
	  <action android:name="android.intent.action.HDMI_PLUGGED" />
	</intent-filter>
</receiver>
