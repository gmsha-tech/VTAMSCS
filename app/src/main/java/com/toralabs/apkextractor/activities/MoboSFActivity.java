package com.toralabs.apkextractor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.toralabs.apkextractor.R;
import com.toralabs.apkextractor.helperclasses.AppListAdapter;

public class MoboSFActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobo_sfactivity);

        textView = findViewById(R.id.uri_path_text);

        textView.setText(AppListAdapter.apkPath.toString());
    }
}