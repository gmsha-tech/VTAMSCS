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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.toralabs.apkextractor.AboutActivity;
import com.toralabs.apkextractor.Constants;
import com.toralabs.apkextractor.R;

import org.json.JSONArray;

public class ChoiceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout layout;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        layout =findViewById(R.id.mainLayout);
        navigationView =findViewById(R.id.nav_view);

        AndroidNetworking.initialize(getApplicationContext());

        AndroidNetworking.get(Constants.HOST_IP + Constants.SCAN)
                .addHeaders("Authorization", "1a8af0d733b59557f1d9fe1bfe978153912d440c7e900801c014c10101b20b27")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        TextView view = findViewById(R.id.recentScansTextview);
                       view.setText(response.toString());
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
        // Navigation Drawer Bar

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new
                ActionBarDrawerToggle(this,layout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
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
    public void onBackPressed(){
        if(layout.isDrawerOpen(GravityCompat.START)){
            layout.closeDrawer(GravityCompat.START);
        }
        else
        {super.onBackPressed();
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
        layout.closeDrawer(GravityCompat.START); return true;
    }

}