package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    //private static final String TAG = "signActivity";
// ...
// Initialize Firebase Auth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.sign_up_button).setOnClickListener(onClickListener);
        findViewById(R.id.goto_Login_Button).setOnClickListener(onClickListener);


    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sign_up_button:
                    signUp();
                    break;
                case R.id.goto_Login_Button:
                    startLoginActivity();
                    break;
            }
        }
    };

    private void signUp() {
        String email = ((EditText) findViewById(R.id.sign_up_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.sign_up_password)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.passwordCheckEdittext)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (email.contains("@") == false) {
                startToast("이메일 형식이 맞지 않습니다.");
            } else {
                if (password.length() < 6){
                    startToast("비밀번호는 6자리 이상이어야 합니다.");
                }else {
                    if (password.equals(passwordCheck)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information

                                            FirebaseUser user = mAuth.getCurrentUser();
                                            startToast("회원가입에 성공했습니다.");


                                        }else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                            startToast("동일한 이메일이 존재합니다.");
                                        }else {
                                            if (task.getException() != null) {
                                                startToast(task.getException().toString());
                                            }
                                            // If sign in fails, display a message to the user.


                                        }
                                    }
                                });
                    } else {
                        startToast("비밀번호가 일치하지 않습니다.");
                    }
                }
            }
        } else {
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }

    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startLoginActivity(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);

    }



}