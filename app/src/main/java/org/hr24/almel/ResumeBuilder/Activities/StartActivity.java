package org.hr24.almel.ResumeBuilder.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.hr24.almel.ResumeBuilder.R;

import org.hr24.almel.ResumeBuilder.ui.fragments.MainFragment;

public class StartActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    private static ProgressDialog pd;
    static Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_start);


        context = this;


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
            }



    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try{
            super.onRestoreInstanceState(savedInstanceState);
        }catch (Exception e) {

            Crashlytics.log("onRestoreInstanceState ViewGroup Error" + e.getMessage());
        }
    }





    public static void showProgress(String message) {
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(message);
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(true);
        pd.show();
    }

    public static void hideProgress() {
        pd.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static SharedPreferences getSharedPref() {

        return context.getSharedPreferences("ResumeSharedPref", MODE_PRIVATE);
    }

    public static Resources getRes() {

        return context.getResources();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Выйти из приложения?")
                .setMessage("Вы действительно хотите выйти?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        StartActivity.super.onBackPressed();
                    }
                }).create().show();
    }






}