package org.hr24.almel.testchallenge.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
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

import org.hr24.almel.testchallenge.R;
import org.hr24.almel.testchallenge.ui.StartActivity;


public class ProfileFragment extends Fragment implements OnRequestSocialPersonCompleteListener{
    private String message = "Need simple social networks integration? Check this lbrary:";
    private String link = "https://github.com/gorbin/ASNE";

    private static final String NETWORK_ID = "NETWORK_ID";
    private SocialNetwork socialNetwork;
    private int networkId;
    private ImageView photo;
    private EditText name;
    private Button savePdfButton;
    private Button share;


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

        share = (Button) rootView.findViewById(R.id.share);
        share.setOnClickListener(shareClick);


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
}