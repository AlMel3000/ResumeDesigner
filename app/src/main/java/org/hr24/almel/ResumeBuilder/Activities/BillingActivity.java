package org.hr24.almel.ResumeBuilder.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.hr24.almel.ResumeBuilder.R;
import org.hr24.almel.ResumeBuilder.billing.IabBroadcastReceiver;
import org.hr24.almel.ResumeBuilder.billing.IabHelper;
import org.hr24.almel.ResumeBuilder.billing.IabResult;
import org.hr24.almel.ResumeBuilder.billing.Inventory;
import org.hr24.almel.ResumeBuilder.billing.Purchase;
import org.hr24.almel.ResumeBuilder.utils.ConstantManager;
import org.hr24.almel.ResumeBuilder.utils.NetworkStatusChecker;

import android.app.AlertDialog;
import android.widget.TextView;

import com.vk.sdk.util.VKUtil;

public class BillingActivity extends AppCompatActivity implements View.OnClickListener, IabBroadcastReceiver.IabBroadcastListener {

    Button purchasePremiumButton;
    TextView headerTv, disclaimerTv;
    static Context context;

    Boolean PREMIUM_STATUS = false;

    // Debug tag, for logging
    static final String TAG = "Billing";
    static final String SKU_PREMIUM = "premium";
    static final int RC_REQUEST = 10001;
    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        context = this;
        initStatus();
        purchasePremiumButton = (Button) findViewById(R.id.purchase_premium_btn);
        headerTv = (TextView) findViewById(R.id.billing_header);
        disclaimerTv = (TextView) findViewById(R.id.disclaimer_tv);

        purchasePremiumButton.setOnClickListener(this);

        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoJaltMApkN2GUBQc5HWUYslVHHsxDcvyJQ5fFjPw8oXyuWFchDoe+rt9QXSqBGMLBU3drM2hK+ZSS1hzl2rkmKU6VCzgcvSFTLgsfzrGjsHjEWVAkPUmjUJOkzsFDe58phE9DWMuBEPz5yxF8+C/fR/pxjKZl5VIinvmBBqtDN4xGDwI4aLNkDriamQVQQ3+yuiSvagOrdGB2zMR2E+PvrWzISiIwx+IK4e1MrZp3EhTntR13kQHsEf4jMD3MkaCJt+2XD+aD/v/9GSEWRv7IVz1knUKqz/Stqpb7C3O4Sh474ds58z3MLfB8GddTDYzgFWYLJ8S1/UQMehvET/5rQIDAQAB";


        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain(getString(R.string.setting_up_problem) + result);

                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(BillingActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                }
            }
        });
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain(getString(R.string.query_inventory_failed) + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            PREMIUM_STATUS = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (PREMIUM_STATUS ? "PREMIUM" : "NOT PREMIUM"));
            saveStatus();
            if (PREMIUM_STATUS) updateUi();



            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain(getString(R.string.another_async_task));
        }
    }

    // User clicked the "Upgrade to Premium" button.
    public void onUpgradeAppButtonClicked() {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain(getString(R.string.error_launching));
        }
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain(getString(R.string.error_purchasing) + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain(getString(R.string.authentity_failed));
                return;
            }

            Log.d(TAG, "Purchase successful.");


             if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert(getString(R.string.thanks_for_premium));
                PREMIUM_STATUS = true;
                saveStatus();
                 updateUi();

            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // very important:
        if (mBroadcastReceiver != null) {
            try {
                unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e){
                Log.d(TAG, e.getMessage());
            }

        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            try {
                mHelper.disposeWhenFinished();
                mHelper = null;
            } catch (Exception e){
                Log.d(TAG, e.getMessage());
            }

        }
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert(getString(R.string.error) + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void onClick(View v) {
        if (!PREMIUM_STATUS){
            if(NetworkStatusChecker.isNetworkAvailable(context.getApplicationContext()) && mHelper != null){

                onUpgradeAppButtonClicked();

            } else {
                complain(StartActivity.getRes().getString(R.string.network_unreachable));
            }
        } else {
            Intent startIntent = new Intent(context, StartActivity.class);
            startActivity(startIntent);
        }

    }

    private void saveStatus(){
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putBoolean(ConstantManager.PREMIUM_STATUS_KEY, PREMIUM_STATUS);

        editor.apply();
    }

    private void initStatus() {
        PREMIUM_STATUS = getSharedPref().getBoolean(ConstantManager.PREMIUM_STATUS_KEY, false);

    }

    public static SharedPreferences getSharedPref() {

        return context.getSharedPreferences("ResumeSharedPref", MODE_PRIVATE);
    }


    private void updateUi(){
        headerTv.setText(R.string.thank_yuo);
        disclaimerTv.setText(R.string.pdf_unblocked);
        purchasePremiumButton.setText(R.string.return_back);
    }
}
