package com.example.abasad.mylocationsmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {
TextView AboutTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        AboutTxt = findViewById(R.id.AboutTxt);
        String AboutText = getString(R.string.app_name) + " V" + getString(R.string.app_version) + "\n";
        AboutText += "By " + getString(R.string.developer_name) + "\n\n";
        AboutText +=  getString(R.string.app_description) + "\n";

        AboutTxt.setText(AboutText);
    }

    public void Close(View view)
    {
        finish();
    }
}
