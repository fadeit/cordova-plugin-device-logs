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

    private static final String TAG = "SyslogPlugin";

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
                    Log.i(TAG, "Requesting syslog...");
                    ProcessBuilder builder = new ProcessBuilder(new String[] { "su", "-c", "logcat", "-d" });
                    builder.redirectErrorStream(true);
                    Process proc = builder.start();
                    InputStream stdout = proc.getInputStream();
                    BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
                    String line;
                    JSONArray logLines = new JSONArray();
                    while ((line = reader.readLine()) != null) {
                        logLines.put(line);
                    }
                    Log.i(TAG, "Got " + logLines.length() + " syslog entries");
                    callbackContext.success(logLines);
                } catch (Exception ex) {
                    Log.e(TAG, "Syslog failed", ex);
                    callbackContext.error("Syslog failed");
                }
            }
        });
    }
}
