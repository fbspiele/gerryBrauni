package com.fbspiele.gerrybrauni;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SoundData{
    String soundDataName;
    boolean soundDataNameIsActive = true;
    int soundDataResId;
    SoundData(String newName, int newResId){
        soundDataName = newName;
        soundDataResId = newResId;
    }
}

public class MainActivity extends AppCompatActivity {
    final static String tag = "MainActivity";
    private final static int MAX_VOLUME = 100;
    float volume;


    List<SoundData> gesRichtigSoundDataList = new ArrayList<>();
    List<SoundData> gesFalschSoundDataList = new ArrayList<>();
    List<SoundData> activeRichtigSoundDataList = new ArrayList<>();
    List<SoundData> activeFalschSoundDataList = new ArrayList<>();

    static List<SoundData> getDefaultRichtigSoundDataList(Context context){
        List<SoundData> soundDataList = new ArrayList<>();
        soundDataList.add(new SoundData("biathlon richtig",R.raw.richtig_biathlon));
        soundDataList.add(new SoundData("bling richtig",R.raw.richtig_bling));
        soundDataList.add(new SoundData("familienduell richtig",R.raw.richtig_familienduell));
        soundDataList.add(new SoundData("super mario münze richtig",R.raw.richtig_super_mario_coin));
        soundDataList.add(new SoundData("wer wird millionär richtig",R.raw.richtig_wer_wird_millionaer));
        return soundDataList;
    }

    static List<SoundData> getDefaultFalschSoundDataList(Context context){
        List<SoundData> soundDataList = new ArrayList<>();
        soundDataList.add(new SoundData("biathlon falsch",R.raw.falsch_biathlon));
        soundDataList.add(new SoundData("definitiv nein falsch",R.raw.falsch_definitiv_nein));
        soundDataList.add(new SoundData("döö dööööb falsch",R.raw.falsch_doedoe_doeoeoeb));
        soundDataList.add(new SoundData("döööööd falsch",R.raw.falsch_doeoeoeoeoed));
        soundDataList.add(new SoundData("familienduell falsch",R.raw.falsch_familienduell));
        soundDataList.add(new SoundData("traurige trompete falsch",R.raw.falsch_sad_trombone));
        soundDataList.add(new SoundData("super mario tod falsch",R.raw.falsch_super_mario));
        soundDataList.add(new SoundData("wer wird millionär falsch",R.raw.falsch_wer_wird_millionaer));
        soundDataList.add(new SoundData("zonk falsch",R.raw.falsch_zonk));
        return soundDataList;
    }


    void listenLaden(Context context){
        gesRichtigSoundDataList = getLoadGesRichtigSoundDataList(context);
        gesFalschSoundDataList = getLoadGesFalschSoundDataList(context);

        activeRichtigSoundDataList = new ArrayList<>();
        for(SoundData soundData:gesRichtigSoundDataList){
            if(soundData.soundDataNameIsActive){
                activeRichtigSoundDataList.add(soundData);
            }
        }

        activeFalschSoundDataList = new ArrayList<>();
        for(SoundData soundData:gesFalschSoundDataList){
            if(soundData.soundDataNameIsActive){
                activeFalschSoundDataList.add(soundData);
            }
        }

        Log.v(tag, "gesRichtigSoundDataList.size() " + gesRichtigSoundDataList.size());
        Log.v(tag, "gesFalschSoundDataList.size() " + gesFalschSoundDataList.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;

        listenLaden(this);

        findViewById(R.id.buttonRichtig).setOnClickListener(v -> playRichtigSound(context));
        findViewById(R.id.buttonFalsch).setOnClickListener(v -> playFalschSound(context));

        SeekBar volumeSeekbar = findViewById(R.id.seekBarLautstarke);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        volume = sharedPreferences.getFloat(getString(R.string.prefs_volume_save_key),100);
        int volumeProzent = (int)(MAX_VOLUME-Math.exp((1-volume)*Math.log(MAX_VOLUME)));
        Log.v(tag, "volumeProzent: " + volumeProzent);
        volumeSeekbar.setProgress(volumeProzent);

        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.v(tag, "volumeSeekBar progress: "+progress);
                volume = (float) (1 - (Math.log(MAX_VOLUME - progress) / Math.log(MAX_VOLUME)));
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                sharedPref.edit().putFloat(getString(R.string.prefs_volume_save_key),volume).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_settings = new Intent(context, SettingsActivity.class);
                startActivity(intent_settings);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        listenLaden(this);
    }

    static List<SoundData> getLoadGesRichtigSoundDataList(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getString(R.string.prefs_key_richtig_list_save_key),"");
        if(json.equals("")){
            return getDefaultRichtigSoundDataList(context);
        }
        List<SoundData> returnList = gson.fromJson(json, new TypeToken<List<SoundData>>(){}.getType());
        if(returnList==null){
            return getDefaultRichtigSoundDataList(context);
        }
        return returnList;
    }
    static List<SoundData> getLoadGesFalschSoundDataList(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getString(R.string.prefs_key_falsch_list_save_key),"");
        if(json.equals("")){
            return getDefaultFalschSoundDataList(context);
        }
        List<SoundData> returnList = gson.fromJson(json, new TypeToken<List<SoundData>>(){}.getType());
        if(returnList==null){
            return getDefaultFalschSoundDataList(context);
        }
        return returnList;
    }


    public static void saveGesRichtigSoundDataList(Context context, List<SoundData> richtigSoundDataList){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(richtigSoundDataList);
        editor.putString(context.getString(R.string.prefs_key_richtig_list_save_key),json);
        editor.apply();
    }
    public static void saveGesFalschSoundDataList(Context context, List<SoundData> falschSoundDataList){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(falschSoundDataList);
        editor.putString(context.getString(R.string.prefs_key_falsch_list_save_key),json);
        editor.apply();
    }



    int getNextRandomSoundNumber(int oldNumber, List<SoundData> list){
        if(list.size()==1||list.size()==0){
            Log.v(tag, "list.size() " + list.size()+"\n returning 0");
            return 0;
        }

        int newCandidate = new Random().nextInt(list.size());
        if(newCandidate!=oldNumber){
            return newCandidate;
        }
        return getNextRandomSoundNumber(oldNumber, list);
    }

    void playRichtigSound(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int oldNumber = sharedPreferences.getInt(getString(R.string.prefs_last_active_right_index),-1);
        int randomSoundNumber = getNextRandomSoundNumber(oldNumber, activeRichtigSoundDataList);
        sharedPreferences.edit().putInt(getString(R.string.prefs_last_active_right_index), randomSoundNumber).apply();
        play(context,activeRichtigSoundDataList.get(randomSoundNumber), volume);
    }
    void playFalschSound(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int oldNumber = sharedPreferences.getInt(getString(R.string.prefs_last_active_wrong_index),-1);
        int randomSoundNumber = getNextRandomSoundNumber(oldNumber, activeFalschSoundDataList);
        sharedPreferences.edit().putInt(getString(R.string.prefs_last_active_wrong_index), randomSoundNumber).apply();
        play(context,activeFalschSoundDataList.get(randomSoundNumber), volume);
    }


    void play(Context context, SoundData soundData, float volume){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, soundData.soundDataResId);
        if(mediaPlayer==null){
            String errorMessage = "mediaPlayer is nicht initalisiert und wollte sound spielen";
            Log.v(tag, errorMessage);
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            return;
        }
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(context, soundData.soundDataResId);
        }
        mediaPlayer.setVolume(volume,volume);
        Log.v(tag, "volume: " + volume);
        mediaPlayer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}