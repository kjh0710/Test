package com.example.test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.bumptech.glide.Glide;
import com.cocosw.bottomsheet.BottomSheet;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.LogMessage;
import com.gowtham.library.utils.TrimVideo;

import java.io.File;


public class Upload extends AppCompatActivity {
    private static final int REQUEST_CODE = 0;
    ImageView album_cover;
    private MediaController mediaController;
    private VideoView videoView;
    private static final String TAG = "Upload";

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {

                        Intent intent = result.getData();

                        Uri uri = intent.getData();
//                        imageview.setImageURI(uri);
                        Glide.with(Upload.this)
                                .load(uri)
                                .into(album_cover);
                    }
                }
            });

    ActivityResultLauncher<Intent> videoTrimResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null) {
                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));
                    Log.d(TAG, "Trimmed path:: " + uri);
                    videoView.setMediaController(mediaController);
                    videoView.setVideoURI(uri);
                    videoView.requestFocus();
                    videoView.start();

                    videoView.setOnPreparedListener(mediaPlayer -> {
                        mediaController.setAnchorView(videoView);
                    });

                    String filepath = String.valueOf(uri);
                    File file = new File(filepath);
                    long length = file.length();
                    Log.d(TAG, "Video size:: " + (length / 1024));
                } else
                    LogMessage.v("videoTrimResultLauncher data is null");
            });

    ActivityResultLauncher<Intent> takeOrSelectVideoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null) {
                    Intent data = result.getData();
                    //check video duration if needed
        /*        if (TrimmerUtils.getDuration(this,data.getData())<=30){
                    Toast.makeText(this,"Video should be larger than 30 sec",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                    if (data.getData() != null) {
                        Log.d(TAG,"Video path:: " + data.getData());
                        openTrimActivity(String.valueOf(data.getData()));
                    } else {
                        Toast.makeText(this, "video uri is null", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Log.d(TAG,"takeVideoResultLauncher data is null");
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpage);

        album_cover = findViewById(R.id.album_cover);
        videoView = findViewById(R.id.video_View);
        mediaController = new MediaController(this);

        findViewById(R.id.choose_album_cover).setOnClickListener(onClickListener);
        findViewById(R.id.choose_audio).setOnClickListener(onClickListener);
        findViewById(R.id.upload_server_button).setOnClickListener(onClickListener);
    }

    private void openTrimActivity(String data) {
        TrimVideo.activity(data)
                .setCompressOption(new CompressOption()) //pass empty constructor for default compress option
                .start(this, videoTrimResultLauncher);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.choose_album_cover:
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    launcher.launch(intent);
                    break;
                case R.id.choose_audio:
                    Log.d(TAG,"오디오 클릭!");
                    onDefaultTrimClicked();
                    break;
                case R.id.upload_server_button:
                    
                    break;
            }
        }
    };


    private void onDefaultTrimClicked() {
        if (checkCamStoragePer()) {
            Log.d(TAG, "바텀시트 성공!");
            showVideoOptions();
        }else{
            Log.d(TAG, "바텀시트 실패!");
        }
    }

    public void showVideoOptions() {
        try {
            BottomSheet.Builder builder = getBottomSheet();
            builder.sheet(R.menu.menu_video);
            builder.listener(item -> {
                if (R.id.action_take == item.getItemId())
                    captureVideo();
                else
                    openVideo();
                return false;
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BottomSheet.Builder getBottomSheet() {
        return new BottomSheet.Builder(this).title("Options");
    }

    public void captureVideo() {
        try {
            Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
            intent.putExtra("android.intent.extra.durationLimit", 30);
            takeOrSelectVideoResultLauncher.launch(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openVideo() {
        try {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            takeOrSelectVideoResultLauncher.launch(Intent.createChooser(intent, "Select Video"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isPermissionOk(grantResults))
            showVideoOptions();
    }

    private boolean checkCamStoragePer() {
        return checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    private boolean checkPermission(String... permissions) {
        boolean allPermitted = false;
        for (String permission : permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED);
            if (!allPermitted)
                break;
        }
        if (allPermitted)
            return true;
        ActivityCompat.requestPermissions(this, permissions,
                220);
        return false;
    }

    private boolean isPermissionOk(int... results) {
        boolean isAllGranted = true;
        for (int result : results) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }


}
