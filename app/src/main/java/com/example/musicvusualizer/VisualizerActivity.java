package com.example.musicvusualizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.musicvusualizer.audioVisual.AudioInputReader;
import com.example.musicvusualizer.audioVisual.VisualizerView;
 public class VisualizerActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

            private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
            private VisualizerView mVisualizerView;
            private AudioInputReader mAudioInputReader;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_visualizer);
                mVisualizerView = findViewById(R.id.activity_visualizer);
                setUpSharedPrefrences();
                setupPermissions();
            }

            private void setUpSharedPrefrences() {
                //getting shared pref instance
                SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
                //setting from prefrence
                mVisualizerView.setShowBass(sharedPreferences.getBoolean(getString(R.string.pref_show_basskey),true));
                mVisualizerView.setShowMid(true);
                mVisualizerView.setShowTreble(true);
                mVisualizerView.setMinSizeScale(1);
                mVisualizerView.setColor(getString(R.string.pref_color_green_value));
                // ---------------- resister the listner ----------------
                sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            }

            /**
             * onPause Cleanup audio stream
             **/
            @Override
            protected void onPause() {
                super.onPause();
                if (mAudioInputReader != null) {
                    mAudioInputReader.shutdown(isFinishing());
                }
            }

            @Override
            protected void onResume() {
                super.onResume();
                if (mAudioInputReader != null) {
                    mAudioInputReader.restart();
                }
            }


            private void setupPermissions() {
                // If we don't have the record audio permission...
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // And if we're on SDK M or later...
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Ask again, nicely, for the permissions.
                        String[] permissionsWeNeed = new String[]{Manifest.permission.RECORD_AUDIO};
                        requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
                    }
                } else {
                    // Otherwise, permissions were granted and we are ready to go!
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this);
                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode,
                                                   @NonNull String permissions[], @NonNull int[] grantResults) {
                switch (requestCode) {
                    case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // The permission was granted! Start up the visualizer!
                            mAudioInputReader = new AudioInputReader(mVisualizerView, this);

                        } else {
                            Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                            finish();
                            // The permission was denied, so we can show a message why we can't run the app
                            // and then close the app.
                        }
                    }
                    // Other permissions could go down here

                }
            }

     /**
      * ------------------------Menu--------------------------
      */
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.visulizer_menu,menu);
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         int id = item.getItemId();
         if (id==R.id.action_settings){
             Intent intent = new Intent(this,SettingsActivity.class);
             startActivity(intent);
             return true;
         }
         return super.onOptionsItemSelected(item);
     }
     // ============================== Shared Prefrence==========================
// -------------------------- if a shared pref changed --------------------------
     @Override
     public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
         if (key.equals(getString(R.string.pref_show_basskey))){
             mVisualizerView.setShowBass(sharedPreferences.getBoolean(key,getResources().getBoolean(R.bool.pref_show_bass_default)));
         }

     }

     /**
      * Activity Life Cycle
      */
     @Override
     protected void onDestroy() {
         //unregister the listner to avoide memory leaks
         PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
         super.onDestroy();
     }
 }

