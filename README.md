# hdmi_state_watcher_android

note:
there is no onDestroy & release FileObserver as it is primary developed for launcher app

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
