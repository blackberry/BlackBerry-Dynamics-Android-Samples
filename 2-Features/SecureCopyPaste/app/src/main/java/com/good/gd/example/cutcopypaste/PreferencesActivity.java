/* Copyright (c) 2023 BlackBerry Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.good.gd.example.cutcopypaste;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.good.gd.GDAndroid;

/**
 * Example Activity to show the Secure GDEditTextPreference being used as part of the Android Preferences
 * framework. As it is a Secure entity it allows (a) dictation to be disabled (b) Secure Copy/Paste to be used
 */
public class PreferencesActivity extends SampleAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {

        /*
        We use the GD Secure SharedPreferences to ensure everything the preferences screen saves is
        Securely saved by the GDSharedPreferences class
         */
        return GDAndroid.getInstance().getGDSharedPreferences(name, mode);
    }

    public static class PrefsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Make adjustments for Edge-To-Edge Mode
            if (isEdgeToEdgeModeEnforced(requireContext())) {
                adjustLayout(view);
            }
        }

        private void adjustLayout(View view) {
            ViewCompat.setOnApplyWindowInsetsListener(view, new androidx.core.view.OnApplyWindowInsetsListener() {
                @NonNull
                @Override
                public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {

                    Insets systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    Insets displayCutoutInsets = insets.getInsets(WindowInsetsCompat.Type.displayCutout());

                    int maxLeft = Math.max(systemBarInsets.left, displayCutoutInsets.left);
                    int maxRight = Math.max(systemBarInsets.right, displayCutoutInsets.right);

                    // Add a bit space at the top to avoid overlap with app bar
                    // and display cutouts
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.setMargins(maxLeft, 120, maxRight, 0);

                    // Return CONSUMED as we don't need the window insets to keep passing
                    // down to descendant views.
                    return WindowInsetsCompat.CONSUMED;
                }
            });
        }
    }
}
