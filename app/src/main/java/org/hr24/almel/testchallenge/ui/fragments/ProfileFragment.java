package org.hr24.almel.testchallenge.ui.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import org.hr24.almel.testchallenge.R;
import org.hr24.almel.testchallenge.ui.StartActivity;
import org.hr24.almel.testchallenge.utils.ConstantManager;
import org.hr24.almel.testchallenge.utils.NetworkStatusChecker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProfileFragment extends Fragment implements OnRequestSocialPersonCompleteListener, View.OnClickListener{
    private String message = "Лучший сервис трудоустройства!";
    private String link = "http://hr24.org";

    private static final String NETWORK_ID = "NETWORK_ID";
    public static boolean POST_STATUS = false;
    private SocialNetwork socialNetwork;
    private int networkId;
    private int mJobCount = 1;
    private int mStudyCount = 1;

    private ImageView photo, photoPlaceholder;
    private EditText name, nick,tel, email, bio, skills, languages, hobby,
            jobPeriod, jobTitle, companyTitle, jobDuty, studyTitle, studyDescription, rating, achievements,
            jobPeriod1, jobTitle1, companyTitle1, jobDuty1,  studyTitle1, studyDescription1, rating1,
            jobPeriod2, jobTitle2, companyTitle2, jobDuty2;
    private Button savePdfButton;
    private Button shareButton;
    private Button viewPdfButton;
    private Button jobAddButton;
    private Button jobAddButton2;
    private Button studyAddButton;
    private Button removeJobButton;
    private Button removeJobButton1;
    private Button removeStudyButton;
    private FloatingActionButton mFab;
    private int mCurrentEditMode = 1;
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

        photo = (ImageView) rootView.findViewById(R.id.imageView);
        photoPlaceholder = (ImageView) rootView.findViewById(R.id.photo_placeholder);
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
        shareButton = (Button) rootView.findViewById(R.id.share);
        savePdfButton = (Button) rootView.findViewById(R.id.create_pdf_button);
        viewPdfButton = (Button) rootView.findViewById(R.id.view);
        Button camButton = (Button) rootView.findViewById(R.id.cam_btn);
        Button galButton = (Button) rootView.findViewById(R.id.gal_btn);
        jobAddButton = (Button) rootView.findViewById(R.id.job_add_btn);
        studyAddButton = (Button) rootView.findViewById(R.id.study_add_button);

        savePdfButton.setOnClickListener(this);
        viewPdfButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        mAddPhotoLinLay.setOnClickListener(this);
        camButton.setOnClickListener(this);
        galButton.setOnClickListener(this);
        jobAddButton.setOnClickListener(this);
        studyAddButton.setOnClickListener(this);
        photo.setOnClickListener(this);

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



        if(NetworkStatusChecker.isNetworkAvailable(getContext())){
        socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
        socialNetwork.requestCurrentPerson();

        StartActivity.showProgress("Loading social person");
        } else {
            showSnackbar("Сеть недоступна, не удалось загрузить Ваш профиль");
        }

        return rootView;
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
                    }

                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == Activity.RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);

                    }


                }


    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(getContext())
                .load(selectedImage)
                .resize(256, 112)
                .centerCrop()
                .into(photo);

        Picasso.with(getContext())
                .load(selectedImage)
                .resize(256, 112)
                .centerCrop()
                .into(photoPlaceholder);


    }




    @Override
    public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        StartActivity.hideProgress();
        name.setText(socialPerson.name);

        if (socialPerson.avatarURL!=null) {
            Picasso.with(getContext())
                    .load(socialPerson.avatarURL)
                    .resize(100, 100)
                    .centerCrop()
                    .into(photo);
        } else {
                Picasso.with(getContext())
                        .load(R.drawable.ic_add_a_photo_black_24dp)
                        .resize(100, 100)
                        .centerCrop()
                        .into(photo);
        }


    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        StartActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }



    private  void shareClick() {
        
        
            AlertDialog.Builder ad = alertDialogInit("Отправить запись на стену?", link);
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

    private OnPostingCompleteListener postingComplete = new OnPostingCompleteListener() {
        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            Toast.makeText(getActivity(), "Sent", Toast.LENGTH_LONG).show();
            POST_STATUS = true;
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
            case R.id.share:
                if (MainFragment.AUTHORIZATION_STATUS) {
                    shareClick();
                } else{
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new MainFragment())
                            .commit();
                }
                break;
            case R.id.create_pdf_button:
                if (MainFragment.AUTHORIZATION_STATUS && POST_STATUS) {
                    createPdf();
                } else if (!MainFragment.AUTHORIZATION_STATUS){

                    Snackbar.make(mCoordinatorFrame, R.string.load_authorization_request, Snackbar.LENGTH_LONG)
                            .setAction("Авторизоваться", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .add(R.id.container, new MainFragment())
                                            .commit();
                                }
                            }).show();
                } else {

                    Snackbar.make(mCoordinatorFrame, R.string.load_post_request, Snackbar.LENGTH_LONG)
                            .setAction("Отправить запись на стену", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    shareClick();
                                }
                            }).show();
                }
                break;
            case R.id.view:
                viewPdf();
                break;
            case R.id.profile_placeholder:
                mAddPhotoLinLay.setVisibility(View.VISIBLE);
                mAddPhototv.setVisibility(View.GONE);
                break;
            case R.id.imageView:
                mProfilePlaceholder.setVisibility(View.VISIBLE);
                photo.setVisibility(View.GONE);
                break;
            case R.id.cam_btn:
                loadPhotoFromCamera();
                break;
            case R.id.gal_btn:
                loadPhotoFromGallery();
                break;
            case R.id.job_add_btn:
                mJobCount = 2;
                jobAddButton.setVisibility(View.GONE);

                LayoutInflater ltInflaterJob2 = getActivity().getLayoutInflater();
                ltInflaterJob2.inflate(R.layout.job, mJobLinLay, true);

                jobPeriod1 = (EditText) rootView.findViewById(R.id.job_period1);
                companyTitle1 = (EditText) rootView.findViewById(R.id.company_title1);
                jobTitle1 = (EditText) rootView.findViewById(R.id.job_title1);
                jobDuty1 =(EditText) rootView.findViewById(R.id.job_duty1);

                jobAddButton2 = (Button) rootView.findViewById(R.id.job_add_btn2);
                removeJobButton = (Button) rootView.findViewById(R.id.job_remove_button);

                jobAddButton2.setOnClickListener(this);
                removeJobButton.setOnClickListener(this);

                dutyString1 = jobDuty1.getText().toString();

                break;
            case R.id.job_remove_button:
                mJobCount = 1;
                jobAddButton.setVisibility(View.VISIBLE);

                View jobView = rootView.findViewById(R.id.job1_ll);
                ViewGroup parentJobll = (ViewGroup) jobView.getParent();
                parentJobll.removeView(jobView);


                break;
            case R.id.job_add_btn2:
                mJobCount = 3;
                jobAddButton2.setVisibility(View.GONE);
                removeJobButton.setVisibility(View.GONE);
                LayoutInflater ltInflaterJob3 = getActivity().getLayoutInflater();
                ltInflaterJob3.inflate(R.layout.job2, mJobLinLay, true);

                jobPeriod2 = (EditText) rootView.findViewById(R.id.job_period2);
                companyTitle2 = (EditText) rootView.findViewById(R.id.company_title2);
                jobTitle2 = (EditText) rootView.findViewById(R.id.job_title2);
                jobDuty2 =(EditText) rootView.findViewById(R.id.job_duty2);
                removeJobButton1 = (Button) rootView.findViewById(R.id.job_remove_button1);

                removeJobButton1.setOnClickListener(this);

                dutyString2 = jobDuty2.getText().toString();


                break;
            case R.id.job_remove_button1:
                mJobCount = 2;

                jobAddButton2.setVisibility(View.VISIBLE);
                removeJobButton.setVisibility(View.VISIBLE);

                View jobView2 = rootView.findViewById(R.id.job2_ll);
                ViewGroup parentJobll2 = (ViewGroup) jobView2.getParent();
                parentJobll2.removeView(jobView2);


                break;
            case R.id.study_add_button:
                mStudyCount = 2;
                studyAddButton.setVisibility(View.GONE);

                LayoutInflater ltInflaterStudy2 = getActivity().getLayoutInflater();
                ltInflaterStudy2.inflate(R.layout.study, mStudyLinLay, true);

                removeStudyButton = (Button) rootView.findViewById(R.id.study_remove_button);
                removeStudyButton.setOnClickListener(this);
                studyTitle1 = (EditText) rootView.findViewById(R.id.study_title1);
                studyDescription1 = (EditText) rootView.findViewById(R.id.study_decription1);
                rating1 = (EditText) rootView.findViewById(R.id.rating1);
                break;
            case R.id.study_remove_button:

                mStudyCount = 1;
                studyAddButton.setVisibility(View.VISIBLE);
                View studyView = rootView.findViewById(R.id.study1_ll);
                ViewGroup parentStudyll = (ViewGroup) studyView.getParent();
                parentStudyll.removeView(studyView);

                break;
            

        }
    }

    private void viewPdf() {

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/cv/sample.pdf");
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
            name.requestFocus();
            mProfilePlaceholder.setVisibility(View.VISIBLE);
            savePdfButton.setVisibility(View.GONE);
            viewPdfButton.setVisibility(View.GONE);
            shareButton.setVisibility(View.GONE);
            photo.setVisibility(View.GONE);

            jobAddButton.setVisibility(View.VISIBLE);
            if (mJobCount==2){
                jobAddButton2.setVisibility(View.VISIBLE);
                removeJobButton.setVisibility(View.VISIBLE);
            }
            if (mJobCount == 3){
                removeJobButton1.setVisibility(View.VISIBLE);
            }
            studyAddButton.setVisibility(View.VISIBLE);
            if (mStudyCount==2){
                removeStudyButton.setVisibility(View.VISIBLE);
            }

        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                userValue.setTextColor(Color.BLACK);


            }
            mProfilePlaceholder.setVisibility(View.GONE);
            if (MainFragment.AUTHORIZATION_STATUS){
                viewPdfButton.setVisibility(View.VISIBLE);
            }
            savePdfButton.setVisibility(View.VISIBLE);
            shareButton.setVisibility(View.VISIBLE);
            if (!MainFragment.AUTHORIZATION_STATUS){
                shareButton.setText("АВТОРИЗОВАТЬСЯ");
            } else {
                shareButton.setText("Запись на стену");
            }

            photo.setVisibility(View.VISIBLE);

            jobAddButton.setVisibility(View.GONE);
            if (mJobCount==2){
                jobAddButton2.setVisibility(View.GONE);
                removeJobButton.setVisibility(View.GONE);
            }
            if (mJobCount == 3){
                removeJobButton1.setVisibility(View.GONE);
            }
            studyAddButton.setVisibility(View.GONE);
            if (mStudyCount==2){
                removeStudyButton.setVisibility(View.GONE);
            }

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

            File file = new File(dir, "sample.pdf");
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


            Paragraph header1 = new Paragraph("Опыт работы");
            header1.setFont(headersLeftFont);
            header1.setAlignment(Element.ALIGN_CENTER);




            Paragraph header2 = new Paragraph("Образование");
            header2.setFont(headersLeftFont);
            header2.setAlignment(Element.ALIGN_CENTER);


            Paragraph header3 = new Paragraph("Достижения");
            header3.setFont(headersLeftFont);
            header3.setAlignment(Element.ALIGN_CENTER);





            Paragraph nameParagraph = new Paragraph(this.name.getText().toString());
            nameParagraph.setAlignment(Paragraph.ALIGN_CENTER);
            nameParagraph.setFont(nameFont);

            Paragraph nick = new Paragraph(this.nick.getText().toString());
            nick.setAlignment(Paragraph.ALIGN_CENTER);
            nick.setFont(nickFont);


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



            Paragraph header4 = new Paragraph("О себе");
            header4.setAlignment(Paragraph.ALIGN_LEFT);
            header4.setIndentationLeft(20);
            header4.setFont(headersRightFont);

            Paragraph bioParagraph = new Paragraph(this.bio.getText().toString());
            bioParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            bioParagraph.setIndentationLeft(10);
            bioParagraph.setFont(textLeftFont);

            Paragraph header5 = new Paragraph("Навыки");
            header5.setAlignment(Paragraph.ALIGN_LEFT);
            header5.setFont(headersRightFont);
            header5.setIndentationLeft(20);

            Paragraph header6 = new Paragraph("Другая информация");
            header6.setAlignment(Paragraph.ALIGN_LEFT);
            header6.setFont(headersRightFont);
            header6.setIndentationLeft(20);

            Paragraph subHeader1 = new Paragraph("Языки");
            subHeader1.setAlignment(Paragraph.ALIGN_LEFT);
            subHeader1.setFont(skillsFont);
            subHeader1.setIndentationLeft(15);


            Paragraph subHeader2 = new Paragraph("Хобби");
            subHeader2.setAlignment(Paragraph.ALIGN_LEFT);
            subHeader2.setFont(skillsFont);
            subHeader2.setIndentationLeft(15);



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

           if (mPhotoFile != null) {
               ByteArrayOutputStream streamAvatar = new ByteArrayOutputStream();

            Bitmap bitmapAvatar;
            bitmapAvatar = BitmapFactory.decodeFile(mPhotoFile.getPath());
            bitmapAvatar.compress(Bitmap.CompressFormat.JPEG, 100 , streamAvatar);
            Image myImgAvatar = Image.getInstance(streamAvatar.toByteArray());
            myImgAvatar.setAlignment(Image.MIDDLE);
            myImgAvatar.scaleAbsolute(100f, 100f);
               myImgAvatar.setSpacingBefore(30f);
            columnRight.addElement(myImgAvatar);

           }else if(bitmapAva!= null){
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
            header1.setSpacingAfter(10f);
            columnLeft.addElement(header1);


            Paragraph companyTitleParagraph = new Paragraph("\u2022" + this.companyTitle.getText().toString());
            companyTitleParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            companyTitleParagraph.setFont(headersSmallLeftFont);

            Paragraph jobPeriodParagraph = new Paragraph(this.jobPeriod.getText().toString());
            jobPeriodParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            jobPeriodParagraph.setFont(textLeftFontGray);


            Paragraph jobTitleParagraph = new Paragraph(this.jobTitle.getText().toString());
            jobTitleParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            jobTitleParagraph.setFont(textLeftFont);

            columnLeft.addElement(companyTitleParagraph);

            jobPeriodParagraph.setSpacingAfter(5f);
            columnLeft.addElement(jobPeriodParagraph);

            jobTitleParagraph.setSpacingAfter(5f);
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
                Paragraph companyTitleParagraph1 = new Paragraph("\u2022" + companyTitle1.getText().toString());
                companyTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                companyTitleParagraph1.setFont(headersSmallLeftFont);

                Paragraph jobPeriodParagraph1 = new Paragraph(jobPeriod1.getText().toString());
                jobPeriodParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobPeriodParagraph1.setFont(textLeftFontGray);


                Paragraph jobTitleParagraph1 = new Paragraph(jobTitle1.getText().toString());
                jobTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobTitleParagraph1.setFont(textLeftFont);

                columnLeft.addElement(companyTitleParagraph1);

                jobPeriodParagraph1.setSpacingAfter(5f);
                columnLeft.addElement(jobPeriodParagraph1);

                jobTitleParagraph1.setSpacingAfter(5f);
                columnLeft.addElement(jobTitleParagraph1);


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
                Paragraph companyTitleParagraph1 = new Paragraph("\u2022" + companyTitle1.getText().toString());
                companyTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                companyTitleParagraph1.setFont(headersSmallLeftFont);

                Paragraph jobPeriodParagraph1 = new Paragraph(jobPeriod1.getText().toString());
                jobPeriodParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobPeriodParagraph1.setFont(textLeftFontGray);


                Paragraph jobTitleParagraph1 = new Paragraph(jobTitle1.getText().toString());
                jobTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobTitleParagraph1.setFont(textLeftFont);

                columnLeft.addElement(companyTitleParagraph1);

                jobPeriodParagraph1.setSpacingAfter(5f);
                columnLeft.addElement(jobPeriodParagraph1);

                jobTitleParagraph1.setSpacingAfter(5f);
                columnLeft.addElement(jobTitleParagraph1);


                Paragraph jobDutyParagraph1 = new Paragraph();
                String [] jobDutyProcessed1 = stringProcessor(dutyString1);
                for (String aJobDutyProcessed1 : jobDutyProcessed1) {

                    Chunk jobDuty1 = new Chunk(aJobDutyProcessed1.trim());
                    jobDutyParagraph1.add(jobDuty1+"\n");
                }

                jobDutyParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                jobDutyParagraph1.setFont(textLeftFont);
                columnLeft.addElement(jobDutyParagraph1);




                Paragraph companyTitleParagraph2 = new Paragraph("\u2022" + companyTitle2.getText().toString());
                companyTitleParagraph2.setAlignment(Paragraph.ALIGN_LEFT);
                companyTitleParagraph2.setFont(headersSmallLeftFont);

                Paragraph jobPeriodParagraph2 = new Paragraph(jobPeriod2.getText().toString());
                jobPeriodParagraph2.setAlignment(Paragraph.ALIGN_LEFT);
                jobPeriodParagraph2.setFont(textLeftFontGray);


                Paragraph jobTitleParagraph2 = new Paragraph(jobTitle2.getText().toString());
                jobTitleParagraph2.setAlignment(Paragraph.ALIGN_LEFT);
                jobTitleParagraph2.setFont(textLeftFont);

                columnLeft.addElement(companyTitleParagraph2);

                jobPeriodParagraph2.setSpacingAfter(5f);
                columnLeft.addElement(jobPeriodParagraph2);

                jobTitleParagraph2.setSpacingAfter(5f);
                columnLeft.addElement(jobTitleParagraph2);


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


            myImgEdu.setSpacingBefore(30f);
            columnLeft.addElement(myImgEdu);

            header2.setSpacingAfter(10f);
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

            studyTitleParagraph.setSpacingAfter(5f);
            columnLeft.addElement(studyTitleParagraph);

            studyDescriptionParagraph.setSpacingAfter(5f);
            columnLeft.addElement(studyDescriptionParagraph);

            studyRatingParagraph.setSpacingAfter(5f);
            columnLeft.addElement(studyRatingParagraph);

            if (mStudyCount==2){
                Paragraph studyTitleParagraph1 = new Paragraph("\u2022" + studyTitle1.getText().toString());
                studyTitleParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                studyTitleParagraph1.setFont(headersSmallLeftFont);

                Paragraph studyDescriptionParagraph1 = new Paragraph(studyDescription1.getText().toString());
                studyDescriptionParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                studyDescriptionParagraph1.setFont(textLeftFont);

                Paragraph studyRatingParagraph1 = new Paragraph(rating1.getText().toString());
                studyRatingParagraph1.setAlignment(Paragraph.ALIGN_LEFT);
                studyRatingParagraph1.setFont(textLeftFont);

                studyTitleParagraph1.setSpacingAfter(5f);
                columnLeft.addElement(studyTitleParagraph1);

                studyDescriptionParagraph1.setSpacingAfter(5f);
                columnLeft.addElement(studyDescriptionParagraph1);

                studyRatingParagraph1.setSpacingAfter(5f);
                columnLeft.addElement(studyRatingParagraph1);

            }

            header3.setSpacingBefore(30f);
            header3.setSpacingAfter(10f);
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




            nameParagraph.setSpacingBefore(10f);
            nameParagraph.setSpacingAfter(5f);

            columnRight.addElement(nameParagraph);
            nick.setSpacingAfter(20f);
            columnRight.addElement(nick);

            columnRight.addElement(telephoneParagraph);
            columnRight.addElement(emailParagraph);

            header4.setSpacingBefore(40f);
            header4.setSpacingAfter(10f);

            columnRight.addElement(header4);
            columnRight.addElement(bioParagraph);

            header5.setSpacingBefore(30f);
            header5.setSpacingAfter(10f);
            columnRight.addElement(header5);

            Paragraph skillsParagraph = new Paragraph();
            String [] skillsProcessed = stringProcessor(skillString);
            for (String skill : skillsProcessed) {
                String [] currentSkill = skillsStringProcessor(skill.trim());
                Chunk skillChunk = new Chunk(currentSkill[0]+"   ");
                skillChunk.setFont(skillsFont);
                int skillRating = Integer.parseInt(currentSkill[1]);
                int skillBlankRating = 5 - skillRating;
                Chunk skillRatingChunk = new Chunk();
                skillRatingChunk.setFont(skillsRatingFont);
                for (int i = 0; i<= skillRating; i++){
                    skillRatingChunk.append("\u2022");
                }
                Chunk skillBlankRatingChunk = new Chunk();
                skillBlankRatingChunk.setFont(skillsBlankRatingFont);
                while (skillBlankRating>0){
                    skillBlankRatingChunk.append("\u2022");
                    skillBlankRating--;
                }

                skillsParagraph.add(skillChunk);
                skillsParagraph.add(skillRatingChunk);
                skillsParagraph.add(skillBlankRatingChunk);
                skillsParagraph.add("\n");
            }
            skillsParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            skillsParagraph.setIndentationLeft(10);
            columnRight.addElement(skillsParagraph);

            header6.setSpacingBefore(30f);
            header6.setSpacingAfter(10f);

            columnRight.addElement(header6);

            subHeader1.setSpacingAfter(5f);
            columnRight.addElement(subHeader1);


            Paragraph languagesParagraph = new Paragraph();
            String [] languagesProcessed = stringProcessor(languageString);
            for (String language : languagesProcessed) {
                Chunk languageChunk = new Chunk(language.trim());
                languagesParagraph.add(languageChunk+"\n");
            }
            languagesParagraph.setAlignment(Paragraph.ALIGN_LEFT);
            languagesParagraph.setFont(textLeftFont);
            languagesParagraph.setSpacingAfter(10f);
            languagesParagraph.setIndentationLeft(10);
            columnRight.addElement(languagesParagraph);

            subHeader2.setSpacingAfter(5f);
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
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
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





}


