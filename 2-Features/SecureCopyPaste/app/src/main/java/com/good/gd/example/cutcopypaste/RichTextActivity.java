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

import android.content.Context;
import android.os.Bundle;
import android.text.Annotation;
import android.text.Editable;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.EasyEditSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LocaleSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuggestionSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.good.gd.GDAndroid;

import java.util.Locale;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;

import com.good.gd.example.cutcopypaste.databinding.ActivityRichtextBinding;

/**
    Activity demonstrating usage of the rich text
    Span objects used in this class are compatible with the Secure ClipboardManager
*/
public class RichTextActivity extends SampleAppActivity {

    private static final String TAG = "RichTextActivity";

    private ActivityRichtextBinding binding;

    private Object[] spans;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        GDAndroid.getInstance().activityInit(this);

        binding = ActivityRichtextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spans = new Object[] {
                //order of object matches the richtext_styles array declaration in SecureCopyPaste/res/values/arrays.xml
                new StyleSpan(BOLD),
                new StyleSpan(ITALIC),
                new UnderlineSpan(),
                new ForegroundColorSpan(0xff_ff_00_00),//red
                new BackgroundColorSpan(0xff_00_ff_00),//green
                new StrikethroughSpan(),
                new ScaleXSpan(1.5f),
                new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                new RelativeSizeSpan(1.2f),
                new BulletSpan(10,0xff_00_00_ff),//blue bullet
                new QuoteSpan(0xff_00_ff_ff),//cyan quote
                new LeadingMarginSpan.Standard(3),
                new URLSpan("scheme://host.com/path"),
                new TypefaceSpan("monospace"),
                new SuperscriptSpan(),
                new SubscriptSpan(),
                new AbsoluteSizeSpan(14,true),
                new TextAppearanceSpan(RichTextActivity.this.getApplicationContext(),android.R.style.TextAppearance_Small),
                new Annotation("key","value"),
                new SuggestionSpan(RichTextActivity.this.getApplicationContext(),new String[]{"suggestion1","suggestion2"},0),
                new EasyEditSpan(),
                new LocaleSpan(Locale.getDefault()),
        };

        binding.richEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        setupAppBarAndEnabledBackButton(getString(R.string.activity_title_richtext));

        View mainView = findViewById(R.id.main_layout);
        View mainContent = findViewById(R.id.content_layout);

        adjustViewsIfEdgeToEdgeMode(mainView, null, mainContent);
    }

    public void onAddStyle(View view) {

        Editable text = binding.richEditView.getText();

        //apply rich text attribute
        text.setSpan(spans[binding.richTextList.getSelectedItemPosition()],
            0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.richEditView.setText(text);
    }
}
