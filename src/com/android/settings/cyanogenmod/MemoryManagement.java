/*
 * Copyright (C) 2012-2013 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import java.io.File;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class MemoryManagement extends SettingsPreferenceFragment {

    public static final String KSM_RUN_FILE = "/sys/kernel/mm/ksm/run";
    public static final String KSM_PREF = "pref_ksm";

    public static final String KSM_PREF_DISABLED = "0";
    public static final String KSM_PREF_ENABLED = "1";

    private static final String PURGEABLE_ASSETS_PREF = "pref_purgeable_assets";
    private static final String PURGEABLE_ASSETS_PERSIST_PROP = "persist.sys.purgeable_assets";
    private static final String PURGEABLE_ASSETS_DEFAULT = "0";

    private static final String LOW_RAM_PREF = "pref_low_ram";
    private static final String LOW_RAM_PERSIST_PROP = "persist.config.low_ram";
    private static final String LOW_RAM_DEFAULT_PROP = "ro.config.low_ram";

    private CheckBoxPreference mPurgeableAssetsPref;
    private CheckBoxPreference mKSMPref;
    private CheckBoxPreference mLowRamPref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.memory_management);

        PreferenceScreen prefSet = getPreferenceScreen();

        mPurgeableAssetsPref = (CheckBoxPreference) prefSet.findPreference(PURGEABLE_ASSETS_PREF);
        mKSMPref = (CheckBoxPreference) prefSet.findPreference(KSM_PREF);
        mLowRamPref = (CheckBoxPreference) prefSet.findPreference(LOW_RAM_PREF);

        if (Utils.fileExists(KSM_RUN_FILE)) {
            mKSMPref.setChecked("1".equals(Utils.fileReadOneLine(KSM_RUN_FILE)));
        } else {
            prefSet.removePreference(mKSMPref);
        }

        String purgeableAssets = SystemProperties.get(PURGEABLE_ASSETS_PERSIST_PROP, "0");
        mPurgeableAssetsPref.setChecked("1".equals(purgeableAssets));

        String lowRamDefault = SystemProperties.get(LOW_RAM_DEFAULT_PROP);
        if (lowRamDefault != null) {
            String lowRam = SystemProperties.get(LOW_RAM_PERSIST_PROP, lowRamDefault);
            mLowRamPref.setChecked("true".equals(lowRam));
        } else {
            prefSet.removePreference(mLowRamPref);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mPurgeableAssetsPref) {
            SystemProperties.set(PURGEABLE_ASSETS_PERSIST_PROP,
                    mPurgeableAssetsPref.isChecked() ? "1" : "0");
            return true;
        }
        if (preference == mKSMPref) {
            Utils.fileWriteOneLine(KSM_RUN_FILE, mKSMPref.isChecked() ? "1" : "0");
            return true;
        }

        if (preference == mLowRamPref) {
            SystemProperties.set(LOW_RAM_PERSIST_PROP, mLowRamPref.isChecked() ? "true" : "false");
            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        return false;
    }
}
