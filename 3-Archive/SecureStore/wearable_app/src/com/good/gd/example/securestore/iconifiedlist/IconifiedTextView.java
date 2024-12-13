/* $Id: BulletedTextView.java 57 2007-11-21 18:31:52Z steven $
 *
 * Copyright 2007 Steven Osborn
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
 *
 * Note: This is now a proprietary modified version of an original Apache
 * 2.0 file (Good Technology).
 */
package com.good.gd.example.securestore.iconifiedlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.good.gd.example.securestore.common_lib.iconifiedlist.IconifiedText;

@SuppressLint("ViewConstructor")
public class IconifiedTextView extends LinearLayout {

    private final TextView mText;
    private final ImageView mIcon;

    public IconifiedTextView(Context context, IconifiedText aIconifiedText) {
        super(context);

        /* First Icon and the Text to the right (horizontal),
         * not above and below (vertical) */
        this.setOrientation(HORIZONTAL);

        mIcon = new ImageView(context);
        mIcon.setImageDrawable(aIconifiedText.getIcon());
        // left, top, right, bottom
        mIcon.setPadding(5, 10, 5, 10); // 5px to the right

        /* At first, add the Icon to ourself
         * (! we are extending LinearLayout) */
        addView(mIcon,  new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mText = new TextView(context);
        mText.setText(aIconifiedText.getText());
        mText.setTextSize(14);
        /* Now the text (after the icon) */
        addView(mText, new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    public void setText(String words) {
        mText.setText(words);
    }

    public void setIcon(Drawable bullet) {
        mIcon.setImageDrawable(bullet);
    }
}
