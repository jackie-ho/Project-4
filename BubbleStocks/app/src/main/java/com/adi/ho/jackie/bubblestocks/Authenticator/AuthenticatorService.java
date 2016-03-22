package com.adi.ho.jackie.bubblestocks.Authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by JHADI on 3/22/16.
 */
public class AuthenticatorService extends Service {
    private PlaceHolderAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new PlaceHolderAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
