package com.wmendez.adbovertcp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static com.wmendez.adbovertcp.Utils.Commands.execCommandsAsSU;
import static com.wmendez.adbovertcp.Utils.Commands.execSingleCommand;

public class ADBActivity extends Activity {

    Toast toast;
    int duration = Toast.LENGTH_LONG;
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
        setElements();

    }

    private void setElements() {
        setButtonStatus();
        setIpAddress();
    }

    private void setIpAddress() {
        String ip;
        try {
            ip = execSingleCommand(getString(R.string.get_ipaddress_command)).replace("\n", "");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        TextView tv = (TextView) findViewById(R.id.ip_address);
        tv.setText(ip);
    }

    private void setButtonStatus() {
        button.setChecked(getADBStatus());
    }

    private boolean getADBStatus() {
        String prop = null;
        try {
            prop = execSingleCommand(getString(R.string.get_tcp_port_command)).replace("\n", "");
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Could not get adb.tcp.port state", duration);
        }
        return prop != null && !prop.equals("") && Integer.parseInt(prop) != -1;
    }

    private CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked == getADBStatus())
                return;
            if (isChecked) {
                if (execCommandsAsSU(getResources().getStringArray(R.array.enable_tcp_commands)))
                    toast = Toast.makeText(getApplicationContext(), "Enabled ADB over TCP", duration);
                else
                    toast = Toast.makeText(getApplicationContext(), "Could not enable ADB over TCP", duration);
            } else {
                if (execCommandsAsSU(getResources().getStringArray(R.array.disable_tcp_commands)))
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
