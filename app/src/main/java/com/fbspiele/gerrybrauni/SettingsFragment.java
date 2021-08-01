package com.fbspiele.gerrybrauni;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import java.util.List;

public class SettingsFragment extends PreferenceFragmentCompat {
    PreferenceScreen preferenceScreen;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        final Context context = getContext();
        preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();

        PreferenceCategory preferenceCategoryActiveRichtig = new PreferenceCategory(context);
        PreferenceCategory preferenceCategoryInactiveRichtig = new PreferenceCategory(context);
        PreferenceCategory preferenceCategoryActiveFalsch = new PreferenceCategory(context);
        PreferenceCategory preferenceCategoryInActiveFalsch = new PreferenceCategory(context);
        preferenceCategoryActiveRichtig.setTitle(context.getString(R.string.prefs_category_active_richtig_sounds));
        preferenceCategoryInactiveRichtig.setTitle(context.getString(R.string.prefs_category_inactive_richtig_sounds));
        preferenceCategoryActiveFalsch.setTitle(context.getString(R.string.prefs_category_active_falsch_sounds));
        preferenceCategoryInActiveFalsch.setTitle(context.getString(R.string.prefs_category_inactive_falsch_sounds));

        preferenceScreen.addPreference(preferenceCategoryActiveRichtig);
        preferenceScreen.addPreference(preferenceCategoryInactiveRichtig);
        preferenceScreen.addPreference(preferenceCategoryActiveFalsch);
        preferenceScreen.addPreference(preferenceCategoryInActiveFalsch);

        List<SoundData> gesRichtigList = MainActivity.getLoadGesRichtigSoundDataList(context);
        List<SoundData> gesFalschList = MainActivity.getLoadGesFalschSoundDataList(context);

        for(SoundData soundData:gesRichtigList){
            Preference soundDataPref = new Preference(context);
            soundDataPref.setTitle(soundData.soundDataName);
            if(soundData.soundDataNameIsActive){
                preferenceCategoryActiveRichtig.addPreference(soundDataPref);
                soundDataPref.setOnPreferenceClickListener(preference -> {
                    soundData.soundDataNameIsActive=false;
                    MainActivity.saveGesRichtigSoundDataList(context,gesRichtigList);
                    onResume();
                    return false;
                });
            }
            else {
                preferenceCategoryInactiveRichtig.addPreference(soundDataPref);
                soundDataPref.setOnPreferenceClickListener(preference -> {
                    soundData.soundDataNameIsActive=true;
                    MainActivity.saveGesRichtigSoundDataList(context,gesRichtigList);
                    onResume();
                    return false;
                });
            }
        }
        for(SoundData soundData:gesFalschList){
            Preference soundDataPref = new Preference(context);
            soundDataPref.setTitle(soundData.soundDataName);
            if(soundData.soundDataNameIsActive){
                preferenceCategoryActiveFalsch.addPreference(soundDataPref);
                soundDataPref.setOnPreferenceClickListener(preference -> {
                    soundData.soundDataNameIsActive=false;
                    MainActivity.saveGesFalschSoundDataList(context,gesFalschList);
                    onResume();
                    return false;
                });
            }
            else {
                preferenceCategoryInActiveFalsch.addPreference(soundDataPref);
                soundDataPref.setOnPreferenceClickListener(preference -> {
                    soundData.soundDataNameIsActive=true;
                    MainActivity.saveGesFalschSoundDataList(context,gesFalschList);
                    onResume();
                    return false;
                });
            }
        }
    }
}