
package com.in_sync.receivers;

import android.content.Context;
import android.content.Intent;

import com.in_sync.MainActivity;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent restartIntent = new Intent(context, MainActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(restartIntent);
    }
}
