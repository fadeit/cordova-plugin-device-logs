package dk.fadeit.syslog;

import android.util.Log;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

public class Syslog extends CordovaPlugin {

    private static final String TAG = "RebootPlugin";

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("syslog".equals(action)) {
            this.syslog(callbackContext);
            return true;
        }
        return false;
    }

    private void syslog(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Log.i(TAG, "Syslog start");
                    ProcessBuilder builder = new ProcessBuilder(new String[] { "su", "-c", "logcat", "-d" });
                    builder.redirectErrorStream(true);
                    Process proc = builder.start();
                    InputStream stdout = proc.getInputStream();
                    BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
                    int logCount = 0;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logCount++;
                    }
                    Log.i(TAG, "Syslog success");
                    Log.i(TAG, "Loglines: " + logCount);
                    callbackContext.success("Syslog success");
                } catch (Exception ex) {
                    Log.e(TAG, "Syslog failed", ex);
                    callbackContext.error("Syslog failed");
                }
            }
        });
    }
}
