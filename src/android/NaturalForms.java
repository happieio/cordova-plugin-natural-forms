package io.happie.naturalforms;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NaturalForms extends CordovaPlugin {

    public static final String TAG = "NaturalForms";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callback) throws JSONException {

        final Intent nfIntent = cordova.getActivity().getPackageManager().getLaunchIntentForPackage("net.expedata.naturalforms");
        final CallbackContext finalCallBack = callback;

        if (nfIntent == null) {
            callback.error("Please install naturalForms");
        }

        File csv = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/JobNimbus/", "data.csv");
        try {
            FileWriter fw = new FileWriter(csv);
            fw.write(args.getString(0));
            fw.close();
        } catch (IOException ioe) {
            callback.error("Could not save naturalForms data.");
        }

        nfIntent.setData(Uri.fromFile(csv));
        try {
            nfIntent.setClassName("net.expedata.naturalforms", "net.expedata.naturalforms.ui.CsvImportActivity"); // , csvImport.name);

            final NaturalForms plugin = this;
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    cordova.startActivityForResult(plugin, nfIntent, 0);
                    finalCallBack.success("success");
                }
            });
            return true;
        } catch (Exception err) {
            callback.error("naturalForms is not supported on this device");
            return true;
        }
        // startActivity(nfIntent);
    }
}