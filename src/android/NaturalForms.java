package io.happie.naturalforms;

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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class NaturalForms extends CordovaPlugin {

    public static final String TAG = "NaturalForms";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callback) throws JSONException {
        try {
            //PackageManager manager = cordova.getActivity().getApplicationContext().getPackageManager();
            //LaunchIntent = manager.getLaunchIntentForPackage(params.getString("net.expedata.naturalforms"));

            //LaunchIntent = new Intent("net.expedata.naturalforms.ui.CsvImportActivity");

            Intent LaunchIntent = new Intent(Intent.ACTION_VIEW);

            if (LaunchIntent == null) {
                callback.error("Could not find natural forms");
                return true;
            }

            File csv = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/JobNimbus/", "data.csv");
            try {
                FileWriter fw = new FileWriter(csv);
                fw.write(args.getString(0));
                fw.close();
            } catch (IOException ioe) {
                callback.error("Could not save naturalForms data.");
            }

            LaunchIntent.setDataAndType(Uri.fromFile(csv), "text/csv");

            ResolveInfo best = getPackageInfo(LaunchIntent, "net.expedata.naturalforms");
            if (best == null) {
                callback.error("Please Install naturalFORMS");
            } else {
                LaunchIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                cordova.getActivity().startActivity(LaunchIntent);
            }

            callback.success();
        } catch (ActivityNotFoundException e) {
            callback.error("Could not start naturalForms");
        }
        return true;
    }

    private ResolveInfo getPackageInfo(final Intent intent, final String packageName) {
        Log.e(TAG, "!!!!!!getPackageInfo");
        final PackageManager pm = cordova.getActivity().getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);

        ResolveInfo best = null;
        for (final ResolveInfo info : matches) {
            Log.e(TAG, info.activityInfo.packageName);
            if (info.activityInfo.packageName.contains(packageName)) {
                best = info;
            }
        }
        return best;
    }
}