package com.example.intentexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intentexample.SubActivity;
import com.example.intentexample.R;

public class MainActivity extends AppCompatActivity {

    private Button btn_login;
    private EditText et_id;
    private String str;

    private int[] apartmentImages = {R.drawable.apartment1, R.drawable.apartment2, R.drawable.apartment3};
    private ImageView imageView;
    private int currentIndex = 0;
    private Handler handler;
    private Runnable runnable;

    ImageView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_id = findViewById(R.id.et_id);



        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = et_id.getText().toString();
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("str", str);
                startActivity(intent); // 액티비티 이동
            }
        });

        Button btnNaverLogin = findViewById(R.id.btn_naverLogin);
        btnNaverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://nid.naver.com/nidlogin.login?mode=form&url=https://www.naver.com/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

//        imageView = findViewById(R.id.imageView);
//
//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//                changeImage();
//                handler.postDelayed(this, 4000); // 5초마다 이미지 변경
//            }
//        };
//
//        // 액티비티가 생성될 때부터 슬라이드쇼 시작
//        handler.postDelayed(runnable, 4000);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // 액티비티가 소멸될 때 슬라이드쇼 중지
//        handler.removeCallbacks(runnable);
//    }
//
//    private void changeImage() {
//        if (currentIndex < apartmentImages.length - 1) {
//            currentIndex++;
//        } else {
//            currentIndex = 0;
//        }
//
//        imageView.setImageResource(apartmentImages[currentIndex]);
    }


}