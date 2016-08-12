package org.hr24.almel.ResumeBuilder.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.odnoklassniki.OkSocialNetwork;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.vk.sdk.VKScope;
import com.vk.sdk.util.VKUtil;

import org.hr24.almel.ResumeBuilder.R;
import org.hr24.almel.ResumeBuilder.ui.StartActivity;
import org.hr24.almel.ResumeBuilder.utils.ConstantManager;
import org.hr24.almel.ResumeBuilder.utils.NetworkStatusChecker;

import java.util.List;

import ru.ok.android.sdk.util.OkScope;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener, View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button vkButton, okButton, fillButton;
    CoordinatorLayout mCoordinatorLayout;
    LinearLayout authLinLayout;
    View fillView;
    public static SocialNetworkManager mSocialNetworkManager;
    int networkId = 0;
    public static boolean AUTHORIZATION_STATUS = false;


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
        okButton = (Button) rootView.findViewById(R.id.ok_btn);
        fillButton = (Button) rootView.findViewById(R.id.fill_btn);
        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.main_coordinator_container);
        authLinLayout = (LinearLayout) rootView.findViewById(R.id.auth_ll);
        fillView = rootView.findViewById(R.id.fill_v);

        vkButton.setOnClickListener(this);
        okButton.setOnClickListener(this);
        fillButton.setOnClickListener(this);

        String VK_KEY = getActivity().getString(R.string.vk_app_id);
        String OK_APP_ID = getActivity().getString(R.string.ok_app_id);
        String OK_PUBLIC_KEY = getActivity().getString(R.string.ok_public_key);
        String OK_SECRET_KEY = getActivity().getString(R.string.ok_secret_key);

        // getVkFingerprint();

        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(StartActivity.SOCIAL_NETWORK_TAG);

        String[] vkScope = new String[] {
                VKScope.WALL,
                VKScope.NOHTTPS,
                VKScope.STATUS,
        };


        String[] okScope = new String[] {
                OkScope.VALUABLE_ACCESS,
                OkScope.SET_STATUS
        };



        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager VkSocialNetwork
            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
            mSocialNetworkManager.addSocialNetwork(vkNetwork);

            //Init and add to manager OkSocialNetwork
            OkSocialNetwork okNetwork = new OkSocialNetwork(this, OK_APP_ID, OK_PUBLIC_KEY, OK_SECRET_KEY, okScope);
            mSocialNetworkManager.addSocialNetwork(okNetwork);

            //Initiate every network from mSocialNetworkManager
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, StartActivity.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if(!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }

        initAuthorizationStatus();

        if (AUTHORIZATION_STATUS){
            authLinLayout.setVisibility(View.GONE);
            fillView.setVisibility(View.GONE);
        }



        return rootView;
    }

    private void getVkFingerprint() {
        String[] fingerprints = VKUtil.getCertificateFingerprint(getContext(), getActivity().getPackageName());
        for (int i =0; i<fingerprints.length; i++){
            Log.d("Fingerprint", fingerprints[i]);
        }
    }

    private void initSocialNetwork(SocialNetwork socialNetwork){
        if(socialNetwork.isConnected()){
            switch (socialNetwork.getID()){
                case VkSocialNetwork.ID:
                    networkId = VkSocialNetwork.ID;

                    break;
                case OkSocialNetwork.ID:
                    networkId = OkSocialNetwork.ID;
                    break;
            }

            authLinLayout.setVisibility(View.GONE);
            fillView.setVisibility(View.GONE);
            AUTHORIZATION_STATUS = true;
            saveAuthorizationStatus();


        }
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
                        StartActivity.showProgress("Загружаем социальную сеть");
                    } else {
                        showSnackbar("Wrong networkId");
                    }

                } else {
                    showSnackbar("Сеть недоступна, попробуйте позже");
                }

                break;
            case R.id.ok_btn:
                if(NetworkStatusChecker.isNetworkAvailable(getContext())){
                networkId = OkSocialNetwork.ID;
                    SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);

                    if(networkId != 0) {
                        socialNetwork.requestLogin();
                        StartActivity.showProgress("Загружаем социальную сеть");
                    } else {
                        showSnackbar("Wrong networkId");
                    }
                } else {
                    showSnackbar("Сеть недоступна, попробуйте позже");
                }
                break;
            case R.id.fill_btn:
                if (!AUTHORIZATION_STATUS) {
                    startProfile(VkSocialNetwork.ID);
                } else {
                    startProfile(networkId);

                }

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
        showSnackbar("Успешная авторизация");
        authLinLayout.setVisibility(View.GONE);
        fillView.setVisibility(View.GONE);
        AUTHORIZATION_STATUS = true;
        saveAuthorizationStatus();



    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        StartActivity.hideProgress();
        showSnackbar("Ошибка: " + errorMessage);

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

    private void saveAuthorizationStatus(){
        SharedPreferences.Editor editor = StartActivity.getSharedPref().edit();
        editor.putBoolean(ConstantManager.AUTHORIZATION_STATUS_KEY, AUTHORIZATION_STATUS);
        editor.apply();
    }

    private void initAuthorizationStatus() {
        AUTHORIZATION_STATUS = StartActivity.getSharedPref().getBoolean(ConstantManager.AUTHORIZATION_STATUS_KEY, false);

    }
}
