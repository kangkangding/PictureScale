package com.img.dkk.picturescale;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImgView iv_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_test = findViewById(R.id.iv_test);

        try {
            iv_test.setImg(getAssets().open("test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
