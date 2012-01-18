/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.cts.verifier.bluetooth;

import com.android.cts.verifier.PassFailButtons;
import com.android.cts.verifier.R;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageTestActivity extends PassFailButtons.Activity {

    static final String EXTRA_DEVICE_ADDRESS = "deviceAddress";
    static final String EXTRA_SECURE = "secure";

    private static final int ENABLE_BLUETOOTH_REQUEST = 1;

    private static final String MESSAGE_DELIMITER = "\n";
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("Message (\\d+) to .*");

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService;

    private ArrayAdapter<String> mReceivedMessagesAdapter;
    private ArrayAdapter<String> mSentMessagesAdapter;

    private ListView mReceivedMessages;
    private ListView mSentMessages;

    private TextView mEmptyReceivedView;
    private TextView mEmptySentView;

    private AlertDialog mInstructionsDialog;

    private String mDeviceAddress;
    private boolean mSecure;
    private boolean mServer;

    private String mRemoteDeviceName = "";
    private StringBuilder mMessageBuffer = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.bt_messages);
        setPassFailButtonClickListeners();

        mDeviceAddress = getIntent().getStringExtra(EXTRA_DEVICE_ADDRESS);
        mSecure = getIntent().getBooleanExtra(EXTRA_SECURE, true);
        mServer = mDeviceAddress == null || mDeviceAddress.isEmpty();
        if (mServer) {
            setTitle(mSecure ? R.string.bt_secure_server : R.string.bt_insecure_server);
        } else {
            setTitle(mSecure ? R.string.bt_secure_client : R.string.bt_insecure_client);
        }

        mReceivedMessages = (ListView) findViewById(R.id.bt_received_messages);
        mReceivedMessagesAdapter = new ArrayAdapter<String>(this, R.layout.bt_message_row);
        mReceivedMessages.setAdapter(mReceivedMessagesAdapter);

        mSentMessages = (ListView) findViewById(R.id.bt_sent_messages);
        mSentMessagesAdapter = new ArrayAdapter<String>(this, R.layout.bt_message_row);
        mSentMessages.setAdapter(mSentMessagesAdapter);

        mEmptyReceivedView = (TextView) findViewById(R.id.bt_empty_received_messages);
        mReceivedMessages.setEmptyView(mEmptyReceivedView);

        mEmptySentView = (TextView) findViewById(R.id.bt_empty_sent_messages);
        mSentMessages.setEmptyView(mEmptySentView);

        setEmptyViewText(R.string.bt_no_messages);

        Button makeDiscoverableButton = (Button) findViewById(R.id.bt_make_discoverable_button);
        makeDiscoverableButton.setVisibility(mServer ? View.VISIBLE : View.GONE);
        makeDiscoverableButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDiscoverable();
            }
        });

        getPassButton().setEnabled(false);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            startChatService();
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, ENABLE_BLUETOOTH_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BLUETOOTH_REQUEST) {
            if (resultCode == RESULT_OK) {
                startChatService();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    private void startChatService() {
        mChatService = new BluetoothChatService(this, new ChatHandler());
        if (mServer) {
            mChatService.start(mSecure);
        } else {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
            mChatService.connect(device, mSecure);
        }
    }

    private void makeDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
            startActivity(intent);
        }
    }

    private class ChatHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BluetoothChatService.MESSAGE_STATE_CHANGE:
                    handleStateChange(msg);
                    break;
                case BluetoothChatService.MESSAGE_READ:
                    handleMessageRead(msg);
                    break;
                case BluetoothChatService.MESSAGE_WRITE:
                    handleMessageWrite(msg);
                    break;
                case BluetoothChatService.MESSAGE_DEVICE_NAME:
                    handleDeviceName(msg);
                    break;
                case BluetoothChatService.MESSAGE_TOAST:
                    handleToast(msg);
                    break;
            }
        }
    }

    private void handleStateChange(Message msg) {
        int state = msg.arg1;
        switch (state) {
            case BluetoothChatService.STATE_LISTEN:
                setEmptyViewText(R.string.bt_waiting);
                setProgressBarIndeterminateVisibility(true);
                showInstructionsDialog();
                break;

            case BluetoothChatService.STATE_CONNECTING:
                setEmptyViewText(R.string.bt_connecting);
                setProgressBarIndeterminateVisibility(true);
                break;

            case BluetoothChatService.STATE_CONNECTED:
                setEmptyViewText(R.string.bt_no_messages);
                setProgressBarIndeterminateVisibility(false);

                hideInstructionsDialog();
                sendInitialMessageFromClient();
                break;

            case BluetoothChatService.STATE_NONE:
                setEmptyViewText(R.string.bt_no_messages);
                setProgressBarIndeterminateVisibility(false);
                break;
        }
    }

    private void setEmptyViewText(int textId) {
        mEmptyReceivedView.setText(textId);
        mEmptySentView.setText(textId);
    }

    private void showInstructionsDialog() {
        if (mInstructionsDialog == null) {
            mInstructionsDialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(getString(R.string.bt_waiting))
                    .setMessage(getString(mSecure
                            ? R.string.bt_secure_server_instructions
                            : R.string.bt_insecure_server_instructions))
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }
        mInstructionsDialog.show();
    }

    private void hideInstructionsDialog() {
        if (mInstructionsDialog != null) {
            mInstructionsDialog.hide();
        }
    }

    private void sendInitialMessageFromClient() {
        if (!mServer) {
            sendMessage(0);
        }
    }

    private void sendMessage(int number) {
        String message = "Message " + number + " to "
                + (mRemoteDeviceName != null ? mRemoteDeviceName : "")
                + MESSAGE_DELIMITER;
        mChatService.write(message.getBytes());
    }

    private void handleMessageRead(Message msg) {
        String chunk = new String((byte[]) msg.obj, 0, msg.arg1);
        mMessageBuffer.append(chunk);

        int delimiterIndex = mMessageBuffer.indexOf(MESSAGE_DELIMITER);
        if (delimiterIndex != -1) {
            String message = mMessageBuffer.substring(0, delimiterIndex); // Chop off delimiter
            mMessageBuffer.delete(0, delimiterIndex + 1);
            addNewMessage(message);
        }
    }

    private void addNewMessage(String msg) {
        mReceivedMessagesAdapter.add(msg);
        Matcher matcher = MESSAGE_PATTERN.matcher(msg);
        if (matcher.matches()) {
            int number = Integer.valueOf(matcher.group(1));
            if (mServer && number == 10 || !mServer && number == 11) {
                getPassButton().setEnabled(true);
            }
            if (number <= 10) {
                sendMessage(number + 1);
            }
        }
    }

    private void handleMessageWrite(Message msg) {
        String sentMessage = new String((byte[]) msg.obj).trim(); // Chop off delimiter
        mSentMessagesAdapter.add(sentMessage);
    }

    private void handleDeviceName(Message msg) {
        mRemoteDeviceName = msg.getData().getString(BluetoothChatService.DEVICE_NAME);
    }

    private void handleToast(Message msg) {
        String toast = msg.getData().getString(BluetoothChatService.TOAST);
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatService.stop();
    }
}
