package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        findViewById(R.id.logout_button).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             switch (v.getId()){
                 case R.id.logout_button:
                     FirebaseAuth.getInstance().signOut();
                     startLoginActivity();
                     break;
             }
        }
    };



    private void startLoginActivity(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);

    }

}
