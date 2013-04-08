package com.wmendez.adbovertcp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import static com.wmendez.adbovertcp.Utils.Commands.execCommandsAsSU;
import static com.wmendez.adbovertcp.Utils.Commands.execSingleCommand;

public class ADBActivity extends Activity {

    Toast toast;
    int duration = Toast.LENGTH_LONG;
    String[] enableADB = {"setprop service.adb.tcp.port 5555", "stop adbd", "start adbd"};
    private String[] disableADB = new String[]{"setprop service.adb.tcp.port -1", "stop adbd", "start adbd"};
    private Switch button;
    private AlarmManager alarmManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Switch) findViewById(R.id.toggle_button);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        button.setOnCheckedChangeListener(changeListener);
        setButtonStatus();
    }

    private void setButtonStatus() {
        button.setChecked(getADBStatus());
    }

    private boolean getADBStatus() {
        String prop = null;
        try {
            prop = execSingleCommand("getprop service.adb.tcp.port").replace("\n", "");
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Could not get adb.tcp.port state", duration);
        }
        return prop != null && Integer.parseInt(prop) != -1;
    }

    private CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked == getADBStatus())
                return;
            if (isChecked) {
                if (execCommandsAsSU(enableADB))
                    toast = Toast.makeText(getApplicationContext(), "Enabled ADB over TCP", duration);
                else
                    toast = Toast.makeText(getApplicationContext(), "Could not enable ADB over TCP", duration);
            } else {
                if (execCommandsAsSU(disableADB))
                    toast = Toast.makeText(getApplicationContext(), "Disabled ADB over TCP", duration);
                else
                    toast = Toast.makeText(getApplicationContext(), "Could not disable ADB over TCP", duration);
            }
            toast.show();
        }
    };

    private void setTimedADB() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10000, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(), 0));
    }


}
