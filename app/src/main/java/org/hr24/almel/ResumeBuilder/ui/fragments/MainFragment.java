package org.hr24.almel.ResumeBuilder.ui.fragments;

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
import android.widget.TextView;

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
import org.hr24.almel.ResumeBuilder.Activities.BillingActivity;
import org.hr24.almel.ResumeBuilder.Activities.StartActivity;
import org.hr24.almel.ResumeBuilder.utils.ConstantManager;
import org.hr24.almel.ResumeBuilder.utils.NetworkStatusChecker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.GET_SIGNATURES;

/**
 * A simple {@link Fragment} subclass.
 * ui.Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener, View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    Button vkButton, fbButton, fillButton, premiumButton ,mailButton, callButton;
    TextView fillResumeTv, unblockPdfTv,sharePostTv, orLowerCaseTv, orUpperCaseTv, purchasePremiumTv;


    CoordinatorLayout mCoordinatorLayout;
    LinearLayout authLinLayout;
    CardView fillView;
    ImageView logoImageView, ukImageView, ruImageView;
    public static SocialNetworkManager mSocialNetworkManager;
    int networkId = 0;
    public static boolean AUTHORIZATION_STATUS = false;
    public static boolean PREMIUM_STATUS = false;
    public static boolean POST_STATUS = false;
    StartActivity startActivity =(StartActivity) getActivity();

    public static String APP_LOCALE;
    private Locale mNewLocale;

    private SocialNetwork currentSocialNetwork;




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
        mailButton = (Button) rootView.findViewById(R.id.mail_btn);
        callButton = (Button) rootView.findViewById(R.id.call_btn);
        premiumButton = (Button) rootView.findViewById(R.id.premium_btn);
        fillResumeTv = (TextView) rootView.findViewById(R.id.fill_resume_tv);
        unblockPdfTv = (TextView) rootView.findViewById(R.id.unblock_pdf_tv);
        sharePostTv = (TextView) rootView.findViewById(R.id.share_post_tv);
        orLowerCaseTv = (TextView) rootView.findViewById(R.id.or_lower_case_tv);
        orUpperCaseTv = (TextView) rootView.findViewById(R.id.or_upper_case_tv);
        purchasePremiumTv = (TextView) rootView.findViewById(R.id.purchase_premium_tv);

        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.main_coordinator_container);
        authLinLayout = (LinearLayout) rootView.findViewById(R.id.auth_ll);
        fillView = (CardView) rootView.findViewById(R.id.fill_v);
        logoImageView = (ImageView) rootView.findViewById(R.id.logo_iv);
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



        getVkFingerprint();
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


        return rootView;
    }








    @Override
    public void onPause() {
        super.onPause();
        saveStatus();



    }

    public void onDestroy() {
        super.onDestroy();
        saveStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PREMIUM_STATUS){
            updateUi();
        }
    }

    private void getVkFingerprint() {
        String[] fingerprints = VKUtil.getCertificateFingerprint(getContext(), getActivity().getPackageName());
        for (String fingerprint : fingerprints) {
            Log.d("Fingerprint", fingerprint);
            Crashlytics.log(fingerprint);

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

                   try{ networkId = VkSocialNetwork.ID;
                    SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);

                    if(networkId != 0) {
                        socialNetwork.requestLogin();
                        StartActivity.showProgress(StartActivity.getRes().getString(R.string.loading_social));
                    } else {
                        showSnackbar(StartActivity.getRes().getString(R.string.wrong_id));
                    }
                   } catch (Exception e){
                       Crashlytics.logException(e);
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
                        try {
                            socialNetwork.requestLogin();
                        } catch (Exception e){
                            Crashlytics.logException(e);
                        }

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
                if(NetworkStatusChecker.isNetworkAvailable(getContext())){

                        Intent billingIntent = new Intent(getContext(), BillingActivity.class);
                        billingIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(billingIntent);

                    } else {
                        showSnackbar(StartActivity.getRes().getString(R.string.network_unreachable));
                    }






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
                startActivity(Intent.createChooser(callIntent, getString(R.string.chose_an_app_to_call)));


                break;

            case R.id.uk_iv:
                setLocale("en");
                APP_LOCALE = StartActivity.getRes().getConfiguration().locale.getDisplayName();
                Log.d("LOCALE", APP_LOCALE);

                break;

            case R.id.ru_iv:
                setLocale("ru");
                APP_LOCALE = StartActivity.getRes().getConfiguration().locale.getDisplayName();
                Log.d("LOCALE", APP_LOCALE);

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

    void setLocale(String mLang) {
        mNewLocale = new Locale(mLang);
        Locale.setDefault(mNewLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = mNewLocale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        updateLocale();
    }

    private void updateLocale(){

        fillButton.setText(StartActivity.getRes().getString(R.string.fill_tv ));
        mailButton.setText(StartActivity.getRes().getString(R.string.hr ));
        callButton.setText(StartActivity.getRes().getString(R.string.reedem_consultation ));
        premiumButton.setText(StartActivity.getRes().getString(R.string.premium ));
        fillResumeTv.setText(StartActivity.getRes().getString(R.string.auth_fill_tv ));
        unblockPdfTv.setText(StartActivity.getRes().getString(R.string.auth_full_func_tv ));
        sharePostTv.setText(StartActivity.getRes().getString(R.string.auth_enter_tv ));
        orLowerCaseTv.setText(StartActivity.getRes().getString(R.string.or_lowercase_tv ));
        orUpperCaseTv.setText(StartActivity.getRes().getString(R.string.or_tv ));
        purchasePremiumTv.setText(StartActivity.getRes().getString(R.string.auth_premium_tv ));

    }






}
