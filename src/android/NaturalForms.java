package io.happie.naturalforms;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ActivityNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class NaturalForms extends CordovaPlugin {

    public static final String TAG = "NaturalForms";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callback) throws JSONException {
        try {
            PackageManager manager = cordova.getActivity().getApplicationContext().getPackageManager();
            Intent LaunchIntent = manager.getLaunchIntentForPackage("net.expedata.naturalforms");//.ui.CsvImportActivity
            //Intent LaunchIntent = new Intent(Intent.ACTION_VIEW);
            if (LaunchIntent == null) {
                callback.error("Could not find natural forms");
                return true;
            }

            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {

                File JNDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "JobNimbus");
                if (!JNDir.mkdirs()) {
                    //callback.error("Could not create naturalForms data.");
                }
                String fileName = "/nf-data" + System.currentTimeMillis() + ".csv";
                try {
                    FileOutputStream overWrite = new FileOutputStream(JNDir.toString() + fileName, false);
                    overWrite.write(args.getString(0).getBytes());
                    overWrite.flush();
                    overWrite.close();

                    LaunchIntent.setDataAndType(Uri.parse("file://" + JNDir.toString() + fileName), "text/csv");

                    ResolveInfo best = getPackageInfo(LaunchIntent, "net.expedata.naturalforms");

                    LaunchIntent.setClassName(best.activityInfo.packageName, "net.expedata.naturalforms.ui.CsvImportActivity");

                    cordova.getActivity().startActivity(LaunchIntent);

                } catch (IOException ioe) {
                    callback.error("Could not save naturalForms data.");
                }
            } else callback.error("cannot access external storage.");

            callback.success();
        } catch (ActivityNotFoundException e) {
            callback.error("Could not start naturalForms");
        }
        return true;
    }

    private ResolveInfo getPackageInfo(final Intent intent, final String packageName) {
        final PackageManager pm = cordova.getActivity().getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);

        ResolveInfo best = null;
        for (final ResolveInfo info : matches) {
            if (info.activityInfo.packageName.contains(packageName)) {
                best = info;
            }
        }
        return best;
    }
}