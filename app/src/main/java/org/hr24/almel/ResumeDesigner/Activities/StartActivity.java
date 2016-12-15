package org.hr24.almel.ResumeDesigner.Activities;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKWallPostResult;

import io.fabric.sdk.android.Fabric;
import org.hr24.almel.ResumeDesigner.R;

import org.hr24.almel.ResumeDesigner.utils.ConstantManager;
import org.hr24.almel.ResumeDesigner.utils.NetworkStatusChecker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import static android.content.pm.PackageManager.GET_SIGNATURES;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {



    static Context context;
    Button vkButton, fbButton, fillButton, premiumButton ,mailButton, callButton;
    TextView fillResumeTv, unblockPdfTv,sharePostTv, orLowerCaseTv, orUpperCaseTv, purchasePremiumTv;


    CoordinatorLayout mCoordinatorLayout;
    LinearLayout authLinLayout;
    CardView fillView;
    ImageView logoImageView, ukImageView, ruImageView;
    public static boolean AUTHORIZATION_STATUS = false;
    public static boolean PREMIUM_STATUS = false;
    public static boolean POST_STATUS = false;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    public static String APP_LOCALE;
    private Locale mNewLocale;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatus();
        if (!POST_STATUS && !PREMIUM_STATUS) {

            VKSdk.initialize(getApplicationContext());
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
        }
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_start);


        context = this;

        vkButton = (Button) findViewById(R.id.vk_btn);
        fbButton = (Button) findViewById(R.id.fb_btn);

        fillButton = (Button) findViewById(R.id.fill_btn);
        mailButton = (Button) findViewById(R.id.mail_btn);
        callButton = (Button) findViewById(R.id.call_btn);
        premiumButton = (Button) findViewById(R.id.premium_btn);
        fillResumeTv = (TextView) findViewById(R.id.fill_resume_tv);
        unblockPdfTv = (TextView) findViewById(R.id.unblock_pdf_tv);
        sharePostTv = (TextView) findViewById(R.id.share_post_tv);
        orLowerCaseTv = (TextView) findViewById(R.id.or_lower_case_tv);
        orUpperCaseTv = (TextView) findViewById(R.id.or_upper_case_tv);
        purchasePremiumTv = (TextView) findViewById(R.id.purchase_premium_tv);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        authLinLayout = (LinearLayout) findViewById(R.id.auth_ll);
        fillView = (CardView) findViewById(R.id.fill_v);
        logoImageView = (ImageView) findViewById(R.id.logo_iv);
        ruImageView = (ImageView) findViewById(R.id.ru_iv);
        ukImageView = (ImageView)  findViewById(R.id.uk_iv);





        vkButton.setOnClickListener(this);
        fbButton.setOnClickListener(this);
        fillButton.setOnClickListener(this);
        logoImageView.setOnClickListener(this);
        mailButton.setOnClickListener(this);
        callButton.setOnClickListener(this);
        premiumButton.setOnClickListener(this);
        ruImageView.setOnClickListener(this);
        ukImageView.setOnClickListener(this);




        //getVkFingerprint();
        //printHashKey();


        if (POST_STATUS||PREMIUM_STATUS){
            updateUi();
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
// Пользователь успешно авторизовался
                makePost(getString(R.string.best_service));
            }
            @Override
            public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                showSnackbar(getString(R.string.posting_error));
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    void makePost(String msg) {
        VKParameters parameters = new VKParameters();
        parameters.put(VKApiConst.MESSAGE, msg);
        parameters.put(VKApiConst.ATTACHMENTS, "http://hr24.org");
        VKRequest post = VKApi.wall().post(parameters);
        post.setModelClass(VKWallPostResult.class);
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                updateUi();
                POST_STATUS = true;
                saveStatus();
                showSnackbar(getString(R.string.thanks));
                // post was added
            }
            @Override
            public void onError(VKError error) {

                showSnackbar(getString(R.string.posting_error));
                // error
            }
        });
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

    /*private void getVkFingerprint() {
        String[] fingerprints = VKUtil.getCertificateFingerprint(getContext(), getActivity().getPackageName());
        for (String fingerprint : fingerprints) {
            Log.d("Fingerprint", fingerprint);

           *//* AlertDialog.Builder bld = new AlertDialog.Builder(getContext());
            bld.setMessage(fingerprint);
            bld.setNeutralButton("OK", null);
            bld.create().show();*//*

        }
    }*/



    public  void sharePost() {
        try {
            shareDialog = new ShareDialog(this);
            // this part is optional
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    updateUi();
                    POST_STATUS = true;
                    saveStatus();
                    showSnackbar(getString(R.string.thanks));

                }

                @Override
                public void onCancel() {


                    showSnackbar(getString(R.string.cancelled));




                }

                @Override
                public void onError(FacebookException error) {
                    showSnackbar(getString(R.string.posting_error));

                }

            });

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(getString(R.string.best_service))
                        .setContentUrl(Uri.parse("http://hr24.org"))
                        .build();

                shareDialog.show(linkContent);
            }
        } catch (Exception e) {

            Crashlytics.logException(e);
        }

    }



    private void updateUi() {
        authLinLayout.setVisibility(View.GONE);
        fillView.setVisibility(View.GONE);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.vk_btn:
                if(NetworkStatusChecker.isNetworkAvailable(getApplicationContext())){

                    String [] scope = {"offline", "wall"};
                    VKSdk.login(this, scope);

                } else {
                    showSnackbar(getString(R.string.network_unreachable));
                }

                break;

            case R.id.fb_btn:
                if(NetworkStatusChecker.isNetworkAvailable(getApplicationContext())){
                    sharePost();
                } else {
                    showSnackbar(getString(R.string.network_unreachable));
                }
                break;
            case R.id.fill_btn:
                if (!AUTHORIZATION_STATUS) {
                    startProfile();
                } else {
                    startProfile();

                }
                break;
            case R.id.premium_btn:
                if(NetworkStatusChecker.isNetworkAvailable(getApplicationContext())){

                    Intent billingIntent = new Intent(getApplicationContext(), BillingActivity.class);
                    billingIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(billingIntent);

                } else {
                    showSnackbar(getString(R.string.network_unreachable));
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
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.professional_resume));

                try {
                    startActivity(sendIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            R.string.apps_are_not_installed_to_send_mail,
                            Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.call_btn:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+ 7 925 721 21 68"));
                startActivity(Intent.createChooser(callIntent, getString(R.string.chose_an_app_to_call)));


                break;

            case R.id.uk_iv:
                setLocale("en");
                APP_LOCALE = getResources().getConfiguration().locale.getDisplayName();
                Log.d("LOCALE", APP_LOCALE);
                Toast.makeText(getApplicationContext(), R.string.english_cv, Toast.LENGTH_LONG).show();

                break;

            case R.id.ru_iv:
                setLocale("ru");
                APP_LOCALE = getResources().getConfiguration().locale.getDisplayName();
                Log.d("LOCALE", APP_LOCALE);
                Toast.makeText(getApplicationContext(), R.string.russian_cv, Toast.LENGTH_LONG).show();

                break;

        }





    }


    private void startProfile(){
        Intent startIntent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(startIntent);
    }



    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorLayout, message,Snackbar.LENGTH_LONG).show();
    }

    private void saveStatus(){
        SharedPreferences.Editor editor = getSharedPreferences("ResumeSharedPref", MODE_PRIVATE).edit();
        editor.putBoolean(ConstantManager.AUTHORIZATION_STATUS_KEY, AUTHORIZATION_STATUS);
        editor.putBoolean(ConstantManager.PREMIUM_STATUS_KEY, PREMIUM_STATUS);
        editor.putBoolean(ConstantManager.POST_STATUS_KEY, POST_STATUS );
        editor.apply();
    }

    private void initStatus() {
        AUTHORIZATION_STATUS = getSharedPreferences("ResumeSharedPref", MODE_PRIVATE).getBoolean(ConstantManager.AUTHORIZATION_STATUS_KEY, false);
        PREMIUM_STATUS = getSharedPreferences("ResumeSharedPref", MODE_PRIVATE).getBoolean(ConstantManager.PREMIUM_STATUS_KEY, false);
        POST_STATUS = getSharedPreferences("ResumeSharedPref", MODE_PRIVATE).getBoolean(ConstantManager.POST_STATUS_KEY, false);

    }


    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("org.hr24.almel.ResumeDesigner",
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

        fillButton.setText(getString(R.string.fill_tv ));
        mailButton.setText(getString(R.string.hr ));
        callButton.setText(getString(R.string.reedem_consultation ));
        premiumButton.setText(getString(R.string.premium ));
        fillResumeTv.setText(getString(R.string.auth_fill_tv ));
        unblockPdfTv.setText(getString(R.string.auth_full_func_tv ));
        sharePostTv.setText(getString(R.string.auth_enter_tv ));
        orLowerCaseTv.setText(getString(R.string.or_lowercase_tv ));
        orUpperCaseTv.setText(getString(R.string.or_tv ));
        purchasePremiumTv.setText(getString(R.string.auth_premium_tv ));

    }










    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try{
            super.onRestoreInstanceState(savedInstanceState);
        }catch (Exception e) {

            Crashlytics.log("onRestoreInstanceState ViewGroup Error" + e.getMessage());
        }
    }

    public  SharedPreferences getSharedPref() {

        return getSharedPreferences("ResumeSharedPref", MODE_PRIVATE);
    }

    public static   Resources getRes() {

        return context.getResources();
    }





}