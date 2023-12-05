package com.yy.sign.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yy.sign.R;

public class AgreeActivity extends Activity {
    private Button agreeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree);
        this.agreeButton = findViewById(R.id.agreeBtn);
        this.agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AgreeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}