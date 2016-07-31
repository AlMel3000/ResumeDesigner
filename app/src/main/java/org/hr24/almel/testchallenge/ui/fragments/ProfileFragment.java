package org.hr24.almel.testchallenge.ui.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.squareup.picasso.Picasso;

import org.hr24.almel.testchallenge.R;
import org.hr24.almel.testchallenge.ui.StartActivity;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment implements OnRequestSocialPersonCompleteListener, View.OnClickListener{
    private String message = "Need simple social networks integration? Check this lbrary:";
    private String link = "https://github.com/gorbin/ASNE";

    private static final String NETWORK_ID = "NETWORK_ID";
    private SocialNetwork socialNetwork;
    private int networkId;
    private ImageView photo;
    private EditText name, nick,tel, email, bio, skills, languages, hobby, jobPeriod, jobTitle, companyTitle, jobDuty, studyTitle, studyDescription, rating, achievements;
    private Button savePdfButton;
    private Button share;
    private FloatingActionButton mFab;
    private int mCurrentEditMode = 1;
    private List<EditText> mUserInfoViews;


    public static ProfileFragment newInstannce(int id) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(NETWORK_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        networkId = getArguments().containsKey(NETWORK_ID) ? getArguments().getInt(NETWORK_ID) : 0;


        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        photo = (ImageView) rootView.findViewById(R.id.imageView);
        savePdfButton = (Button) rootView.findViewById(R.id.create_pdf_button);
        name = (EditText) rootView.findViewById(R.id.name);
        nick = (EditText) rootView.findViewById(R.id.nick);
        tel = (EditText) rootView.findViewById(R.id.tel);
        email = (EditText) rootView.findViewById(R.id.email);
        bio = (EditText) rootView.findViewById(R.id.bio);
        skills = (EditText) rootView.findViewById(R.id.skills);
        languages = (EditText) rootView.findViewById(R.id.languages);
        hobby = (EditText) rootView.findViewById(R.id.hobby);
        jobPeriod = (EditText) rootView.findViewById(R.id.job_period);
        companyTitle = (EditText) rootView.findViewById(R.id.company_title);
        jobTitle = (EditText) rootView.findViewById(R.id.job_title);
        studyTitle =(EditText) rootView.findViewById(R.id.study_title);
        studyDescription =(EditText) rootView.findViewById(R.id.study_decription);
        rating =(EditText) rootView.findViewById(R.id.rating);
        achievements=(EditText) rootView.findViewById(R.id.achievements);
        jobDuty=(EditText) rootView.findViewById(R.id.job_duty);

        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        share = (Button) rootView.findViewById(R.id.share);

        share.setOnClickListener(shareClick);

        mFab.setOnClickListener(this);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(name);
        mUserInfoViews.add(nick);
        mUserInfoViews.add(tel);
        mUserInfoViews.add(email);
        mUserInfoViews.add(bio);
        mUserInfoViews.add(skills);
        mUserInfoViews.add(languages);
        mUserInfoViews.add(hobby);
        mUserInfoViews.add(jobPeriod);
        mUserInfoViews.add(companyTitle);
        mUserInfoViews.add(jobTitle);
        mUserInfoViews.add(studyTitle);
        mUserInfoViews.add(studyDescription);
        mUserInfoViews.add(rating);
        mUserInfoViews.add(achievements);
        mUserInfoViews.add(jobDuty);


        socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
        socialNetwork.requestCurrentPerson();

        StartActivity.showProgress("Loading social person");
        return rootView;
    }





    @Override
    public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        StartActivity.hideProgress();
        name.setText(socialPerson.name);

        Picasso.with(getContext())
                .load(socialPerson.avatarURL)
                .resize(100, 100)
                .centerCrop()
                .into(photo);


    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        StartActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }



    private View.OnClickListener shareClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder ad = alertDialogInit("Would you like to post Link:", link);
            ad.setPositiveButton("Post link", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Bundle postParams = new Bundle();
                    postParams.putString(SocialNetwork.BUNDLE_LINK, link);
                    socialNetwork.requestPostLink(postParams, message, postingComplete);
                }
            });
            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });
            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    dialog.cancel();
                }
            });
            ad.create().show();
        }
    };

    private OnPostingCompleteListener postingComplete = new OnPostingCompleteListener() {
        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            Toast.makeText(getActivity(), "Sent", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            Toast.makeText(getActivity(), "Error while sending: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    };



    private AlertDialog.Builder alertDialogInit(String title, String message){
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setCancelable(true);
        return ad;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;

        }
    }

    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
                userValue.setTextColor(Color.DKGRAY);

            }
            name.requestFocus();
            savePdfButton.setVisibility(View.GONE);
            share.setVisibility(View.GONE);
            //// TODO: 01.08.16 реализовать возможность выбора фото из галереи/съёмки 
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                userValue.setTextColor(Color.BLACK);


            }
            savePdfButton.setVisibility(View.VISIBLE);
            share.setVisibility(View.VISIBLE);

        }
    }
}