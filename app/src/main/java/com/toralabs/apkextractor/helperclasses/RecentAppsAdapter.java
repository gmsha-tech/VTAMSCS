package com.toralabs.apkextractor.helperclasses;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
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

public class RecentAppsAdapter extends RecyclerView.Adapter<RecentAppsAdapter.ViewHolder> {

    ArrayList<RecentAppsHelper> list = new ArrayList<>();
    Context context;
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

    public RecentAppsAdapter(ArrayList<RecentAppsHelper> list, Context mContext) {
        this.context = mContext;
        this.list = list;

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


        final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vulnerabilities Testing App/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        dest_file_path = dir;
        download_file_url = list.get(position).getPdfUrl();
        viewHolder.pdfReportBtn.setOnClickListener(new View.OnClickListener() {
            ProgressDialog progressDialog;


            @Override
            public void onClick(View view) {
//                progressDialog = new ProgressDialog(context);
//                progressDialog.setTitle("Downloading...");
//                progressDialog.setCancelable(false);
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                progressDialog.show();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        downloadAndOpenPDF(list.get(position).getPdfUrl());
                    }
                });

//                AndroidNetworking.download(list.get(position).pdfUrl, String.valueOf(dir),list.get(position).packageName +".pdf")
//                        .setPriority(Priority.MEDIUM)
//                        .build()
//                        .setDownloadProgressListener(new DownloadProgressListener() {
//                            @Override
//                            public void onProgress(long bytesDownloaded, long totalBytes) {
//                                Log.e("download", "onProgress: "+bytes2String(bytesDownloaded) );
//                                progressDialog.setProgressNumberFormat((bytes2String(totalBytes)) + "/" + (bytes2String(bytesDownloaded)));
//                            }
//                        })
//                        .startDownload(new DownloadListener() {
//                            @Override
//                            public void onDownloadComplete() {
//                                // do anything after completion
//                                progressDialog.dismiss();
//                                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
//
//                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vulnerabilities Testing App/",
//                                        list.get(position).packageName +".pdf");
//                                Uri path = Uri.fromFile(file);
//                                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
//                                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                pdfOpenintent.setDataAndType(path, "application/pdf");
//                                try {
//                                    context.startActivity(pdfOpenintent);
//                                }
//                                catch (ActivityNotFoundException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                            @Override
//                            public void onError(ANError error) {
//                                // handle error
//                                progressDialog.dismiss();
//                                Toast.makeText(context, "download failed: " + error.getErrorDetail(), Toast.LENGTH_SHORT).show();
//                                Log.e("download", "onError: "+error.getErrorBody() );
//                            }
//                        });
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }

    private static double SPACE_KB = 1024;
    private static double SPACE_MB = 1024 * SPACE_KB;
    private static double SPACE_GB = 1024 * SPACE_MB;
    private static double SPACE_TB = 1024 * SPACE_GB;

    public static String bytes2String(long sizeInBytes) {

        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);

        try {
            if (sizeInBytes < SPACE_KB) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if (sizeInBytes < SPACE_MB) {
                return nf.format(sizeInBytes / SPACE_KB) + " KB";
            } else if (sizeInBytes < SPACE_GB) {
                return nf.format(sizeInBytes / SPACE_MB) + " MB";
            } else if (sizeInBytes < SPACE_TB) {
                return nf.format(sizeInBytes / SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes / SPACE_TB) + " TB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }

    }

    File downloadFile(String dwnload_file_path) {
        File file = dest_file_path;
        try {

            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            // connect
            urlConnection.connect();


            FileOutputStream fileOutput = new FileOutputStream(file);

            // Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            // this is the total size of the file which we are
            // downloading
            totalsize = urlConnection.getContentLength();
            setText("Starting PDF download...");

            // create a buffer...
            byte[] buffer = new byte[1024 * 1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                per = ((float) downloadedSize / totalsize) * 100;
                setText("Total PDF File size  : "
                        + (totalsize / 1024)
                        + " KB\n\nDownloading PDF " + (int) per
                        + "% complete");
            }
            // close the output stream when complete //
            fileOutput.close();
            setText("Download Complete. Open PDF Application installed in the device.");

        } catch (final MalformedURLException e) {
            setTextError("Some error occured. Press back and try again. MALFORM",
                    Color.RED);
        } catch (final IOException e) {
            setTextError("Some error occured. Press back and try again. IO",
                    Color.RED);
        } catch (final Exception e) {
            setTextError(
                    "Failed to download image. Please check your internet connection.",
                    Color.RED);
        }
        return file;
    }

    void setTextError(final String message, final int color) {

        Log.e("downloaderror", "Error: "+message );
    }

    void setText(final String txt) {
        Log.e("download", "progress: "+txt );

    }
    void downloadAndOpenPDF(final String download_file_url) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        new Thread(new Runnable() {
            public void run() {
                Uri path = Uri.fromFile(downloadFile(download_file_url));
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("download", "run: PDF Reader application is not installed in your device" );
                }
            }
        }).start();

    }
}
