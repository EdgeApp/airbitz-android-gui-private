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

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.airbitz.AirbitzApplication;
import com.airbitz.R;
import com.airbitz.activities.NavigationActivity;
import com.airbitz.api.CoreAPI;
import com.airbitz.fragments.BaseFragment;
import com.airbitz.fragments.HelpFragment;
import com.airbitz.objects.HighlightOnPressImageButton;

/**
 * Created on 13/01/15.
 */
public class ElectrumServerFragment extends BaseFragment {
    private final String TAG = getClass().getSimpleName();

    private Switch mAirbitzSwitch;
    private EditText mServerEditText;
    private Switch mSSLSwitch;
    private Button mSaveButton;
    private CoreAPI mCoreAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCoreAPI = CoreAPI.getApi();

        setHasOptionsMenu(true);
        setDrawerEnabled(false);
        setBackEnabled(true);
    }

    @Override
    public String getTitle() {
        return mActivity.getString(R.string.fragment_electrum_server_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater i = getThemedInflater(inflater, R.style.AppTheme_Blue);
        View mView = i.inflate(R.layout.fragment_electrum_server, container, false);

        mServerEditText = (EditText) mView.findViewById(R.id.fragment_electrum_server_url_edittext);
        mServerEditText.setTypeface(Typeface.DEFAULT);
        mAirbitzSwitch = (Switch) mView.findViewById(R.id.fragment_electrum_server_airbitz_toggle);
        mAirbitzSwitch.setTypeface(Typeface.DEFAULT);
        mAirbitzSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adjustTextColors();
            }
        });
        mSSLSwitch = (Switch) mView.findViewById(R.id.fragment_electrum_server_ssl_toggle);
        mSSLSwitch.setTypeface(Typeface.DEFAULT);

        mSaveButton = (Button) mView.findViewById(R.id.fragment_electrum_server_button_logout);
        mSaveButton.setTypeface(Typeface.DEFAULT);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSave();
            }
        });

        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_standard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_help:
                ((NavigationActivity) getActivity()).pushFragment(
                    new HelpFragment(HelpFragment.ELECTRUM_SERVER), NavigationActivity.Tabs.MORE.ordinal());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void adjustTextColors() {
        if (mAirbitzSwitch.isChecked()) {
            mServerEditText.setEnabled(false);
            mSSLSwitch.setEnabled(false);
        } else {
            mServerEditText.setEnabled(true);
            mSSLSwitch.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String server = mCoreAPI.GetStratumServer();
        if(server != null && server.length() != 0)
        {
            String[] parts = server.split("://");
            if(parts.length > 1) {
                String prefix = parts[0];
                String uri = parts[1];

                if (prefix.equals("stratums")) {
                    mSSLSwitch.setChecked(true);
                } else {
                    mSSLSwitch.setChecked(false);
                }
                mServerEditText.setText(uri);
                mAirbitzSwitch.setChecked(false);
                adjustTextColors();
                return;
            }
        }

        mAirbitzSwitch.setChecked(true);
        mSSLSwitch.setChecked(false);
        adjustTextColors();

    }


    private void goSave() {
        String uri = mServerEditText.getText().toString();
        String prefix = "stratum://";

        if(mSSLSwitch.isChecked())
        {
            prefix = "stratums://";
        }

        if(mAirbitzSwitch.isChecked() || uri.equals(""))
        {
            mCoreAPI.SetStratumServer(null);
        }
        else
        {
            mCoreAPI.SetStratumServer(prefix + uri);
        }
        mActivity.popFragment();
    }
}
