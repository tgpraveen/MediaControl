/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.mediacontrol;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.media.AudioManager;
import android.os.SystemClock;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
/**
 * 
 * Display a message as a notification, with an accompanying sound.
 */
public class MessageDisplay{

    private MessageDisplay() {
    }

    /*
     * App-specific methods for the sample application - 1) parse the incoming
     * message; 2) generate a notification; 3) play a sound
     */

    public static void displayMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            //String sender = (String) extras.get("sender");
            String message = (String) extras.get("message");
            //int startPointer=message.charAt(':');
            //startPointer=startPointer+1; //As the separator really is ":-".
            //System.out.println("startpointer+1 is:- "+startPointer+"\n message is :- "+message);
            if (message.equals("MediaControl:-Play"))
            {
            	//Util.generateNotification(context, "Message from " + sender + ": " + message);
            	sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,context);
            	//Intent i = new Intent();
            	//i.setClassName();
                //i.setAction("android.intent.action.MEDIA_BUTTON");
                //context.sendBroadcast(i);            	
            }
            if (message.equals("MediaControl:-Forward"))
            {
            	//Util.generateNotification(context, "Message from " + sender + ": " + message);
            	sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_NEXT,context);
            	//Intent i = new Intent();
            	//i.setClassName();
                //i.setAction("android.intent.action.MEDIA_BUTTON");
                //context.sendBroadcast(i);            	
            }
            if (message.equals("MediaControl:-Rewind"))
            {
            	//Util.generateNotification(context, "Message from " + sender + ": " + message);
            	sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS,context);
            	//Intent i = new Intent();
            	//i.setClassName();
                //i.setAction("android.intent.action.MEDIA_BUTTON");
                //context.sendBroadcast(i);            	
            }
            playNotificationSound(context);
        }
    }
//Following function inspired from CM(CyanogenMod) codebase:-
    private static void sendMediaButtonEvent(int code, Context context) {
        long eventtime = SystemClock.uptimeMillis();

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, code, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        context.sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, code, 0);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        context.sendOrderedBroadcast(upIntent, null);
    }

    
    private static void playNotificationSound(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (uri != null) {
            Ringtone rt = RingtoneManager.getRingtone(context, uri);
            if (rt != null) {
            	rt.setStreamType(AudioManager.STREAM_NOTIFICATION);
                rt.play();
            }
        }
    }
}
