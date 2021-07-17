package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Splash extends Activity {

    private static final String TAG = "높이 넓이";
    int standardSize_X, standardSize_Y;
    float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        ImageView imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);


        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 1500); // 1초 후에 hd handler 실행  3000ms = 3초

    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), Main.class)); //로딩이 끝난 후, 로그인 기록이 있으면 메인으로 이동 , 로그인 기록이 없으면 로그인 페이조
            Splash.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

}




