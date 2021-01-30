package com.example.showallaround;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SinglePostActivity extends AppCompatActivity {

    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        setTitle("Single Post");

        Intent intent = getIntent();
        text = findViewById(R.id.textViewSingle);
        text.setText(intent.getStringExtra("id"));
    }
}