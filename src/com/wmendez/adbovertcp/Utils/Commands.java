package com.wmendez.adbovertcp.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Commands {
    public static String execSingleCommand(String command) {
        try {
            // Executes the command.
            Process process = Runtime.getRuntime().exec(command);

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            // process.getOutputStream().
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean execCommandsAsSU(String... commands) {
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
