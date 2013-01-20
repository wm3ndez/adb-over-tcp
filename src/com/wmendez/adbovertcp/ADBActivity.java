package com.wmendez.adbovertcp;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.DataOutputStream;
import java.io.IOException;

public class ADBActivity extends Activity {

    Toast toast;
    int duration = Toast.LENGTH_LONG;
    String[] enableADB = {"setprop service.adb.tcp.port 5555",
            "stop adbd",
            "start adbd"};
    private String[] disableADB = new String[]{"setprop service.adb.tcp.port -1",
            "stop adbd",
            "start adbd"};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ToggleButton button = (ToggleButton) findViewById(R.id.toggle_button);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        button.setOnCheckedChangeListener(changeListener);
    }

    private CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (execCommands(enableADB))
                    toast = Toast.makeText(getApplicationContext(), "Enabled ADB over TCP", duration);
                else
                    toast = Toast.makeText(getApplicationContext(), "Could not enable ADB over TCP", duration);
            } else {
                if (execCommands(disableADB))
                    toast = Toast.makeText(getApplicationContext(), "Disabled ADB over TCP", duration);
                else
                    toast = Toast.makeText(getApplicationContext(), "Could not disable ADB over TCP", duration);
            }
            toast.show();
        }
    };

    public Boolean execCommands(String... commands) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            for (String command : commands) {
                os.writeBytes(command + "\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }
}
