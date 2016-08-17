package org.hr24.almel.ResumeBuilder.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.crashlytics.android.Crashlytics;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.vk.sdk.VKScope;
import com.vk.sdk.util.VKUtil;
import org.hr24.almel.ResumeBuilder.R;
import org.hr24.almel.ResumeBuilder.ui.StartActivity;
import org.hr24.almel.ResumeBuilder.billing.IabBroadcastReceiver;
import org.hr24.almel.ResumeBuilder.billing.IabHelper;
import org.hr24.almel.ResumeBuilder.billing.IabResult;
import org.hr24.almel.ResumeBuilder.billing.Inventory;
import org.hr24.almel.ResumeBuilder.billing.Purchase;
import org.hr24.almel.ResumeBuilder.utils.ConstantManager;
import org.hr24.almel.ResumeBuilder.utils.NetworkStatusChecker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.pm.PackageManager.GET_SIGNATURES;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener, View.OnClickListener, IabBroadcastReceiver.IabBroadcastListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static final String TAG = "inAppBilling";

    Button vkButton, fbButton, fillButton, premiumButton;
    CoordinatorLayout mCoordinatorLayout;
    LinearLayout authLinLayout;
    CardView fillView;
    ImageView logoImageView, ukImageView, ruImageView;
    Button mailButton, callButton;
    public static SocialNetworkManager mSocialNetworkManager;
    int networkId = 0;
    public static boolean AUTHORIZATION_STATUS = false;
    public static boolean PREMIUM_STATUS = false;
    public static boolean POST_STATUS = false;

    public static boolean RUSSIAN_LOCALE = true;

    private SocialNetwork currentSocialNetwork;

    // SKUs for our products: the premium upgrade (non-consumable)
    static final String SKU_PREMIUM = "premium";

    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);


        vkButton = (Button) rootView.findViewById(R.id.vk_btn);
        fbButton = (Button) rootView.findViewById(R.id.fb_btn);
        fillButton = (Button) rootView.findViewById(R.id.fill_btn);
        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.main_coordinator_container);
        authLinLayout = (LinearLayout) rootView.findViewById(R.id.auth_ll);
        fillView = (CardView) rootView.findViewById(R.id.fill_v);
        premiumButton = (Button) rootView.findViewById(R.id.premium_btn);
        logoImageView = (ImageView) rootView.findViewById(R.id.logo_iv);
        mailButton = (Button) rootView.findViewById(R.id.mail_btn);
        callButton = (Button) rootView.findViewById(R.id.call_btn);
        ruImageView = (ImageView) rootView.findViewById(R.id.ru_iv);
        ukImageView = (ImageView)  rootView.findViewById(R.id.uk_iv);

        vkButton.setOnClickListener(this);
        fbButton.setOnClickListener(this);
        fillButton.setOnClickListener(this);
        logoImageView.setOnClickListener(this);
        mailButton.setOnClickListener(this);
        callButton.setOnClickListener(this);
        premiumButton.setOnClickListener(this);
        ruImageView.setOnClickListener(this);
        ukImageView.setOnClickListener(this);

        String VK_KEY = getActivity().getString(R.string.vk_app_id);



        // getVkFingerprint();
        //printHashKey();




        initStatus();


        if (!POST_STATUS && !PREMIUM_STATUS) {
            mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(StartActivity.SOCIAL_NETWORK_TAG);

            String[] vkScope = new String[]{
                    VKScope.WALL,
                    VKScope.NOHTTPS
            };


            ArrayList<String> fbScope = new ArrayList<String>();
            fbScope.addAll(Arrays.asList("public_profile, email, user_friends"));


            if (mSocialNetworkManager == null) {
                mSocialNetworkManager = new SocialNetworkManager();

                //Init and add to manager VkSocialNetwork
                VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
                mSocialNetworkManager.addSocialNetwork(vkNetwork);


                FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
                mSocialNetworkManager.addSocialNetwork(fbNetwork);


                //Initiate every network from mSocialNetworkManager
                getFragmentManager().beginTransaction().add(mSocialNetworkManager, StartActivity.SOCIAL_NETWORK_TAG).commit();
                mSocialNetworkManager.setOnInitializationCompleteListener(this);
            } else {
                //if manager exist - get and setup login only for initialized SocialNetworks
                if (!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                    List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                    for (SocialNetwork socialNetwork : socialNetworks) {
                        socialNetwork.setOnLoginCompleteListener(this);
                        initSocialNetwork(socialNetwork);
                    }
                }
            }
        }



        if (POST_STATUS||PREMIUM_STATUS){
           updateUi();
        }

        //region ============ inAppBilling ===========


     /*   *//* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         *//*
        String base64EncodedPublicKey = "CONSTRUCT_YOUR_KEY_AND_PLACE_IT_HERE";


        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(getContext(), base64EncodedPublicKey);


        //// TODO: 14.08.16  enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
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
                mBroadcastReceiver = new IabBroadcastReceiver(MainFragment.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                getContext().registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                    Crashlytics.logException(e);
                }
            }
        });*/

        //endregion

        return rootView;
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, StartActivity.getRes().getString(R.string.query_inventory_finished));

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain(StartActivity.getRes().getString(R.string.query_inventory_failed) + result);
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

            if (PREMIUM_STATUS){updateUi();}
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
            complain(StartActivity.getRes().getString(R.string.another_async_task));
            Crashlytics.logException(e);
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
            mHelper.launchPurchaseFlow(getActivity(), SKU_PREMIUM, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain(StartActivity.getRes().getString(R.string.error_launching));
            Crashlytics.logException(e);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    /** Verifies the developer payload of a purchase. */
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
                complain(StartActivity.getRes().getString(R.string.error_purchasing) + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain(StartActivity.getRes().getString(R.string.authentity_failed));
                return;
            }

            Log.d(TAG, "Purchase successful.");

           if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert(StartActivity.getRes().getString(R.string.thanks_for_premium));
                PREMIUM_STATUS = true;
                updateUi();

            }

        }
    };



    @Override
    public void onPause() {
        super.onPause();
        saveStatus();
        //region ============ inAppBilling ===========

       /* // very important:
        if (mBroadcastReceiver != null) {
            getContext().unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }*/

        //endregion


    }

    public void onDestroy() {
        super.onDestroy();
        saveStatus();
    }

    private void getVkFingerprint() {
        String[] fingerprints = VKUtil.getCertificateFingerprint(getContext(), getActivity().getPackageName());
        for (String fingerprint : fingerprints) {
            Log.d("Fingerprint", fingerprint);
        }
    }

    private void initSocialNetwork(SocialNetwork socialNetwork){
        if(socialNetwork.isConnected()){
            switch (socialNetwork.getID()){
                case VkSocialNetwork.ID:
                    networkId = VkSocialNetwork.ID;

                    break;

                case FacebookSocialNetwork.ID:
                    networkId = FacebookSocialNetwork.ID;
                    break;
            }


        }
    }

    public  void sharePost() {
        try {
            currentSocialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(networkId);
            Bundle postParams = new Bundle();
            String link = "http://hr24.org";
            postParams.putString(SocialNetwork.BUNDLE_LINK, link);
            String message = StartActivity.getRes().getString(R.string.best_service);
            currentSocialNetwork.requestPostLink(postParams, message, postingComplete);
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

    }

    private OnPostingCompleteListener postingComplete = new OnPostingCompleteListener() {
        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            updateUi();
            POST_STATUS = true;
            saveStatus();
            showSnackbar(StartActivity.getRes().getString(R.string.thanks));
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            Log.d("PostError", "Error while sending: " + errorMessage);
            if (errorMessage.trim().equals("ShareDialog canceled")){
                showSnackbar(StartActivity.getRes().getString(R.string.cancelled));
            } else {
                showSnackbar(StartActivity.getRes().getString(R.string.posting_error));
            }
            currentSocialNetwork = null;
        }
    };

    private void updateUi() {
        authLinLayout.setVisibility(View.GONE);
        fillView.setVisibility(View.GONE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.vk_btn:
                if(NetworkStatusChecker.isNetworkAvailable(getContext())){

                    networkId = VkSocialNetwork.ID;
                    SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);

                    if(networkId != 0) {
                        socialNetwork.requestLogin();
                        StartActivity.showProgress(StartActivity.getRes().getString(R.string.loading_social));
                    } else {
                        showSnackbar(StartActivity.getRes().getString(R.string.wrong_id));
                    }

                } else {
                    showSnackbar(StartActivity.getRes().getString(R.string.network_unreachable));
                }

                break;

            case R.id.fb_btn:
                if(NetworkStatusChecker.isNetworkAvailable(getContext())){
                networkId = FacebookSocialNetwork.ID;
                    SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);

                    if (AUTHORIZATION_STATUS){
                        sharePost();
                    } else {
                    if(networkId != 0) {
                        socialNetwork.requestLogin();
                        StartActivity.showProgress(StartActivity.getRes().getString(R.string.loading_social));
                    } else {
                        showSnackbar(StartActivity.getRes().getString(R.string.wrong_id));
                    }}
                } else {
                    showSnackbar(StartActivity.getRes().getString(R.string.network_unreachable));
                }
                break;
            case R.id.fill_btn:
                if (!AUTHORIZATION_STATUS) {
                    startProfile(0);
                } else {
                    startProfile(networkId);

                }
                break;
            case R.id.premium_btn:
                // onUpgradeAppButtonClicked();
                PREMIUM_STATUS = true;
                saveStatus();
                updateUi();
                showSnackbar(StartActivity.getRes().getString(R.string.premium_snackbar));

                break;
            case R.id.logo_iv:
                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.setData(Uri.parse("http://hr24.org"));
                startActivity(viewIntent);
                break;

            case R.id.mail_btn:
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("mailto:andrei@hr24.org"));
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, StartActivity.getRes().getString(R.string.professional_resume));
                startActivity(sendIntent);

                break;

            case R.id.call_btn:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+ 7 925 721 21 68"));
                startActivity(callIntent);


                break;

            case R.id.uk_iv:
                RUSSIAN_LOCALE = false;

                break;

            case R.id.ru_iv:
                RUSSIAN_LOCALE = true;

                break;

        }





    }



    @Override
    public void onSocialNetworkManagerInitialized() {
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);

        }

    }

    @Override
    public void onLoginSuccess(int socialNetworkID) {
        StartActivity.hideProgress();


        AUTHORIZATION_STATUS = true;
        saveStatus();
        sharePost();




    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        StartActivity.hideProgress();

    }

    private void startProfile(int networkId){
        ProfileFragment profile = ProfileFragment.newInstannce(networkId);
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack("profile")
                .replace(R.id.container, profile)
                .commit();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message,Snackbar.LENGTH_LONG).show();
    }

    private void saveStatus(){
        SharedPreferences.Editor editor = StartActivity.getSharedPref().edit();
        editor.putBoolean(ConstantManager.AUTHORIZATION_STATUS_KEY, AUTHORIZATION_STATUS);
        editor.putBoolean(ConstantManager.PREMIUM_STATUS_KEY, PREMIUM_STATUS);
        editor.putBoolean(ConstantManager.POST_STATUS_KEY, POST_STATUS );
        editor.apply();
    }

    private void initStatus() {
        AUTHORIZATION_STATUS = StartActivity.getSharedPref().getBoolean(ConstantManager.AUTHORIZATION_STATUS_KEY, false);
        PREMIUM_STATUS = StartActivity.getSharedPref().getBoolean(ConstantManager.PREMIUM_STATUS_KEY, false);
        POST_STATUS = StartActivity.getSharedPref().getBoolean(ConstantManager.POST_STATUS_KEY, false);

    }

    void complain(String message) {
        Log.e(TAG,  message);
        alert(StartActivity.getRes().getString(R.string.error) + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getContext());
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    public void printHashKey() {
        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo("org.hr24.almel.ResumeBuilder",
                    GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("HASH KEY:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("HASH KEY:", "NameNotFoundException "+ e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.d("HASH KEY:", "NoSuchAlgorithmException "+ e.getMessage());
        }
    }


}
