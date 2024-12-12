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


 /*
 * This file contains sample code that is licensed according to the BlackBerry Dynamics SDK terms and conditions.
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */

package com.good.gd.example.services.greetings.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class GreetingsClientDialogFragment extends DialogFragment {

    private static final String TITLE_KEY = "title_key";
    private static final String MESSAGE_KEY = "message_key";

    private String message;
    private String title;

    static GreetingsClientDialogFragment createInstance(String title, String message) {

        GreetingsClientDialogFragment mess = new GreetingsClientDialogFragment();
        Bundle bdl = new Bundle(2);
        bdl.putString(TITLE_KEY, title);
        bdl.putString(MESSAGE_KEY, message);
        mess.setArguments(bdl);
        return mess;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        message = getArguments().getString(MESSAGE_KEY);
        title = getArguments().getString(TITLE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_fragment, container);
        TextView t = view.findViewById(R.id.message);
        t.setText(message);
        getDialog().setTitle(title);

        Button b = view.findViewById(R.id.dismiss_button);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });
        return view;
    }
}
