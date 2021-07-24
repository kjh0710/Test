package com.example.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Main extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startLoginActivity();
        }


        findViewById(R.id.goto_Mypage).setOnClickListener(onClickListener);
        findViewById(R.id.goto_uploadpage_button).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             switch (v.getId()){
                 case R.id.goto_Mypage:
                     startMypageActivity();
                     break;
                 case R.id.goto_uploadpage_button:
                     startUploadpageActivity();
                     break;
             }
        }
    };



    private void startLoginActivity(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);

    }

    private void startMypageActivity(){
        Intent intent = new Intent(this, Mypage.class);
        startActivity(intent);

    }

    private void startUploadpageActivity(){
        Intent intent = new Intent(this, Upload.class);
        startActivity(intent);

    }





}
