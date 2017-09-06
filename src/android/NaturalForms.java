package io.happie.naturalforms;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ActivityNotFoundException;
import android.content.pm.ResolveInfo;
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

import static android.support.v4.content.FileProvider.getUriForFile;

public class NaturalForms extends CordovaPlugin {

    public static final String TAG = "NaturalFormsPlugin";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callback) throws JSONException {
        try {
            PackageManager manager = cordova.getActivity().getApplicationContext().getPackageManager();
            Intent LaunchIntent = manager.getLaunchIntentForPackage("net.expedata.naturalforms");
            if (LaunchIntent == null) {
                callback.error("naturalForms not installed");
                return true;
            }

            File nfData = new File(cordova.getActivity().getCacheDir() + File.separator + "nf-data" + System.currentTimeMillis() + ".csv");

            Log.w(TAG, "PATH: " + nfData.toString());

            try {
                nfData.createNewFile();
                FileOutputStream overWrite = new FileOutputStream(nfData.toString(), false);
                overWrite.write(args.getString(0).getBytes());
                overWrite.flush();
                overWrite.close();

                Uri contentUri = getUriForFile(cordova.getActivity().getApplicationContext(), "com.jobnimbus.JobNimbus2.provider", nfData);

                LaunchIntent.setDataAndType(contentUri, "text/csv");

                LaunchIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                ResolveInfo best = getPackageInfo(LaunchIntent, "net.expedata.naturalforms");

                LaunchIntent.setClassName(best.activityInfo.packageName, "net.expedata.naturalforms.ui.CsvImportActivity");

                cordova.getActivity().startActivity(LaunchIntent);

            } catch (IOException e) {
                callback.error(e.getMessage());
            } catch (Exception e) {
                callback.error(e.getMessage());
            }

            callback.success();
        } catch (ActivityNotFoundException e) {
            callback.error("naturalForms not installed");
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