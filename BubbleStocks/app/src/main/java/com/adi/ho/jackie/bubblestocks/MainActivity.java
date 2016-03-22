package com.adi.ho.jackie.bubblestocks;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adi.ho.jackie.bubblestocks.Fragments.StockFragment;
import com.adi.ho.jackie.bubblestocks.HttpConnections.HttpRequests;
import com.adi.ho.jackie.bubblestocks.StockPortfolio.DBStock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuotesData;

public class MainActivity extends AppCompatActivity {

    public static final String stockTwitsAuthenticate = "";
    DBStock stock1;
    public static final String AUTHORITY = "com.adi.ho.jackie.bubblestocks.StubProvider";
    // Account type
    public static final String ACCOUNT_TYPE = "example.com";
    // Account
    public static final String ACCOUNT = "default_account";

    RealmConfiguration realmConfig;
    Realm realm;
    Account mAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccount = createSyncAccount(this);
        Map<Character, Integer> map = new HashMap<>();

        Button checkStock = (Button)findViewById(R.id.stock_button);

        checkStock.setOnClickListener(stockListener);

    }
    View.OnClickListener stockListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            StockFragment stockFragment = new StockFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction stockTransaction = fragmentManager.beginTransaction();
            Bundle stockBundle = new Bundle();
            stockBundle.putParcelable(stock1.getSymbol(), stock1);
            stockFragment.setArguments(stockBundle);

            stockTransaction.replace(R.id.stock_fragmentcontainer, stockFragment).commit();
        }
    };




    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    private void autoSyncStocks(){
        int seconds = 60;
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, seconds);
    }

    //Initiate Realm Database
    public void initiateRealmInstance(){
        // Create a RealmConfiguration which is to locate Realm file in package's "files" directory.
        realmConfig = new RealmConfiguration.Builder(MainActivity.this).build();
        // Get a Realm instance for this thread
         realm = Realm.getInstance(realmConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();

    }
}
