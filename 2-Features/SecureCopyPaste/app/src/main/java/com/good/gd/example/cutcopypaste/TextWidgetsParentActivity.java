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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.example.cutcopypaste.utils.StringUtils;
import com.good.gd.example.cutcopypaste.utils.ViewUtils;

import java.util.Map;

/**
 * Activity which shows both GD text controls that use text encryption/decryption
 * before for performing cut/copy/paste operations and non-GD text controls.
 */
public class TextWidgetsParentActivity extends SampleAppActivity
                                        implements GDStateListener,
                                                   View.OnClickListener,
                                                   View.OnLongClickListener {

    protected String TAG = TextWidgetsParentActivity.class.getSimpleName();

    private static final String MIME_TYPE_HTML = "text/html";
    private static final String ENCODING_UTF_8 = "utf-8";

    private static final String GD_EDIT_TEXT_CONTENT_KEY = "gd_edit_text_data_key";
    private static final String GD_AUTO_COMPLETE_TEXT_VIEW_CONTENT_KEY = "gd_auto_complete_text_view_key";
    private static final String GD_SEARCH_VIEW_CONTENT_KEY = "gd_search_view_content_key";
    private static final String GD_CLIPBOARD_CONTENT_KEY = "secure_view_content_key";
    private static final String NON_GD_EDIT_TEXT_CONTENT_KEY = "edit_text_data_key";
    private static final String NON_GD_AUTO_COMPLETE_TEXT_VIEW_CONTENT_KEY = "auto_complete_text_view_key";
    private static final String NON_GD_SEARCH_VIEW_CONTENT_KEY = "search_view_content_key";
    private static final String NON_GD_CLIPBOARD_CONTENT_KEY = "system_view_content_key";

    private static final int RANDOM_TEXT_LENGTH = 1024 * 100;

    private static final String IME_OPTION_NO_MICROPHONE = "nm";

    // we declare these so they can be set by subclasses; the bindings are different classes
    protected ViewGroup rootView;

    protected TextView gdNoSelectTextView;
    protected TextView gdTextView;
    protected TextView gdEditText;
    protected TextView gdAutoCompleteTextView;
    protected TextView gdMultiAutoCompleteTextView;
    protected TextView gdClipboardView;
    protected View gdSearchView;
    protected WebView gdWebView;

    protected TextView gdNoSelectTextViewCaption;
    protected TextView gdTextViewCaption;
    protected TextView gdEditTextCaption;
    protected TextView gdAutoCompleteTextViewCaption;
    protected TextView gdMultiAutoCompleteTextViewCaption;
    protected TextView gdClipboardViewCaption;
    protected TextView gdSearchViewCaption;
    protected TextView gdWebViewCaption;

    protected TextView nonGdNoSelectTextView;
    protected TextView nonGdTextView;
    protected TextView nonGdEditText;
    protected TextView nonGdAutoCompleteTextView;
    protected TextView nonGdMultiAutoCompleteTextView;
    protected TextView nonGdClipboardView;
    protected View nonGdSearchView;
    protected WebView nonGdWebView;

    protected TextView nonGdNoSelectTextViewCaption;
    protected TextView nonGdTextViewCaption;
    protected TextView nonGdEditTextCaption;
    protected TextView nonGdAutoCompleteTextViewCaption;
    protected TextView nonGdMultiAutoCompleteTextViewCaption;
    protected TextView nonGdClipboardViewCaption;
    protected TextView nonGdSearchViewCaption;
    protected TextView nonGdWebViewCaption;

    protected TextView inboundState;
    protected TextView outboundState;
    protected TextView dictationState;
    protected TextView fieldLengthInfoView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GDAndroid.getInstance().activityInit(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            String fieldInfoText = getString(R.string.field_length_hint,
                                             getResources().getInteger(R.integer.field_limit));
            fieldLengthInfoView.setText(fieldInfoText);

            gdWebView.loadData(getString(R.string.gd_web_view_text), MIME_TYPE_HTML, ENCODING_UTF_8);

            nonGdWebView.loadData(getString(R.string.non_gd_web_view_text), MIME_TYPE_HTML, ENCODING_UTF_8);

            setFieldCaptions();
            setFieldCaptionColors();

            gdNoSelectTextView.setOnClickListener(this);
            gdNoSelectTextView.setOnLongClickListener(this);
            nonGdNoSelectTextView.setOnClickListener(this);
            nonGdNoSelectTextView.setOnLongClickListener(this);

        } catch (Exception ex) {

            if (ex.getMessage().contains("com.good.gd.widget.GDWebView") || ex.getMessage().contains("webview")){
                Log.e(TAG, "Please install/enable a Android System WebView app on your device..", ex);
                Toast.makeText(this, "Please install/enable a Android System WebView app on your device.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    protected void restoreFromSavedInstanceState(final Bundle savedState) {
        
        restoreTextToTextView(gdEditText, savedState.getCharSequence(GD_EDIT_TEXT_CONTENT_KEY));
        restoreTextToTextView(gdAutoCompleteTextView, savedState.getCharSequence(GD_AUTO_COMPLETE_TEXT_VIEW_CONTENT_KEY));
        restoreTextToTextView(gdClipboardView, savedState.getCharSequence(GD_CLIPBOARD_CONTENT_KEY));
        restoreQueryToSearchView(gdSearchView, savedState.getCharSequence(GD_SEARCH_VIEW_CONTENT_KEY));

        restoreTextToTextView(nonGdEditText, savedState.getCharSequence(NON_GD_EDIT_TEXT_CONTENT_KEY));
        restoreTextToTextView(nonGdAutoCompleteTextView, savedState.getCharSequence(NON_GD_AUTO_COMPLETE_TEXT_VIEW_CONTENT_KEY));
        restoreTextToTextView(nonGdClipboardView, savedState.getCharSequence(NON_GD_CLIPBOARD_CONTENT_KEY));
        restoreQueryToSearchView(nonGdSearchView, savedState.getCharSequence(NON_GD_SEARCH_VIEW_CONTENT_KEY));
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        saveTextFromTextView(outState, GD_EDIT_TEXT_CONTENT_KEY, gdEditText);
        saveTextFromTextView(outState, GD_AUTO_COMPLETE_TEXT_VIEW_CONTENT_KEY, gdAutoCompleteTextView);
        saveTextFromTextView(outState, GD_CLIPBOARD_CONTENT_KEY, gdClipboardView);
        saveQueryFromSearchView(outState, GD_SEARCH_VIEW_CONTENT_KEY, gdSearchView);

        saveTextFromTextView(outState, NON_GD_EDIT_TEXT_CONTENT_KEY, nonGdEditText);
        saveTextFromTextView(outState, NON_GD_AUTO_COMPLETE_TEXT_VIEW_CONTENT_KEY, nonGdAutoCompleteTextView);
        saveTextFromTextView(outState, NON_GD_CLIPBOARD_CONTENT_KEY, nonGdClipboardView);
        saveQueryFromSearchView(outState, NON_GD_SEARCH_VIEW_CONTENT_KEY, nonGdSearchView);
    }

    private void setFieldCaptions() {
        gdNoSelectTextViewCaption.setText(classNameFor(gdNoSelectTextView));
        gdTextViewCaption.setText(classNameFor(gdTextView));
        gdEditTextCaption.setText(classNameFor(gdEditText));
        gdAutoCompleteTextViewCaption.setText(classNameFor(gdAutoCompleteTextView));
        gdMultiAutoCompleteTextViewCaption.setText(classNameFor(gdMultiAutoCompleteTextView));
        gdClipboardViewCaption.setText(classNameFor(gdClipboardView));
        gdSearchViewCaption.setText(classNameFor(gdSearchView));
        gdWebViewCaption.setText(classNameFor(gdWebView));

        nonGdNoSelectTextViewCaption.setText(classNameFor(nonGdNoSelectTextView));
        nonGdTextViewCaption.setText(classNameFor(nonGdTextView));
        nonGdEditTextCaption.setText(classNameFor(nonGdEditText));
        nonGdAutoCompleteTextViewCaption.setText(classNameFor(nonGdAutoCompleteTextView));
        nonGdMultiAutoCompleteTextViewCaption.setText(classNameFor(nonGdMultiAutoCompleteTextView));
        nonGdClipboardViewCaption.setText(classNameFor(nonGdClipboardView));
        nonGdSearchViewCaption.setText(classNameFor(nonGdSearchView));
        nonGdWebViewCaption.setText(classNameFor(nonGdWebView));
    }

    private String classNameFor(View view) {
        String simpleName = view.getClass().getSimpleName();
        if (simpleName.startsWith("GD") || simpleName.startsWith("AppCompat")) {
            return simpleName;
        } else {
            return view.getClass().getName();
        }
    }

    private void setFieldCaptionColors() {
        setCaptionColor(gdNoSelectTextViewCaption, widgetIsDynamics(gdNoSelectTextView));
        setCaptionColor(gdTextViewCaption, widgetIsDynamics(gdTextView));
        setCaptionColor(gdEditTextCaption, widgetIsDynamics(gdEditText));
        setCaptionColor(gdAutoCompleteTextViewCaption, widgetIsDynamics(gdAutoCompleteTextView));
        setCaptionColor(gdMultiAutoCompleteTextViewCaption, widgetIsDynamics(gdMultiAutoCompleteTextView));
        setCaptionColor(gdSearchViewCaption, widgetIsDynamics(gdSearchView));
        setCaptionColor(gdWebViewCaption, widgetIsDynamics(gdWebView));
        setCaptionColor(gdClipboardViewCaption, true);

        setCaptionColor(nonGdNoSelectTextViewCaption, !widgetIsDynamics(nonGdNoSelectTextView));
        setCaptionColor(nonGdTextViewCaption, !widgetIsDynamics(nonGdTextView));
        setCaptionColor(nonGdEditTextCaption, !widgetIsDynamics(nonGdEditText));
        setCaptionColor(nonGdAutoCompleteTextViewCaption, !widgetIsDynamics(nonGdAutoCompleteTextView));
        setCaptionColor(nonGdMultiAutoCompleteTextViewCaption, !widgetIsDynamics(nonGdMultiAutoCompleteTextView));
        setCaptionColor(nonGdSearchViewCaption, !widgetIsDynamics(nonGdSearchView));
        setCaptionColor(nonGdWebViewCaption, !widgetIsDynamics(nonGdWebView));
        setCaptionColor(nonGdClipboardViewCaption, true);
    }

    private void setCaptionColor(TextView textView, boolean isCorrect) {
        textView.setTextColor(
                    getColor(
                        isCorrect ? R.color.caption_color_good : R.color.caption_color_bad));
    }

    private boolean widgetIsDynamics(View view) {
        return view.getClass().getName().startsWith("com.good.gd");
    }

    public void launchPreferences(View aView) {

        Intent i = new Intent();
        i.setClass(this.getApplicationContext(), PreferencesActivity.class);

        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void clearAllFields(View view) {

        ViewUtils.recursiveLoopChildren(rootView, new ViewUtils.ViewAction() {
            @Override
            public void execute(View view) {
                if (view instanceof EditText) {
                    ((EditText) view).setText("");
                }
            }
        });
    }

    /**
     * @param view - Generate random text button
     *
     * Generates random text and puts it to system clipboard
     *
     * Invoked when "Generate random text" is pressed, it generates random text data with length of {@value #RANDOM_TEXT_LENGTH} and place it on the clipboard programmatically.
     * The user can then attempt to paste the data back into the sample application, or into a different application.
     */
    public void generateText(View view) {

        String randomStringChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ1234567890~!@#$%^&*()_+=-";

        String generatedText = StringUtils.randomString(RANDOM_TEXT_LENGTH, randomStringChars);

        Log.i(TAG, "generated string size: " + generatedText.getBytes().length);

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", generatedText));
    }

    private void updateDLPViews(final Map<String, Object> settings) {

        Boolean outBoundDLP = (Boolean) settings.get(GDAndroid.GDAppConfigKeyPreventDataLeakageOut);
        Boolean inBoundDLP = (Boolean) settings.get(GDAndroid.GDAppConfigKeyPreventDataLeakageIn);
        Boolean preventDictation = (Boolean) settings.get(GDAndroid.GDAppConfigKeyPreventAndroidDictation);
        Boolean keyboardRestrictedModeIsEnabled = (Boolean) settings.get(GDAndroid.GDAppConfigKeyAndroidKeyboardRestrictedMode);

        outboundState.setText(outBoundDLP ? R.string.enabled : R.string.disabled);
        inboundState.setText(inBoundDLP ? R.string.enabled : R.string.disabled);

        if (preventDictation) {
            dictationState.setText(R.string.enabled);

            //Prevent Dictation policy is enabled
            //removing mic option from soft keyboard
            gdClipboardView.setPrivateImeOptions(IME_OPTION_NO_MICROPHONE);
        } else {
            dictationState.setText(R.string.disabled);

            //Prevent Dictation policy is disabled
            //set ime options to default
            gdClipboardView.setPrivateImeOptions(null);
        }
        
        if (keyboardRestrictedModeIsEnabled != null) {
            int imeOptions = gdClipboardView.getImeOptions();
            
            if (keyboardRestrictedModeIsEnabled) {
                // add flag
                imeOptions |= EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING;
            } else {
                // remove flag
                imeOptions &= ~EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING;
            }

            gdClipboardView.setImeOptions(imeOptions);
        }
    }

    /*
        There should be two sample GD Apps with toggle On/Off for Fingerprint usage:
        SecureCopyPaste and Greetings Client.
    */
    public void launchFingerprint(View aView) {
        GDAndroid.getInstance().openFingerprintSettingsUI();
    }

    @Override
    public void onAuthorized() {
        updateDLPViews(GDAndroid.getInstance().getApplicationConfig());
    }

    @Override
    public void onLocked() {
    }

    @Override
    public void onWiped() {
    }

    @Override
    public void onUpdateConfig(final Map<String, Object> settings) {
        updateDLPViews(settings);
    }

    @Override
    public void onUpdatePolicy(final Map<String, Object> policyValues) {
    }

    @Override
    public void onUpdateServices() {
    }

    @Override
    public void onUpdateEntitlements() {
    }

    public void launchRichtext(View view) {
        startActivity(new Intent(this,RichTextActivity.class));
    }

    public void clearPasteboard(View view) {
        // this file already has imported Android ClipboardManager
        // so use full path to SDK class to use it
        com.good.gd.content.ClipboardManager.getInstance(this).clearPrimaryClip();
    }

    // utility

    private void saveTextFromTextView(Bundle bundle, String key, TextView textView) {
        bundle.putCharSequence(key, textView != null ? textView.getText() : "");
    }

    private void restoreTextToTextView(TextView textView, CharSequence text) {
        if (textView != null) {
            textView.setText(text);
        }
    }

    private void saveQueryFromSearchView(Bundle bundle, String key, View view) {
        if (view instanceof android.widget.SearchView) {
            bundle.putCharSequence(key, ((android.widget.SearchView) view).getQuery());
        } else if (view instanceof androidx.appcompat.widget.SearchView) {
            bundle.putCharSequence(key, ((androidx.appcompat.widget.SearchView) view).getQuery());
        }
    }

    private void restoreQueryToSearchView(View view, CharSequence text) {
        if (view instanceof android.widget.SearchView) {
            ((android.widget.SearchView) view).setQuery(text, false);
        } else if (view instanceof androidx.appcompat.widget.SearchView) {
            ((androidx.appcompat.widget.SearchView) view).setQuery(text, false);
        }
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "onClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, "onLongClick", Toast.LENGTH_SHORT).show();
        return true;
    }
}
