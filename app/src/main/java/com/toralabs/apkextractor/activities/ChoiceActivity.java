package com.toralabs.apkextractor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.toralabs.apkextractor.AboutActivity;
import com.toralabs.apkextractor.Constants;
import com.toralabs.apkextractor.R;
import com.toralabs.apkextractor.helperclasses.RecentAppsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChoiceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout layout;
    NavigationView navigationView;

    ArrayList<RecentAppsHelper> mRecentAppsArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        layout = findViewById(R.id.mainLayout);
        navigationView = findViewById(R.id.nav_view);

        AndroidNetworking.initialize(getApplicationContext());

        AndroidNetworking.get(Constants.HOST_IP + Constants.SCAN)
                .addHeaders("Authorization", Constants.MOBSF_AUTHORIZATION)
                .setPriority(Priority.HIGH)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray array = response.getJSONArray("content");


                            for (int i = 0; i < array.length(); i++) {
                                JSONObject explrObject = array.getJSONObject(i);

                                // Generate App Url
                                String appUrl = Constants.HOST_IP +  "/" +
                                        explrObject.getString("ANALYZER") + "/?name="
                                        + explrObject.getString("FILE_NAME") + "&checksum="
                                        + explrObject.getString("MD5") + "&type=" + explrObject.getString("SCAN_TYPE");

                                // Generate PDF URL
                                String pdfUrl = Constants.HOST_IP +"/pdf/?md5=" + explrObject.getString("MD5");
                                mRecentAppsArray.add(new RecentAppsHelper(
                                        explrObject.getString("FILE_NAME"),
                                        explrObject.getString("APP_NAME"),
                                        appUrl,
                                        explrObject.getString("PACKAGE_NAME"),
                                        explrObject.getString("VERSION_NAME"),
                                        explrObject.getString("TIMESTAMP"),
                                        pdfUrl
                                ));
                                Log.e("LIST", "onCreate: "+ mRecentAppsArray.get(i).getPdfUrl());
                            }


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("API", "onError: " + anError.getErrorBody());
                    }
                });

        // Navigation Drawer Bar


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new
                ActionBarDrawerToggle(this, layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        layout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void mobosfButtonClicked(View view) {

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.mobosfchoise);


        Button choose1 = dialog.findViewById(R.id.choose1);
        Button choose2 = dialog.findViewById(R.id.choose2);

        choose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                dialog.dismiss();
            }
        });

        choose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApk();
                dialog.dismiss();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.create();
        }
        dialog.show();

    }

    public void bevigilClickedButton(View view) {

        startActivity(new Intent(getApplicationContext(), BevigilActivity.class));
    }

    // Request code for selecting a PDF document.
    private static final int REQUEST_CODE = 2;

    private void getApk() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.android.package-archive");


        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                // Handle the selected file URI (e.g., display file name, copy to another location, etc.)
                // Note: You may need to handle permission requests if targeting Android 10 (API level 29) or higher


                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                if ("apk".equalsIgnoreCase(fileExtension)) {
                    // Proceed with further processing for .apk file
                    Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
                } else {
                    // Show error message or take appropriate action for invalid file type
                    Toast.makeText(this, "Not APK", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.bevigil:

                startActivity(new Intent(getApplicationContext(), BevigilActivity.class));
                break;
            case R.id.about:
                Intent intent = new Intent(ChoiceActivity.this, AboutActivity.class);
                startActivity(intent);
                break;

        }
        layout.closeDrawer(GravityCompat.START);
        return true;
    }

}