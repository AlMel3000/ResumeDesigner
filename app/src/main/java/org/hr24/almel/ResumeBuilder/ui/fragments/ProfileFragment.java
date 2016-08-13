package org.hr24.almel.ResumeBuilder.ui.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.picasso.Picasso;
import org.hr24.almel.ResumeBuilder.R;
import org.hr24.almel.ResumeBuilder.ui.StartActivity;
import org.hr24.almel.ResumeBuilder.utils.ConstantManager;
import org.hr24.almel.ResumeBuilder.utils.NetworkStatusChecker;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProfileFragment extends Fragment implements OnRequestSocialPersonCompleteListener, View.OnClickListener{

    private int mCurrentEditMode = 1;


    private static final String NETWORK_ID = "NETWORK_ID";
    public static boolean PHOTO_SET = false;
    public static boolean POST_STATUS = false;
    private SocialNetwork socialNetwork;
    private int networkId;
    private int mJobCount = 1;
    private int mStudyCount = 1;

    private ImageView photoImgV;
    private EditText name, nick,tel, email, bio, skills, languages, hobby,
            jobPeriod, jobTitle, companyTitle, jobDuty, studyTitle, studyDescription, rating, achievements,
            jobPeriod2, jobTitle2, companyTitle2, jobDuty2, studyTitle2, studyDescription2, rating2,
            jobPeriod3, jobTitle3, companyTitle3, jobDuty3;
    private Button savePdfButton;
    private Button shareButton;
    private Button viewPdfButton;
    private Button job2AddButton;
    private Button job3AddButton;
    private Button study2AddButton;
    private Button removeJob2Button;
    private Button removeJob3Button;
    private Button removeStudy2Button;
    private Button sharePdfButton;
    private FloatingActionButton mFab;
    private List<EditText> mUserInfoViews;
    private LinearLayout mAddPhotoLinLay, mJobLinLay, mProfilePlaceholder;
    private TextView mAddPhototv;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private CoordinatorLayout mCoordinatorFrame;
    public String skillString;
    public  String languageString;
    public String hobbyString;
    public String dutyString;
    public String dutyString1;
    public String dutyString2;
    public String achievementString;
    private LinearLayout mStudyLinLay;
    View rootView;
    Bitmap bitmapAva = null;
    ScrollView scrollView;
    List<EditText> mUserInfoViewsJob2, mUserInfoViewsJob3, mUserInfoViewsStudy2;



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

        networkId = getArguments().containsKey(NETWORK_ID) ? getArguments().getInt(NETWORK_ID) : 0;


        rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        photoImgV = (ImageView) rootView.findViewById(R.id.imageView);
        mProfilePlaceholder = (LinearLayout) rootView.findViewById(R.id.profile_placeholder);
        mAddPhotoLinLay = (LinearLayout) rootView.findViewById(R.id.add_photo_ll);
        mJobLinLay = (LinearLayout) rootView.findViewById(R.id.job_ll);
        mStudyLinLay = (LinearLayout) rootView.findViewById(R.id.study_ll);

        mAddPhototv  = (TextView) rootView.findViewById(R.id.add_photo);

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
        jobDuty=(EditText) rootView.findViewById(R.id.job_duty);
        studyTitle =(EditText) rootView.findViewById(R.id.study_title);
        studyDescription =(EditText) rootView.findViewById(R.id.study_decription);
        rating =(EditText) rootView.findViewById(R.id.rating);
        achievements=(EditText) rootView.findViewById(R.id.achievements);

        mCoordinatorFrame = (CoordinatorLayout) rootView.findViewById(R.id.frame);

        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        shareButton = (Button) rootView.findViewById(R.id.sharePost);
        savePdfButton = (Button) rootView.findViewById(R.id.create_pdf_button);
        viewPdfButton = (Button) rootView.findViewById(R.id.view);
        Button camButton = (Button) rootView.findViewById(R.id.cam_btn);
        Button galButton = (Button) rootView.findViewById(R.id.gal_btn);
        job2AddButton = (Button) rootView.findViewById(R.id.job_2_add_btn);
        study2AddButton = (Button) rootView.findViewById(R.id.study_2_add_button);
        sharePdfButton = (Button) rootView.findViewById(R.id.sharePdf);

        scrollView = (ScrollView) rootView.findViewById(R.id.sv_main);


        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        mFab.hide();
                        break;
                    case MotionEvent.ACTION_UP:
                        mFab.show();
                        break;

                }
                return false;
            }
        });



        savePdfButton.setOnClickListener(this);
        viewPdfButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        mAddPhotoLinLay.setOnClickListener(this);
        camButton.setOnClickListener(this);
        galButton.setOnClickListener(this);
        job2AddButton.setOnClickListener(this);
        study2AddButton.setOnClickListener(this);
        photoImgV.setOnClickListener(this);
        sharePdfButton.setOnClickListener(this);


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
        mUserInfoViews.add(jobDuty);
        mUserInfoViews.add(studyTitle);
        mUserInfoViews.add(studyDescription);
        mUserInfoViews.add(rating);
        mUserInfoViews.add(achievements);




        if(NetworkStatusChecker.isNetworkAvailable(getContext()) && MainFragment.AUTHORIZATION_STATUS){
        socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
        socialNetwork.requestCurrentPerson();

        StartActivity.showProgress("Загружаем профиль");

        } else if (!NetworkStatusChecker.isNetworkAvailable(getContext()) && MainFragment.AUTHORIZATION_STATUS){
            showSnackbar("Сеть недоступна, не удалось загрузить Ваш профиль");
        }

        initUserFields();

        if (PHOTO_SET) {
            Picasso.with(getContext())
                    .load(loadUserPhoto())
                    .placeholder(R.drawable.ic_add_a_photo_black_24dp)
                    .resize(100, 100)
                    .centerCrop()
                    .into(photoImgV);


        Bitmap sourceBitmap = null;
        try {
            sourceBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), loadUserPhoto());
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        try {
            bitmapAva = rotatePhoto(loadUserPhoto(), sourceBitmap);
        } catch (IOException e) {
            Log.e("BitMapError", e.getMessage());
            Crashlytics.logException(e);
        }
        }

        /*if (savedInstanceState == null) {
            //активити запускается впервые


        } else {


        }*/

        if (mJobCount==2){
            addJob2();
        } else if (mJobCount == 3){
            addJob2();
            addJob3();

        }

        if (mStudyCount==2){
            addStudy2();

        }

        if(PHOTO_SET){
            mAddPhototv.setText("Изменить фото");
        }

        if (MainFragment.AUTHORIZATION_STATUS && !POST_STATUS){
            sharePost();
        }



        return rootView;
    }



    @Override
    public void onPause() {
        super.onPause();
        saveUserFields();
        if (mStudyCount==2){
            saveUserFieldsStudy2();
        }
        if (mJobCount==2){
            saveUserFieldsJob2();
        } else if (mJobCount==3){
            saveUserFieldsJob2();
            saveUserFieldsJob3();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        saveUserFields();
        if (mStudyCount==2){
            saveUserFieldsStudy2();
        }
        if (mJobCount==2){
            saveUserFieldsJob2();
        } else if (mJobCount==3){
            saveUserFieldsJob2();
            saveUserFieldsJob3();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);

                    try {
                        bitmapAva = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mSelectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }

                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == Activity.RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);

                    Bitmap sourceBitmap = null;
                    try {
                       sourceBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mSelectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }

                    try {
                        bitmapAva = rotatePhoto(mSelectedImage, sourceBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }

                }


                }


    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(getContext())
                .load(selectedImage)
                .resize(104, 104)
                .centerCrop()
                .into(photoImgV);



        saveUserPhoto(selectedImage);
        PHOTO_SET = true;

    }




    @Override
    public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        StartActivity.hideProgress();
        if (name.getText() == null) {
            name.setText(socialPerson.name);
        }

    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        StartActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }



    public  void sharePost() {
           Bundle postParams = new Bundle();
           String link = "http://hr24.org";
           postParams.putString(SocialNetwork.BUNDLE_LINK, link);
           String message = "Лучший сервис трудоустройства!";
           socialNetwork.requestPostLink(postParams, message, postingComplete);

        }

    private OnPostingCompleteListener postingComplete = new OnPostingCompleteListener() {
        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            POST_STATUS = true;
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            Log.d("PostError", "Error while sending: " + errorMessage);
        }
    };




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
            case R.id.sharePost:
                if (!MainFragment.AUTHORIZATION_STATUS) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new MainFragment())
                            .commit();
                }
                break;
            case R.id.create_pdf_button:
                if (MainFragment.AUTHORIZATION_STATUS) {
                    createPdf();
                } else if (!MainFragment.AUTHORIZATION_STATUS){

                    Snackbar.make(mCoordinatorFrame, R.string.load_authorization_request, Snackbar.LENGTH_LONG)
                            .setAction("Отправить запись на стену", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .add(R.id.container, new MainFragment())
                                            .commit();
                                }
                            }).show();
                }
                break;
            case R.id.view:
                viewPdf();
                break;
            case R.id.sharePdf:
                sendPdf();
                break;
            case R.id.profile_placeholder:
                mAddPhotoLinLay.setVisibility(View.VISIBLE);
                mAddPhototv.setVisibility(View.GONE);
                break;
            case R.id.imageView:
                mProfilePlaceholder.setVisibility(View.VISIBLE);
                break;
            case R.id.cam_btn:
                loadPhotoFromCamera();
                break;
            case R.id.gal_btn:
                loadPhotoFromGallery();
                break;
            case R.id.job_2_add_btn:
                mJobCount = 2;
               addJob2();

                break;
            case R.id.job_2_remove_button:
                mJobCount = 1;
                saveUserFieldsJob2();
                job2AddButton.setVisibility(View.VISIBLE);
                View jobView = rootView.findViewById(R.id.job1_ll);
                ViewGroup parentJobll = (ViewGroup) jobView.getParent();
                parentJobll.removeView(jobView);



                break;
            case R.id.job_3_add_btn:
                mJobCount = 3;
                addJob3();


                break;
            case R.id.job_3_remove_button:
                mJobCount = 2;
                saveUserFieldsJob3();
                job3AddButton.setVisibility(View.VISIBLE);
                removeJob2Button.setVisibility(View.VISIBLE);

                View jobView2 = rootView.findViewById(R.id.job2_ll);
                ViewGroup parentJobll2 = (ViewGroup) jobView2.getParent();
                parentJobll2.removeView(jobView2);



                break;
            case R.id.study_2_add_button:
                mStudyCount = 2;
                addStudy2();


                break;
            case R.id.study_2_remove_button:

                mStudyCount = 1;
                saveUserFieldsStudy2();
                study2AddButton.setVisibility(View.VISIBLE);
                View studyView = rootView.findViewById(R.id.study1_ll);
                ViewGroup parentStudyll = (ViewGroup) studyView.getParent();
                parentStudyll.removeView(studyView);


                break;
            

        }
    }

    private void addStudy2() {
        study2AddButton.setVisibility(View.GONE);

        LayoutInflater ltInflaterStudy2 = getActivity().getLayoutInflater();
        ltInflaterStudy2.inflate(R.layout.study2, mStudyLinLay, true);

        removeStudy2Button = (Button) rootView.findViewById(R.id.study_2_remove_button);
        removeStudy2Button.setOnClickListener(this);
        studyTitle2 = (EditText) rootView.findViewById(R.id.study_title1);
        studyDescription2 = (EditText) rootView.findViewById(R.id.study_decription1);
        rating2 = (EditText) rootView.findViewById(R.id.rating1);

        mUserInfoViewsStudy2= new ArrayList<>();
        mUserInfoViewsStudy2.add(studyTitle2);
        mUserInfoViewsStudy2.add(studyDescription2);
        mUserInfoViewsStudy2.add(rating2);

        initUserFieldsStudy2();
    }

    private void addJob2() {
        job2AddButton.setVisibility(View.GONE);

        LayoutInflater ltInflaterJob2 = getActivity().getLayoutInflater();
        ltInflaterJob2.inflate(R.layout.job2, mJobLinLay, true);

        jobPeriod2 = (EditText) rootView.findViewById(R.id.job_period1);
        companyTitle2 = (EditText) rootView.findViewById(R.id.company_title1);
        jobTitle2 = (EditText) rootView.findViewById(R.id.job_title1);
        jobDuty2 =(EditText) rootView.findViewById(R.id.job_duty1);

        job3AddButton = (Button) rootView.findViewById(R.id.job_3_add_btn);
        removeJob2Button = (Button) rootView.findViewById(R.id.job_2_remove_button);

        job3AddButton.setOnClickListener(this);
        removeJob2Button.setOnClickListener(this);

        mUserInfoViewsJob2= new ArrayList<>();
        mUserInfoViewsJob2.add(jobPeriod2);
        mUserInfoViewsJob2.add(companyTitle2);
        mUserInfoViewsJob2.add(jobTitle2);
        mUserInfoViewsJob2.add(jobDuty2);

        initUserFieldsJob2();

    }

    private void addJob3() {
        job3AddButton.setVisibility(View.GONE);
        job2AddButton.setVisibility(View.GONE);
        removeJob2Button.setVisibility(View.GONE);
        LayoutInflater ltInflaterJob3 = getActivity().getLayoutInflater();
        ltInflaterJob3.inflate(R.layout.job3, mJobLinLay, true);

        jobPeriod3 = (EditText) rootView.findViewById(R.id.job_period2);
        companyTitle3 = (EditText) rootView.findViewById(R.id.company_title2);
        jobTitle3 = (EditText) rootView.findViewById(R.id.job_title2);
        jobDuty3 =(EditText) rootView.findViewById(R.id.job_duty2);
        removeJob3Button = (Button) rootView.findViewById(R.id.job_3_remove_button);

        removeJob3Button.setOnClickListener(this);

        mUserInfoViewsJob3= new ArrayList<>();
        mUserInfoViewsJob3.add(jobPeriod3);
        mUserInfoViewsJob3.add(companyTitle3);
        mUserInfoViewsJob3.add(jobTitle3);
        mUserInfoViewsJob3.add(jobDuty3);

        initUserFieldsJob3();

    }

    private void viewPdf() {

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/cv/Resume.pdf");
        if(!file.exists()){
            Toast.makeText(getContext(),
                    "Сначала создайте pdf",
                    Toast.LENGTH_SHORT).show();
        } else{
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");

        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(),
                    "Не установлено приложений для просмотра pdf",
                    Toast.LENGTH_SHORT).show();
            Crashlytics.logException(e);
        }
        }
    }

    private void sendPdf() {

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/cv/Resume.pdf");
        if(!file.exists()){
            Toast.makeText(getContext(),
                    "Сначала создайте pdf",
                    Toast.LENGTH_SHORT).show();
        } else{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

            try {
                startActivity(Intent.createChooser(intent, "Выберите куда отправить файл резюме:"));
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(),
                        "Не установлено приложений для отправки файлов. Попробуйте Dropbox например.",
                        Toast.LENGTH_SHORT).show();
                Crashlytics.logException(e);
            }
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

            if (mJobCount==2){
            for (EditText userValueJob2:mUserInfoViewsJob2){
                userValueJob2.setEnabled(true);
                userValueJob2.setFocusable(true);
                userValueJob2.setFocusableInTouchMode(true);
                userValueJob2.setTextColor(Color.DKGRAY);
            }
                job3AddButton.setVisibility(View.VISIBLE);
                removeJob2Button.setVisibility(View.VISIBLE);

            } else if (mJobCount==3){
                for (EditText userValueJob2:mUserInfoViewsJob2){
                    userValueJob2.setEnabled(true);
                    userValueJob2.setFocusable(true);
                    userValueJob2.setFocusableInTouchMode(true);
                    userValueJob2.setTextColor(Color.DKGRAY);
                }
                for (EditText userValueJob3:mUserInfoViewsJob3){
                    userValueJob3.setEnabled(true);
                    userValueJob3.setFocusable(true);
                    userValueJob3.setFocusableInTouchMode(true);
                    userValueJob3.setTextColor(Color.DKGRAY);
                }
                removeJob3Button.setVisibility(View.VISIBLE);

            }

            if (mStudyCount==2){
                for (EditText userValueStudy2:mUserInfoViewsStudy2){
                    userValueStudy2.setEnabled(true);
                    userValueStudy2.setFocusable(true);
                    userValueStudy2.setFocusableInTouchMode(true);
                    userValueStudy2.setTextColor(Color.DKGRAY);
                }
                removeStudy2Button.setVisibility(View.VISIBLE);
            }



            mProfilePlaceholder.setVisibility(View.VISIBLE);
            savePdfButton.setVisibility(View.GONE);
            viewPdfButton.setVisibility(View.GONE);
            shareButton.setVisibility(View.GONE);
            sharePdfButton.setVisibility(View.GONE);


            if (mJobCount ==1){job2AddButton.setVisibility(View.VISIBLE);}

            if (mStudyCount==1){study2AddButton.setVisibility(View.VISIBLE);}

        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                userValue.setTextColor(Color.BLACK);
            }

            if (mJobCount==2){
                for (EditText userValueJob2:mUserInfoViewsJob2){
                    userValueJob2.setEnabled(false);
                    userValueJob2.setFocusable(false);
                    userValueJob2.setFocusableInTouchMode(false);
                    userValueJob2.setTextColor(Color.BLACK);
                }
                saveUserFieldsJob2();
                job3AddButton.setVisibility(View.GONE);
                removeJob2Button.setVisibility(View.GONE);

            } else if (mJobCount==3){
                for (EditText userValueJob2:mUserInfoViewsJob2){
                    userValueJob2.setEnabled(false);
                    userValueJob2.setFocusable(false);
                    userValueJob2.setFocusableInTouchMode(false);
                    userValueJob2.setTextColor(Color.BLACK);
                }
                for (EditText userValueJob3:mUserInfoViewsJob3){
                    userValueJob3.setEnabled(false);
                    userValueJob3.setFocusable(false);
                    userValueJob3.setFocusableInTouchMode(false);
                    userValueJob3.setTextColor(Color.BLACK);
                }
                saveUserFieldsJob2();
                saveUserFieldsJob3();
                removeJob3Button.setVisibility(View.GONE);

            }

            if (mStudyCount==2){
                for (EditText userValueStudy2:mUserInfoViewsStudy2){
                    userValueStudy2.setEnabled(false);
                    userValueStudy2.setFocusable(false);
                    userValueStudy2.setFocusableInTouchMode(false);
                    userValueStudy2.setTextColor(Color.BLACK);
                }
                saveUserFieldsStudy2();
                removeStudy2Button.setVisibility(View.GONE);
            }

            saveUserFields();



            mProfilePlaceholder.setVisibility(View.GONE);
            if (MainFragment.AUTHORIZATION_STATUS){
                viewPdfButton.setVisibility(View.VISIBLE);
                sharePdfButton.setVisibility(View.VISIBLE);
            }
            savePdfButton.setVisibility(View.VISIBLE);

            if (!MainFragment.AUTHORIZATION_STATUS){
                shareButton.setVisibility(View.VISIBLE);
                shareButton.setText("АВТОРИЗОВАТЬСЯ");
            } else {
                shareButton.setVisibility(View.GONE);
            }

            if(PHOTO_SET){
                mAddPhototv.setText("Изменить фото");
            }

            job2AddButton.setVisibility(View.GONE);
            study2AddButton.setVisibility(View.GONE);

        }
    }






    public void createPdf() {



        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {



        skillString = skills.getText().toString();
        languageString = languages.getText().toString();
        hobbyString = hobby.getText().toString();
        dutyString = jobDuty.getText().toString();
        achievementString = achievements.getText().toString();

        final Rectangle[] COLUMNS = {
                new Rectangle(36, 36, 290, 806),
                new Rectangle(305, 36, 559, 806)
        };
        BaseColor hr24blue = new BaseColor(27,168,195);

        Document document = new Document();

        try {


            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cv";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

            File file = new File(dir, "Resume.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter writer =PdfWriter.getInstance(document, fOut);

            document.open();

            PdfContentByte canvas = writer.getDirectContent();

            ColumnText columnLeft = new ColumnText(canvas);
            ColumnText columnRight = new ColumnText(canvas);
            int side_of_the_page = 0;
            int side_of_the_page2 = 1;

            COLUMNS[1].setBackgroundColor(hr24blue);
            canvas.rectangle(COLUMNS[1]);


            columnLeft.setSimpleColumn(COLUMNS[side_of_the_page]);
            columnRight.setSimpleColumn(COLUMNS[side_of_the_page2]);

            columnLeft.setAlignment(Paragraph.ALIGN_CENTER);
            columnRight.setAlignment(Paragraph.ALIGN_CENTER);

           BaseFont droidSans = BaseFont.createFont("/system/fonts/DroidSans.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

            Font headersLeftFont= new Font(droidSans, 16.0f, 1, hr24blue);
            Font headersSmallLeftFont= new Font(droidSans, 14.0f, 1, BaseColor.BLACK);
            Font textLeftFont= new Font(droidSans, 12.0f, 0, BaseColor.BLACK);
            Font textLeftFontGray= new Font(droidSans, 12.0f, 0, BaseColor.GRAY);

            Font nameFont= new Font(droidSans, 18.0f,0, CMYKColor.BLACK);
            Font nickFont= new Font(droidSans, 14.0f,0, CMYKColor.WHITE);
            Font contactsFont= new Font(droidSans, 10.0f,1, CMYKColor.WHITE);
            Font contactsHeaderFont= new Font(droidSans, 10.0f,1, CMYKColor.BLACK);
            Font headersRightFont= new Font(droidSans, 16.0f, 1, BaseColor.BLACK);
            Font skillsFont= new Font(droidSans, 12.0f, 1, BaseColor.WHITE);
            Font skillsBlankRatingFont= new Font(droidSans, 20.0f, 1, BaseColor.WHITE);
            Font skillsRatingFont= new Font(droidSans, 20.0f, 1, BaseColor.BLACK);

            ByteArrayOutputStream streamExp = new ByteArrayOutputStream();
            Bitmap bitmapExp;
            bitmapExp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.exp);
            bitmapExp.compress(Bitmap.CompressFormat.JPEG, 100 , streamExp);
            Image myImgExp = Image.getInstance(streamExp.toByteArray());
            myImgExp.setAlignment(Image.MIDDLE);

            myImgExp.scaleAbsolute(40f, 40f);

            ByteArrayOutputStream streamEdu = new ByteArrayOutputStream();
            Bitmap bitmapEdu;
            bitmapEdu = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.edu);
            bitmapEdu.compress(Bitmap.CompressFormat.JPEG, 100 , streamEdu);
            Image myImgEdu = Image.getInstance(streamEdu.toByteArray());
            myImgEdu.setAlignment(Image.MIDDLE);

            myImgEdu.scaleAbsolute(40f, 40f);

            if(bitmapAva!= null){
               ByteArrayOutputStream streamAva = new ByteArrayOutputStream();
               bitmapAva.compress(Bitmap.CompressFormat.JPEG, 100 , streamAva);
               Image myImgAva = Image.getInstance(streamAva.toByteArray());
               myImgAva.setAlignment(Image.MIDDLE);
               myImgAva.scaleAbsolute(100f, 100f);
               myImgAva.setSpacingBefore(30f);
               columnRight.addElement(myImgAva);


            }else {
               Toast.makeText(getContext(), "Резюме без фото, пока Вы не добавите его из галереи или не сфотографируетесь.", Toast.LENGTH_LONG).show();
           }



            columnLeft.addElement(myImgExp);

            Paragraph header1 = new Paragraph("Опыт работы");
            header1.setFont(headersLeftFont);
            header1.setAlignment(Element.ALIGN_CENTER);
            header1.setSpacingAfter(8f);
            columnLeft.addElement(header1);




                Paragraph companyTitleParagraph = new Paragraph("\u2022" + this.companyTitle.getText().toString());
                companyTitleParagraph.setAlignment(Paragraph.ALIGN_LEFT);
                companyTitleParagraph.setFont(headersSmallLeftFont);
                columnLeft.addElement(companyTitleParagraph);




                Paragraph jobPeriodParagraph = new Paragraph(this.jobPeriod.getText().toString());
                jobPeriodParagraph.setAlignment(Paragraph.ALIGN_LEFT);
                jobPeriodParagraph.setFont(textLeftFontGray);
                jobPeriodParagraph.setSpacingAfter(4f);
                columnLeft.addElement(jobPeriodParagraph);


            Paragraph jobTitleParagraph = new Paragraph(this.jobTitle.getText().toString());
            jobTitleParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            jobTitleParagraph.setFont(textLeftFont);
            jobTitleParagraph.setSpacingAfter(4f);
            columnLeft.addElement(jobTitleParagraph);


            Paragraph jobDutyParagraph = new Paragraph();
            String [] jobDutyProcessed = stringProcessor(dutyString);
            for (String aJobDutyProcessed : jobDutyProcessed) {

                Chunk jobDuty = new Chunk(aJobDutyProcessed.trim());
                jobDutyParagraph.add(jobDuty+"\n");
            }

            jobDutyParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            jobDutyParagraph.setFont(textLeftFont);
            columnLeft.addElement(jobDutyParagraph);

            if (mJobCount ==2){

                Paragraph companyTitleParagraph1 = new Paragraph("\u2022" + companyTitle2.getText().toString());
                companyTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                companyTitleParagraph1.setFont(headersSmallLeftFont);

                Paragraph jobPeriodParagraph1 = new Paragraph(jobPeriod2.getText().toString());
                jobPeriodParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobPeriodParagraph1.setFont(textLeftFontGray);


                Paragraph jobTitleParagraph1 = new Paragraph(jobTitle2.getText().toString());
                jobTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobTitleParagraph1.setFont(textLeftFont);

                columnLeft.addElement(companyTitleParagraph1);

                jobPeriodParagraph1.setSpacingAfter(4f);
                columnLeft.addElement(jobPeriodParagraph1);

                jobTitleParagraph1.setSpacingAfter(4f);
                columnLeft.addElement(jobTitleParagraph1);


                dutyString1 = jobDuty2.getText().toString();

                Paragraph jobDutyParagraph1 = new Paragraph();
                String [] jobDutyProcessed1 = stringProcessor(dutyString1);
                for (String aJobDutyProcessed1 : jobDutyProcessed1) {

                    Chunk jobDuty1 = new Chunk(aJobDutyProcessed1.trim());
                    jobDutyParagraph1.add(jobDuty1+"\n");
                }

                jobDutyParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobDutyParagraph1.setFont(textLeftFont);
                columnLeft.addElement(jobDutyParagraph1);
            }

            if (mJobCount ==3){
                Paragraph companyTitleParagraph1 = new Paragraph("\u2022" + companyTitle2.getText().toString());
                companyTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                companyTitleParagraph1.setFont(headersSmallLeftFont);

                Paragraph jobPeriodParagraph1 = new Paragraph(jobPeriod2.getText().toString());
                jobPeriodParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobPeriodParagraph1.setFont(textLeftFontGray);


                Paragraph jobTitleParagraph1 = new Paragraph(jobTitle2.getText().toString());
                jobTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobTitleParagraph1.setFont(textLeftFont);

                columnLeft.addElement(companyTitleParagraph1);

                jobPeriodParagraph1.setSpacingAfter(4f);
                columnLeft.addElement(jobPeriodParagraph1);

                jobTitleParagraph1.setSpacingAfter(4f);
                columnLeft.addElement(jobTitleParagraph1);


                dutyString1 = jobDuty2.getText().toString();

                Paragraph jobDutyParagraph1 = new Paragraph();
                String [] jobDutyProcessed1 = stringProcessor(dutyString1);
                for (String aJobDutyProcessed1 : jobDutyProcessed1) {

                    Chunk jobDuty1 = new Chunk(aJobDutyProcessed1.trim());
                    jobDutyParagraph1.add(jobDuty1+"\n");
                }

                jobDutyParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobDutyParagraph1.setFont(textLeftFont);
                columnLeft.addElement(jobDutyParagraph1);




                Paragraph companyTitleParagraph2 = new Paragraph("\u2022" + companyTitle3.getText().toString());
                companyTitleParagraph2.setAlignment(Paragraph.ALIGN_LEFT);
                companyTitleParagraph2.setFont(headersSmallLeftFont);

                Paragraph jobPeriodParagraph2 = new Paragraph(jobPeriod3.getText().toString());
                jobPeriodParagraph2.setAlignment(Paragraph.ALIGN_LEFT);
                jobPeriodParagraph2.setFont(textLeftFontGray);


                Paragraph jobTitleParagraph2 = new Paragraph(jobTitle3.getText().toString());
                jobTitleParagraph2.setAlignment(Paragraph.ALIGN_LEFT);
                jobTitleParagraph2.setFont(textLeftFont);

                columnLeft.addElement(companyTitleParagraph2);

                jobPeriodParagraph2.setSpacingAfter(4f);
                columnLeft.addElement(jobPeriodParagraph2);

                jobTitleParagraph2.setSpacingAfter(4f);
                columnLeft.addElement(jobTitleParagraph2);


                dutyString2 = jobDuty3.getText().toString();

                Paragraph jobDutyParagraph2 = new Paragraph();
                String [] jobDutyProcessed2 = stringProcessor(dutyString2);
                for (String aJobDutyProcessed2 : jobDutyProcessed2) {

                    Chunk jobDuty2 = new Chunk(aJobDutyProcessed2.trim());
                    jobDutyParagraph2.add(jobDuty2+"\n");
                }

                jobDutyParagraph2.setAlignment(Paragraph.ALIGN_LEFT);
                jobDutyParagraph2.setFont(textLeftFont);
                columnLeft.addElement(jobDutyParagraph2);
            }


            myImgEdu.setSpacingBefore(20f);
            columnLeft.addElement(myImgEdu);


            Paragraph header2 = new Paragraph("Образование");
            header2.setFont(headersLeftFont);
            header2.setAlignment(Element.ALIGN_CENTER);
            header2.setSpacingAfter(8f);
            columnLeft.addElement(header2);


            Paragraph studyTitleParagraph = new Paragraph("\u2022" + this.studyTitle.getText().toString());
            studyTitleParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            studyTitleParagraph.setFont(headersSmallLeftFont);

            Paragraph studyDescriptionParagraph = new Paragraph(this.studyDescription.getText().toString());
            studyDescriptionParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            studyDescriptionParagraph.setFont(textLeftFont);

            Paragraph studyRatingParagraph = new Paragraph(rating.getText().toString());
            studyRatingParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            studyRatingParagraph.setFont(textLeftFont);

            studyTitleParagraph.setSpacingAfter(4f);
            columnLeft.addElement(studyTitleParagraph);

            studyDescriptionParagraph.setSpacingAfter(4f);
            columnLeft.addElement(studyDescriptionParagraph);

            studyRatingParagraph.setSpacingAfter(4f);
            columnLeft.addElement(studyRatingParagraph);

            if (mStudyCount==2){
                Paragraph studyTitleParagraph1 = new Paragraph("\u2022" + studyTitle2.getText().toString());
                studyTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                studyTitleParagraph1.setFont(headersSmallLeftFont);

                Paragraph studyDescriptionParagraph1 = new Paragraph(studyDescription2.getText().toString());
                studyDescriptionParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                studyDescriptionParagraph1.setFont(textLeftFont);

                Paragraph studyRatingParagraph1 = new Paragraph(rating2.getText().toString());
                studyRatingParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                studyRatingParagraph1.setFont(textLeftFont);

                studyTitleParagraph1.setSpacingAfter(4f);
                columnLeft.addElement(studyTitleParagraph1);

                studyDescriptionParagraph1.setSpacingAfter(4f);
                columnLeft.addElement(studyDescriptionParagraph1);

                studyRatingParagraph1.setSpacingAfter(4f);
                columnLeft.addElement(studyRatingParagraph1);

            }


            Paragraph header3 = new Paragraph("Достижения");
            header3.setFont(headersLeftFont);
            header3.setAlignment(Element.ALIGN_CENTER);
            header3.setSpacingBefore(20f);
            header3.setSpacingAfter(8f);
            columnLeft.addElement(header3);


            Paragraph achievementsParagraph = new Paragraph();
            String [] achievementsProcessed = stringProcessor(achievementString);
            for (String achievement : achievementsProcessed) {

                Chunk achieved = new Chunk(achievement.trim());
                achievementsParagraph.add(achieved+"\n");
            }
            achievementsParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            achievementsParagraph.setFont(textLeftFont);
            columnLeft.addElement(achievementsParagraph);




            Paragraph nameParagraph = new Paragraph(this.name.getText().toString());
            nameParagraph.setAlignment(Paragraph.ALIGN_CENTER);
            nameParagraph.setFont(nameFont);
            nameParagraph.setSpacingBefore(8f);
            nameParagraph.setSpacingAfter(4f);
            columnRight.addElement(nameParagraph);

            Paragraph nick = new Paragraph(this.nick.getText().toString());
            nick.setAlignment(Paragraph.ALIGN_CENTER);
            nick.setFont(nickFont);
            nick.setSpacingAfter(20f);
            columnRight.addElement(nick);

            Paragraph telephoneParagraph = new Paragraph();
            Chunk tChunk = new Chunk("T");
            tChunk.setFont(contactsHeaderFont);
            String tel1 = tel.getText().toString();
            Chunk telChunk = new Chunk("    "+ tel1);
            telChunk.setFont(contactsFont);
            telephoneParagraph.add(tChunk);
            telephoneParagraph.add(telChunk);
            telephoneParagraph.setIndentationLeft(10);
            telephoneParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            columnRight.addElement(telephoneParagraph);

            Paragraph emailParagraph = new Paragraph();
            Chunk eChunk = new Chunk("E");
            eChunk.setFont(contactsHeaderFont);
            String email1 = email.getText().toString();
            Chunk emailChunk = new Chunk("    "+ email1);
            emailChunk.setFont(contactsFont);
            emailParagraph.add(eChunk);
            emailParagraph.add(emailChunk);
            emailParagraph.setIndentationLeft(10);
            emailParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            columnRight.addElement(emailParagraph);

            Paragraph header4 = new Paragraph("О себе");
            header4.setAlignment(Paragraph.ALIGN_LEFT);
            header4.setIndentationLeft(20);
            header4.setFont(headersRightFont);
            header4.setSpacingBefore(40f);
            header4.setSpacingAfter(8f);

            Paragraph bioParagraph = new Paragraph(this.bio.getText().toString());
            bioParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            bioParagraph.setIndentationLeft(10);
            bioParagraph.setFont(textLeftFont);
            columnRight.addElement(header4);
            columnRight.addElement(bioParagraph);

            Paragraph header5 = new Paragraph("Навыки");
            header5.setAlignment(Paragraph.ALIGN_LEFT);
            header5.setFont(headersRightFont);
            header5.setIndentationLeft(20);
            header5.setSpacingBefore(30f);
            header5.setSpacingAfter(8f);
            columnRight.addElement(header5);

            Paragraph skillsParagraph = new Paragraph();
            Chunk skillChunk = new Chunk();


            String [] skillsProcessed = stringProcessor(skillString.trim());
            for (String skill : skillsProcessed) {
                String [] currentSkill = skillsStringProcessor(skill.trim());
                skillChunk = new Chunk(currentSkill[0]+"   ");
                skillChunk.setFont(skillsFont);
                Chunk skillRatingChunk = new Chunk();
                Chunk skillBlankRatingChunk = new Chunk();
                if (currentSkill.length>1) {
                    int skillRating = Integer.parseInt(currentSkill[1]);
                    int skillBlankRating = 5 - skillRating;
                    skillRatingChunk.setFont(skillsRatingFont);
                    for (int i = 0; i < skillRating; i++) {
                        skillRatingChunk.append("\u2022");
                    }
                    skillBlankRatingChunk.setFont(skillsBlankRatingFont);
                    while (skillBlankRating > 0) {
                        skillBlankRatingChunk.append("\u2022");
                        skillBlankRating--;
                    }

                }
                skillsParagraph.add(skillChunk);
                if (currentSkill.length>1)
                    skillsParagraph.add(skillRatingChunk);
                if (currentSkill.length>1)
                    skillsParagraph.add(skillBlankRatingChunk);
                skillsParagraph.add("\n");
            }
            skillsParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            skillsParagraph.setIndentationLeft(10);
            columnRight.addElement(skillsParagraph);


            Paragraph header6 = new Paragraph("Другая информация");
            header6.setAlignment(Paragraph.ALIGN_LEFT);
            header6.setFont(headersRightFont);
            header6.setIndentationLeft(20);header6.setSpacingBefore(30f);
            header6.setSpacingAfter(8f);
            columnRight.addElement(header6);

            Paragraph subHeader1 = new Paragraph("Языки");
            subHeader1.setAlignment(Paragraph.ALIGN_LEFT);
            subHeader1.setFont(skillsFont);
            subHeader1.setIndentationLeft(15);
            subHeader1.setSpacingAfter(4f);
            columnRight.addElement(subHeader1);


            Paragraph languagesParagraph = new Paragraph();
            String [] languagesProcessed = stringProcessor(languageString);
            for (String language : languagesProcessed) {
                Chunk languageChunk = new Chunk(language.trim());
                languagesParagraph.add(languageChunk+"\n");
            }
            languagesParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            languagesParagraph.setFont(textLeftFont);
            languagesParagraph.setSpacingAfter(8f);
            languagesParagraph.setIndentationLeft(10);
            columnRight.addElement(languagesParagraph);

            Paragraph subHeader2 = new Paragraph("Хобби");
            subHeader2.setAlignment(Paragraph.ALIGN_LEFT);
            subHeader2.setFont(skillsFont);
            subHeader2.setIndentationLeft(15);
            subHeader2.setSpacingAfter(4f);
            columnRight.addElement(subHeader2);

            Paragraph hobbyParagraph = new Paragraph();
            String [] hobbyProcessed = stringProcessor(hobbyString);
            for (String hobby : hobbyProcessed) {
                Chunk hobbyChunk = new Chunk(hobby.trim());
                hobbyParagraph.add(hobbyChunk+"\n");
            }
            hobbyParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            hobbyParagraph.setFont(textLeftFont);
            hobbyParagraph.setIndentationLeft(10);
            columnRight.addElement(hobbyParagraph);





            columnLeft.go();
            columnRight.go();

            showSnackbar("Документ создан");



        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
            Crashlytics.logException(de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
            Crashlytics.logException(e);
        }
        finally
        {
            document.close();
        }

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorFrame, R.string.load_from_camera_permissions_request, Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }

    }


    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());
        getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return image;
    }

    private void loadPhotoFromCamera() {

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                showSnackbar("Ошибка получения фото" + e);
                Crashlytics.logException(e);
            }
            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorFrame, R.string.load_from_camera_permissions_request, Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackbar("Разрешение получено");
            }
        }
        if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            showSnackbar("Разрешение получено");
        }
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_choose_picture)), ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorFrame, message,Snackbar.LENGTH_LONG).show();
    }

    private String[] stringProcessor(String string){

        return string.split("//");
    }

    private String[] skillsStringProcessor(String string){

        return string.split("@");
    }

    private void initUserFields() {
        List<String> userFields = new ArrayList<>();
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_NAME_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_NICK_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_PHONE_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_MAIL_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_BIO_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_SKILLS_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_LANGUAGES_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_HOBBY_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_JOB_PERIOD_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_COMPANY_TITLE_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_JOB_TITLE_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_JOB_DUTY_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_STUDY_TITLE_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_STUDY_DESCRIPTION_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_RATING_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_ACHIEVEMENTS_KEY, null));

        for (int i = 0; i < userFields.size(); i++) {
            mUserInfoViews.get(i).setText(userFields.get(i));
        }
        mJobCount = StartActivity.getSharedPref().getInt(ConstantManager.JOB_COUNT_KEY, 1);
        mStudyCount = StartActivity.getSharedPref().getInt(ConstantManager.STUDY_COUNT_KEY, 1);
        PHOTO_SET = StartActivity.getSharedPref().getBoolean(ConstantManager.PHOTO_SET_STATUS_KEY, false);
        POST_STATUS = StartActivity.getSharedPref().getBoolean(ConstantManager.POST_STATUS_KEY, false);

    }

    private void saveUserFields() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }

        SharedPreferences.Editor editor = StartActivity.getSharedPref().edit();
        final String[] USER_FIELDS = {
                ConstantManager.USER_NAME_KEY,
                ConstantManager.USER_NICK_KEY,
                ConstantManager.USER_PHONE_KEY,
                ConstantManager.USER_MAIL_KEY,
                ConstantManager.USER_BIO_KEY,
                ConstantManager.USER_SKILLS_KEY,
                ConstantManager.USER_LANGUAGES_KEY,
                ConstantManager.USER_HOBBY_KEY,
                ConstantManager.USER_JOB_PERIOD_KEY,
                ConstantManager.USER_COMPANY_TITLE_KEY,
                ConstantManager.USER_JOB_TITLE_KEY,
                ConstantManager.USER_JOB_DUTY_KEY,
                ConstantManager.USER_STUDY_TITLE_KEY,
                ConstantManager.USER_STUDY_DESCRIPTION_KEY,
                ConstantManager.USER_RATING_KEY,
                ConstantManager.USER_ACHIEVEMENTS_KEY
        };

        for (int i =0; i<USER_FIELDS.length; i++){
            editor.putString(USER_FIELDS[i], userData.get(i));
        }
        editor.putInt(ConstantManager.JOB_COUNT_KEY, mJobCount);
        editor.putInt(ConstantManager.STUDY_COUNT_KEY, mStudyCount);
        editor.putBoolean(ConstantManager.PHOTO_SET_STATUS_KEY, PHOTO_SET);
        editor.putBoolean(ConstantManager.POST_STATUS_KEY, POST_STATUS );

        editor.apply();
    }

    public Uri loadUserPhoto(){
        return Uri.parse(StartActivity.getSharedPref().getString(ConstantManager.USER_PHOTO_KEY, "android.resource://org.hr24.almel.ResumeBuilder/drawable/ic_add_a_photo_black_24dp.xml"));
    }

    public void saveUserPhoto(Uri uri){
        SharedPreferences.Editor editor = StartActivity.getSharedPref().edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }





    private void saveUserFieldsStudy2() {
        List<String> userData = new ArrayList<>();
        userData.add(studyTitle2.getText().toString());
        userData.add(studyDescription2.getText().toString());
        userData.add(rating2.getText().toString());
        SharedPreferences.Editor editor = StartActivity.getSharedPref().edit();
        final String[] USER_FIELDS = {
                ConstantManager.USER_STUDY_2_TITLE_KEY,
                ConstantManager.USER_STUDY_2_DESCRIPTION_KEY,
                ConstantManager.USER_RATING_2_KEY
        };

        for (int i =0; i<USER_FIELDS.length; i++){
            editor.putString(USER_FIELDS[i], userData.get(i));
        }
        editor.apply();

    }

    private void initUserFieldsStudy2() {
        List<String> userFields = new ArrayList<>();
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_STUDY_2_TITLE_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_STUDY_2_DESCRIPTION_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_RATING_2_KEY, null));

        for (int i = 0; i < userFields.size(); i++) {
            mUserInfoViewsStudy2.get(i).setText(userFields.get(i));
        }

    }


    private void saveUserFieldsJob2() {
        List<String> userData = new ArrayList<>();
        userData.add(jobPeriod2.getText().toString());
        userData.add(companyTitle2.getText().toString());
        userData.add(jobTitle2.getText().toString());
        userData.add(jobDuty2.getText().toString());
        SharedPreferences.Editor editor = StartActivity.getSharedPref().edit();
        final String[] USER_FIELDS = {
                ConstantManager.USER_JOB_2_PERIOD_KEY,
                ConstantManager.USER_COMPANY_2_TITLE_KEY,
                ConstantManager.USER_JOB_2_TITLE_KEY,
                ConstantManager.USER_JOB_2_DUTY_KEY
        };

        for (int i =0; i<USER_FIELDS.length; i++){
            editor.putString(USER_FIELDS[i], userData.get(i));
        }
        editor.apply();
    }

    private void initUserFieldsJob2() {
        List<String> userFields = new ArrayList<>();
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_JOB_2_PERIOD_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_COMPANY_2_TITLE_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_JOB_2_TITLE_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_JOB_2_DUTY_KEY, null));

        for (int i = 0; i < userFields.size(); i++) {
            mUserInfoViewsJob2.get(i).setText(userFields.get(i));
        }

    }





    private void saveUserFieldsJob3() {
        List<String> userData = new ArrayList<>();
        userData.add(jobPeriod3.getText().toString());
        userData.add(companyTitle3.getText().toString());
        userData.add(jobTitle3.getText().toString());
        userData.add(jobDuty3.getText().toString());
        SharedPreferences.Editor editor = StartActivity.getSharedPref().edit();
        final String[] USER_FIELDS = {
                ConstantManager.USER_JOB_3_PERIOD_KEY,
                ConstantManager.USER_COMPANY_3_TITLE_KEY,
                ConstantManager.USER_JOB_3_TITLE_KEY,
                ConstantManager.USER_JOB_3_DUTY_KEY
        };

        for (int i =0; i<USER_FIELDS.length; i++){
            editor.putString(USER_FIELDS[i], userData.get(i));
        }
        editor.apply();
    }

    private void initUserFieldsJob3() {
        List<String> userFields = new ArrayList<>();
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_JOB_3_PERIOD_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_COMPANY_3_TITLE_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_JOB_3_TITLE_KEY, null));
        userFields.add(StartActivity.getSharedPref().getString(ConstantManager.USER_JOB_3_DUTY_KEY, null));


        for (int i = 0; i < userFields.size(); i++) {
            mUserInfoViewsJob3.get(i).setText(userFields.get(i));
        }

    }

    private Bitmap rotatePhoto(Uri uri, Bitmap sourceBitmap) throws IOException {

        ExifInterface exif = new ExifInterface(uri.getPath());
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = exifToDegrees(rotation);Matrix matrix = new Matrix();
        if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);



    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }


}


