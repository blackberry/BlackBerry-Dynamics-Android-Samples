/* Copyright (c) 2024 BlackBerry Ltd.
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
package com.good.gd.example.appkinetics.saveeditservice;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SampleAppActivity extends AppCompatActivity {

    public void setupAppBar(String appTitle) {
        TextView title = findViewById(R.id.app_bar_title);
        title.setText(appTitle);
    }

    public void setupAppBarAndEnabledBackButton(String appTitle) {
        TextView title = findViewById(R.id.app_bar_title);
        title.setText(appTitle);

        ImageView appBarIcon = findViewById(R.id.app_bar_icon);
        appBarIcon.setImageResource(R.drawable.back_arrow);

        appBarIcon.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    /**
     * Use this method to adjust sample app UI for Edge-to-edge mode on Android 15 and above
     *
     * @param mainView - a root layout of the activity or fragment
     * @param bottomBar - a bottom app bar of the activity or fragment
     * @param contentView - a layout where most of UI elements resides
     */
    public void adjustViewsIfEdgeToEdgeMode(@Nullable View mainView, @Nullable View bottomBar, @Nullable View contentView) {

        // If root layout is null then do nothing
        if (mainView == null)
            return;

        if (!isEdgeToEdgeModeEnforced(mainView.getContext())) {
            Log.i("SampleAppActivity", "adjustViewsIfEdgeToEdgeMode not in Edge to Edge mode return");
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainView, new androidx.core.view.OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {

                Insets systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                Insets displayCutoutInsets = insets.getInsets(WindowInsetsCompat.Type.displayCutout());

                int maxTop = Math.max(systemBarInsets.top, displayCutoutInsets.top);
                int maxBottom = Math.max(systemBarInsets.bottom, displayCutoutInsets.bottom);
                int maxLeft = Math.max(systemBarInsets.left, displayCutoutInsets.left);
                int maxRight = Math.max(systemBarInsets.right, displayCutoutInsets.right);

                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                mlp.topMargin = maxTop;

                v.setLayoutParams(mlp);

                View icon = mainView.findViewById(R.id.app_bar_icon);
                if (icon != null) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) icon.getLayoutParams();
                    lp.leftMargin = maxLeft;
                    icon.setLayoutParams(lp);
                }

                View action = mainView.findViewById(R.id.app_bar_action);
                if (action != null) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) action.getLayoutParams();
                    lp.rightMargin = maxRight;
                    action.setLayoutParams(lp);
                }

                if (bottomBar != null) {

                    ViewGroup.MarginLayoutParams mlp2 = (ViewGroup.MarginLayoutParams) bottomBar.getLayoutParams();
                    mlp2.bottomMargin = maxBottom;
                    mlp2.leftMargin = maxLeft;
                    mlp2.rightMargin = maxRight;

                    bottomBar.setLayoutParams(mlp2);
                }

                if (contentView != null) {

                    ViewGroup.MarginLayoutParams mlp3 = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
                    mlp3.leftMargin = maxLeft;
                    mlp3.rightMargin = maxRight;

                    contentView.setLayoutParams(mlp3);

                }

                // Return CONSUMED as we don't need the window insets to keep passing
                // down to descendant views.
                return WindowInsetsCompat.CONSUMED;
            }
        });
    }

    private static boolean isEdgeToEdgeModeEnforced(Context context) {

        boolean isAndroid15OrHigher = Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                || Build.VERSION.CODENAME.equalsIgnoreCase("VanillaIceCream");

        boolean isAndroid15TargetSDKVersion =
                context.getApplicationContext().getApplicationInfo().targetSdkVersion > Build.VERSION_CODES.UPSIDE_DOWN_CAKE;

        return isAndroid15OrHigher && isAndroid15TargetSDKVersion;
    }
}
