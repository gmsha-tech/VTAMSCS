package com.toralabs.apkextractor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.toralabs.apkextractor.R;

public class ChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);


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
}