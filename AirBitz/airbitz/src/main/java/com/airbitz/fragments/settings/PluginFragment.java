/**
 * Copyright (c) 2014, Airbitz Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Redistribution or use of modified source code requires the express written
 *    permission of Airbitz Inc.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the Airbitz Project.
 */

package com.airbitz.fragments.settings;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbitz.R;
import com.airbitz.activities.NavigationActivity;
import com.airbitz.api.PluginFramework.UiHandler;
import com.airbitz.api.PluginFramework;
import com.airbitz.fragments.BaseFragment;
import com.airbitz.fragments.send.SendFragment;
import com.airbitz.fragments.send.SendConfirmationFragment;
import com.airbitz.utils.Common;

public class PluginFragment extends BaseFragment implements NavigationActivity.OnBackPress {
    private final String TAG = getClass().getSimpleName();

    private WebView mWebView;
    private TextView mTitleTextView;
    private View mView;
    private PluginFramework mFramework;

    private SendConfirmationFragment mSendConfirmation;

    public PluginFragment() {
        mFramework = new PluginFramework(handler);
        mFramework.setup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null) {
            return mView;
        }
        mView = inflater.inflate(R.layout.fragment_plugin, container, false);
        mWebView = (WebView) mView.findViewById(R.id.plugin_webview);

        mTitleTextView = (TextView) mView.findViewById(R.id.layout_title_header_textview_title);
        mTitleTextView.setTypeface(NavigationActivity.montserratBoldTypeFace);
        mTitleTextView.setVisibility(View.INVISIBLE);

        ImageButton mBackButton = (ImageButton) mView.findViewById(R.id.layout_title_header_button_back);
        mBackButton.setVisibility(View.VISIBLE);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        mFramework.buildPluginView(mWebView);
        mWebView.loadUrl("file:///android_asset/plugin_example.html#/signup/");
        mWebView.setBackgroundColor(0x00000000);
        return mView;
    }

    @Override
    public boolean onBackPress() {
        if (mSendConfirmation != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((NavigationActivity) getActivity()).popFragment();
                }
            });
            return false;
        } else {
            mFramework.back();
            return true;
        }
    }

    private UiHandler handler = new UiHandler() {
        public void setTitle(final String title) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    PluginFragment.this.setTitle(title);
                }
            });
        }

        public void launchSend(final String cbid, final String uuid, final String address, final long amountSatoshi) {
            final SendConfirmationFragment.OnExitHandler exitHandler = new SendConfirmationFragment.OnExitHandler() {
                public void success() {
                    mFramework.sendSuccess(cbid);
                }
                public void error() {
                    mFramework.sendError(cbid);
                }
            };
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mSendConfirmation = new SendConfirmationFragment();
                    mSendConfirmation.setExitHandler(exitHandler);
                    Bundle bundle = new Bundle();
                    bundle.putString(SendFragment.UUID, address);
                    bundle.putLong(SendFragment.AMOUNT_SATOSHI, amountSatoshi);
                    bundle.putString(SendFragment.FROM_WALLET_UUID, uuid);
                    mSendConfirmation.setArguments(bundle);

                    ((NavigationActivity) getActivity()).pushFragment(mSendConfirmation, NavigationActivity.Tabs.SETTING.ordinal());
                }
            });
        }

        public void exit() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((NavigationActivity) getActivity()).popFragment();
                }
            });
        }
    };

    private void setTitle(String title) {
        mTitleTextView.setVisibility(View.VISIBLE);
        mTitleTextView.setText(title);
    }
}
