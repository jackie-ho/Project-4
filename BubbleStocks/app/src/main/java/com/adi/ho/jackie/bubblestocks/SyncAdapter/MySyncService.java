package com.adi.ho.jackie.bubblestocks.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by JHADI on 3/22/16.
 */
public class MySyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static StockSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new StockSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MySyncService", "Binding");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
