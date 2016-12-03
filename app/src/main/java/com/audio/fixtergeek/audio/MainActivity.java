package com.audio.fixtergeek.audio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //Codigos de lospermisos
    private static final int RECORD_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;

    //DECLARAMOS VARIABLES
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static String audioFilePath;
    private static Button stopButton;
    private static Button playButton;
    private static Button recordButton;
    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //traemos los botones y checamos si el cel tiene micro yeah!!
        recordButton = (Button) findViewById(R.id.elRecord);
        playButton = (Button) findViewById(R.id.elPlay);
        stopButton = (Button) findViewById(R.id.elStop);

        if (!hasMicrophone()){
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
        }else {
            playButton.setEnabled(false);
            stopButton.setEnabled(false);
        }
        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/lupe.3gp";

        //perdimos el permiso
        requestPermission(Manifest.permission.RECORD_AUDIO, RECORD_REQUEST_CODE);

    }//onCreate

    protected boolean hasMicrophone(){
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void recordAudio(View view) throws IOException{
        isRecording = true;
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            mediaRecorder.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
        mediaRecorder.start();
    }//record

    public void stopAudio(View view){
        stopButton.setEnabled(false);
        playButton.setEnabled(true);
        if (isRecording){
            recordButton.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }else{
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
        }
    }//stop

    public void playAudio(View view) throws IOException{
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }//play

    protected void requestPermission(String permissionType, int requestCode){
        int permission = ContextCompat.checkSelfPermission(this, permissionType);
        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{permissionType}, requestCode);
        }
    }//requestpermission

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResult){
        switch (requestCode){
            case RECORD_REQUEST_CODE:{
                if (grantResult.length == 0 || grantResult[0] != PackageManager.PERMISSION_GRANTED){
                    recordButton.setEnabled(false);
                    Toast.makeText(this, "permiso de grabacion requerido", Toast.LENGTH_LONG).show();
                }else{
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_REQUEST_CODE);
                }
                return;
            }
            case STORAGE_REQUEST_CODE:{
                if (grantResult.length == 0 || grantResult[0] != PackageManager.PERMISSION_GRANTED) {
                    recordButton.setEnabled(false);
                    Toast.makeText(this, "permiso de almacenamieto requerido", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
