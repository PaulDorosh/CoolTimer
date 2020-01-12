package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    TextView textView;
    SeekBar seekBar;
    boolean isTimerOn;
    CountDownTimer timer;
    Button button;
    private int defaulInterval;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar.setMax(600);
        isTimerOn = false;
        setIntervalFromSharedPreferences(sharedPreferences);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                long progressInMillis = progress * 1000;
                updateTimer(progressInMillis);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    public void start(View view) {

        if (!isTimerOn) {
            button.setText("Stop");
            seekBar.setEnabled(false);
            isTimerOn = true;

            timer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    updateTimer(millisUntilFinished);
                }

                @Override
                public void onFinish() {

                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if(sharedPreferences.getBoolean("enable_sound", true)){

                        String melodyName = sharedPreferences.getString("timer_melody", "finish");
                        switch (melodyName) {
                            case "finish": {

                                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.finishsound);
                                mediaPlayer.start();
                                break;
                            }
                            case "bip": {
                                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bipsound);
                                mediaPlayer.start();
                                break;
                            }
                            case "alarm": {
                                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm_siren_sound);
                                mediaPlayer.start();
                                break;
                            }
                        }

                    }
                    timerReset();
                }
            }.start();
        }
        else{
         timerReset();
        }

    }

    private void updateTimer(long millisUntilFinished){
        int minutes = (int) millisUntilFinished/1000/60;
        int seconds = (int )millisUntilFinished/1000 - (minutes * 60);

        String minutesString = "";
        String secondsString = "";
        if (minutes<10){
            minutesString = "0" +minutes;
        }
        else {
            minutesString = "" + minutes;
        }

        if (seconds < 10){
            secondsString = "0" + seconds;
        }
        else {
            secondsString = "" + seconds;
        }

        textView.setText(minutesString + ":" + secondsString);
    }

    private void timerReset(){
        timer.cancel();
        button.setText("Start");
        isTimerOn = false;
        seekBar.setEnabled(true);
        setIntervalFromSharedPreferences(sharedPreferences);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        }
        else
            if (id == R.id.action_about){
                Intent openAbout = new Intent(this, AboutActivity.class);
                startActivity(openAbout);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setIntervalFromSharedPreferences(SharedPreferences sharedPreferences){

        defaulInterval = Integer.valueOf(sharedPreferences.getString("default_interval", "30"));
        long defaultIntervalInMillis = defaulInterval * 1000;
        updateTimer(defaultIntervalInMillis);
        seekBar.setProgress(defaulInterval);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("default_interval")){
            setIntervalFromSharedPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
