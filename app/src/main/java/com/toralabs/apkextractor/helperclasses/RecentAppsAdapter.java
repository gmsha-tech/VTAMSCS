package com.toralabs.apkextractor.helperclasses;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.button.MaterialButton;
import com.toralabs.apkextractor.Constants;
import com.toralabs.apkextractor.R;
import com.toralabs.apkextractor.activities.ChoiceActivity;
import com.toralabs.apkextractor.activities.MainActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RecentAppsAdapter extends RecyclerView.Adapter<RecentAppsAdapter.ViewHolder> {

    ArrayList<RecentAppsHelper> list = new ArrayList<>();
    Context context;

    Activity activity;
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */

    File dest_file_path;
    int downloadedSize = 0, totalsize;
    String download_file_url;
    float per = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView appName, apkPackageName, timeStamp, versionName;
        MaterialButton pdfReportBtn;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            appName = view.findViewById(R.id.appname);
            apkPackageName = view.findViewById(R.id.packageName);
            timeStamp = view.findViewById(R.id.timeStamp);
            versionName = view.findViewById(R.id.version);
            pdfReportBtn = view.findViewById(R.id.downloadPdfReportBtn);
        }


    }

    public RecentAppsAdapter(ArrayList<RecentAppsHelper> list, Context mContext, Activity activity) {
        this.context = mContext;
        this.list = list;
        this.activity = activity;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recent_apps_recyclerview_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.versionName.setText("Version: " + list.get(position).getVersionName());
        viewHolder.appName.setText("App Name: " + list.get(position).getApkName());
        viewHolder.timeStamp.setText("Time: " + list.get(position).getTimeStamp());
        viewHolder.apkPackageName.setText("Apk Package Name: " + list.get(position).getPackageName());


        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.DOWNLOAD_PATH_PDF);
        if (!dir.exists()) {
            dir.mkdir();
        }
        dest_file_path = dir;
        download_file_url = list.get(position).getPdfUrl();
        viewHolder.pdfReportBtn.setOnClickListener(new View.OnClickListener() {
            ProgressDialog progressDialog;


            @Override
            public void onClick(View view) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // downloading Pdf File
                        new DownloadFile().execute(list.get(position).getPdfUrl(), list.get(position).getPackageName() + ".pdf", dir.toString());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void openPdf(final Uri path) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        new Thread(new Runnable() {
            public void run() {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("download", "run: PDF Reader application is not installed in your device");
                }
            }
        }).start();

    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            File file = new File(strings[2]);

            File pdfFile = new File(file, fileName);

            try {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    pdfFile.createNewFile();
                    openPdf(Uri.fromFile(pdfFile));
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

}
