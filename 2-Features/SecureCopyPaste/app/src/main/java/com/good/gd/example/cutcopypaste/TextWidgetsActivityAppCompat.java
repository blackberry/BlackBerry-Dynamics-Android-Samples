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

import android.os.Bundle;
import android.view.View;

import com.good.gd.example.cutcopypaste.databinding.ActivityTextWidgetsAppcompatBinding;

/**
 * Activity which shows both GD text controls that use text encryption/decryption
 * before for performing cut/copy/paste operations and non-GD text controls.
 */
public class TextWidgetsActivityAppCompat extends TextWidgetsParentActivity {

    protected String TAG = TextWidgetsActivityAppCompat.class.getSimpleName();

    private ActivityTextWidgetsAppcompatBinding binding;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTextWidgetsAppcompatBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        setContentView(rootView);
        setFieldsFromBinding();

        if (savedInstanceState != null) {
            restoreFromSavedInstanceState(savedInstanceState);
        }

        setupAppBarAndEnabledBackButton(getString(R.string.activity_title_widgets_appcompat));

        View mainView = findViewById(R.id.main_layout);
        View mainContent = findViewById(R.id.content_layout);

        adjustViewsIfEdgeToEdgeMode(mainView, null, mainContent);
    }

    private void setFieldsFromBinding() {
        gdNoSelectTextView = binding.gdNoSelectTextView;
        gdTextView = binding.gdTextView;
        gdEditText = binding.gdEditText;
        gdAutoCompleteTextView = binding.gdAutoCompleteTextView;
        gdMultiAutoCompleteTextView = binding.gdMultiAutoCompleteTextView;
        gdClipboardView = binding.gdClipboardView;
        gdSearchView = binding.gdSearchView;
        gdWebView = binding.gdWebView;

        gdNoSelectTextViewCaption = binding.gdNoSelectTextViewCaption;
        gdTextViewCaption = binding.gdTextViewCaption;
        gdEditTextCaption = binding.gdEditTextCaption;
        gdAutoCompleteTextViewCaption = binding.gdAutoCompleteTextViewCaption;
        gdMultiAutoCompleteTextViewCaption = binding.gdMultiAutoCompleteTextViewCaption;
        gdClipboardViewCaption = binding.gdClipboardViewCaption;
        gdSearchViewCaption = binding.gdSearchViewCaption;
        gdWebViewCaption = binding.gdWebViewCaption;

        nonGdNoSelectTextView = binding.nonGdNoSelectTextView;
        nonGdTextView = binding.nonGdTextView;
        nonGdEditText = binding.nonGdEditText;
        nonGdAutoCompleteTextView = binding.nonGdAutoCompleteTextView;
        nonGdMultiAutoCompleteTextView = binding.nonGdMultiAutoCompleteTextView;
        nonGdClipboardView = binding.nonGdClipboardView;
        nonGdSearchView = binding.nonGdSearchView;
        nonGdWebView = binding.nonGdWebView;

        nonGdNoSelectTextViewCaption = binding.nonGdNoSelectTextViewCaption;
        nonGdTextViewCaption = binding.nonGdTextViewCaption;
        nonGdEditTextCaption = binding.nonGdEditTextCaption;
        nonGdAutoCompleteTextViewCaption = binding.nonGdAutoCompleteTextViewCaption;
        nonGdMultiAutoCompleteTextViewCaption = binding.nonGdMultiAutoCompleteTextViewCaption;
        nonGdClipboardViewCaption = binding.nonGdClipboardViewCaption;
        nonGdSearchViewCaption = binding.nonGdSearchViewCaption;
        nonGdWebViewCaption = binding.nonGdWebViewCaption;

        inboundState = binding.inboundState;
        outboundState = binding.outboundState;
        dictationState = binding.dictationState;
        fieldLengthInfoView = binding.fieldLengthInfoView;
    }
}
