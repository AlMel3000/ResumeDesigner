package org.hr24.almel.testchallenge.ui.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProfileFragment extends Fragment implements OnRequestSocialPersonCompleteListener, View.OnClickListener{
    private String message = "Need simple social networks integration? Check this lbrary:";
    private String link = "Посмотрите на моё новое резюме";

    private static final String NETWORK_ID = "NETWORK_ID";
    private SocialNetwork socialNetwork;
    private int networkId;
    private ImageView photo, photoPlaceholder;
    private EditText name, nick,tel, email, bio, skills, languages, hobby, jobPeriod, jobTitle, companyTitle, jobDuty, studyTitle, studyDescription, rating, achievements;
    private Button savePdfButton, share, viewPDF, camButton, galButton;
    private FloatingActionButton mFab;
    private int mCurrentEditMode = 1;
    private List<EditText> mUserInfoViews;
    private RelativeLayout mProfilePlaceholder;
    private LinearLayout mAddPhotoLinLay;
    private TextView mAddPhototv;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private CoordinatorLayout mCoordinatorFrame;




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
        photoPlaceholder = (ImageView) rootView.findViewById(R.id.photo_placeholder);
        mProfilePlaceholder = (RelativeLayout) rootView.findViewById(R.id.profile_placeholder);
        mAddPhotoLinLay = (LinearLayout) rootView.findViewById(R.id.add_photo_ll);

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
        studyTitle =(EditText) rootView.findViewById(R.id.study_title);
        studyDescription =(EditText) rootView.findViewById(R.id.study_decription);
        rating =(EditText) rootView.findViewById(R.id.rating);
        achievements=(EditText) rootView.findViewById(R.id.achievements);
        jobDuty=(EditText) rootView.findViewById(R.id.job_duty);
        mCoordinatorFrame = (CoordinatorLayout) rootView.findViewById(R.id.frame);

        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        share = (Button) rootView.findViewById(R.id.share);
        savePdfButton = (Button) rootView.findViewById(R.id.create_pdf_button);
        viewPDF = (Button) rootView.findViewById(R.id.view);
        camButton = (Button) rootView.findViewById(R.id.cam_btn);
        galButton = (Button) rootView.findViewById(R.id.gal_btn);

        savePdfButton.setOnClickListener(this);
        viewPDF.setOnClickListener(this);
        share.setOnClickListener(this);
        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        mAddPhotoLinLay.setOnClickListener(this);
        camButton.setOnClickListener(this);
        galButton.setOnClickListener(this);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);

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



    private  void shareClick() {
        
        
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
            case R.id.share:
                shareClick();
                break;
            case R.id.create_pdf_button:
                createPdf();
                break;
            case R.id.view:
                viewPdf();
                break;
            case R.id.profile_placeholder:
                mAddPhotoLinLay.setVisibility(View.VISIBLE);
                mAddPhototv.setVisibility(View.GONE);;
                break;
            case R.id.cam_btn:
                loadPhotoFromCamera();
                break;
            case R.id.gal_btn:
                loadPhotoFromGallery();
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
                    "No Application Available to View PDF",
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
            viewPDF.setVisibility(View.GONE);
            share.setVisibility(View.GONE);
            photo.setVisibility(View.GONE);
            //// TODO: 01.08.16 реализовать возможность выбора фото из галереи/съёмки 
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                userValue.setTextColor(Color.BLACK);


            }
            mProfilePlaceholder.setVisibility(View.GONE);
            savePdfButton.setVisibility(View.VISIBLE);
            share.setVisibility(View.VISIBLE);
            viewPDF.setVisibility(View.VISIBLE);
            photo.setVisibility(View.VISIBLE);

        }
    }






    public void createPdf() {

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
            Font headersRightFont= new Font(droidSans, 16.0f, 1, BaseColor.BLACK);
            Font skillsFont= new Font(droidSans, 12.0f, 1, BaseColor.WHITE);


            Paragraph header1 = new Paragraph("Опыт работы");
            header1.setFont(headersLeftFont);

            Paragraph companyTitle = new Paragraph("\u2022" + this.companyTitle.getText().toString());
            companyTitle.setAlignment(Paragraph.ALIGN_LEFT);
            companyTitle.setFont(headersSmallLeftFont);

            Paragraph jobPeriod = new Paragraph(this.jobPeriod.getText().toString());
            jobPeriod.setAlignment(Paragraph.ALIGN_LEFT);
            jobPeriod.setFont(textLeftFontGray);



            Paragraph jobTitle = new Paragraph(this.jobTitle.getText().toString());
            jobTitle.setAlignment(Paragraph.ALIGN_LEFT);
            jobTitle.setFont(textLeftFont);

            Paragraph jobDuty = new Paragraph(this.jobDuty.getText().toString());
            jobDuty.setAlignment(Paragraph.ALIGN_LEFT);
            jobDuty.setFont(textLeftFont);

            Paragraph header2 = new Paragraph("Образование");
            header2.setFont(headersLeftFont);

            Paragraph studyTitle = new Paragraph("\u2022" + this.studyTitle.getText().toString());
            studyTitle.setAlignment(Paragraph.ALIGN_LEFT);
            studyTitle.setFont(headersSmallLeftFont);

            Paragraph studyDescription = new Paragraph(this.studyDescription.getText().toString());
            studyDescription.setAlignment(Paragraph.ALIGN_LEFT);
            studyDescription.setFont(textLeftFont);

            Paragraph studyRating = new Paragraph(rating.getText().toString());
            studyRating.setAlignment(Paragraph.ALIGN_LEFT);
            studyRating.setFont(textLeftFont);

            Paragraph header3 = new Paragraph("Достижения");
            header3.setFont(headersLeftFont);

            Paragraph achievements = new Paragraph(this.achievements.getText().toString());
            achievements.setAlignment(Paragraph.ALIGN_LEFT);
            achievements.setFont(textLeftFont);







            Paragraph name = new Paragraph(this.name.getText().toString());
            name.setAlignment(Paragraph.ALIGN_CENTER);
            name.setFont(nameFont);

            Paragraph nick = new Paragraph(this.nick.getText().toString());
            nick.setAlignment(Paragraph.ALIGN_CENTER);
            nick.setFont(nickFont);


            Paragraph telephone = new Paragraph("   T   "+ tel.getText().toString());
            telephone.setAlignment(Paragraph.ALIGN_LEFT);
            telephone.setFont(contactsFont);

            Paragraph email = new Paragraph("   E   "+ this.email.getText().toString());
            email.setAlignment(Paragraph.ALIGN_LEFT);
            email.setFont(contactsFont);


            Paragraph header4 = new Paragraph("О себе");
            header4.setAlignment(Paragraph.ALIGN_CENTER);
            header4.setFont(headersRightFont);

            Paragraph bio = new Paragraph(this.bio.getText().toString());
            bio.setAlignment(Paragraph.ALIGN_CENTER);
            bio.setFont(textLeftFont);

            Paragraph header5 = new Paragraph("Навыки");
            header5.setAlignment(Paragraph.ALIGN_CENTER);
            header5.setFont(headersRightFont);

            Paragraph skills = new Paragraph(this.skills.getText().toString());
            skills.setAlignment(Paragraph.ALIGN_CENTER);
            skills.setFont(skillsFont);

            Paragraph header6 = new Paragraph("Другая информация");
            header6.setAlignment(Paragraph.ALIGN_CENTER);
            header6.setFont(headersRightFont);

            Paragraph subHeader1 = new Paragraph("Языки");
            subHeader1.setAlignment(Paragraph.ALIGN_CENTER);
            subHeader1.setFont(skillsFont);

            Paragraph languages = new Paragraph(this.languages.getText().toString());
            languages.setAlignment(Paragraph.ALIGN_CENTER);
            languages.setFont(textLeftFont);

            Paragraph subHeader2 = new Paragraph("Хобби");
            subHeader2.setAlignment(Paragraph.ALIGN_CENTER);
            subHeader2.setFont(skillsFont);

            Paragraph hobby = new Paragraph(this.hobby.getText().toString());
            hobby.setAlignment(Paragraph.ALIGN_CENTER);
            hobby.setFont(textLeftFont);









            ByteArrayOutputStream streamExp = new ByteArrayOutputStream();
            Bitmap bitmapExp;
            bitmapExp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.exp);
            bitmapExp.compress(Bitmap.CompressFormat.JPEG, 100 , streamExp);
            Image myImgExp = Image.getInstance(streamExp.toByteArray());
            myImgExp.setAlignment(Image.MIDDLE);

            myImgExp.scaleAbsolute(45f, 45f);

            ByteArrayOutputStream streamEdu = new ByteArrayOutputStream();
            Bitmap bitmapEdu;
            bitmapEdu = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.edu);
            bitmapEdu.compress(Bitmap.CompressFormat.JPEG, 100 , streamEdu);
            Image myImgEdu = Image.getInstance(streamEdu.toByteArray());
            myImgEdu.setAlignment(Image.MIDDLE);

            myImgEdu.scaleAbsolute(40f, 40f);

           if (mPhotoFile != null) {
               ByteArrayOutputStream streamAva = new ByteArrayOutputStream();

            Bitmap bitmapAvatar;
            bitmapAvatar = BitmapFactory.decodeFile(mPhotoFile.getPath());
            bitmapAvatar.compress(Bitmap.CompressFormat.JPEG, 100 , streamAva);
            Image myImgAva = Image.getInstance(streamAva.toByteArray());
            myImgAva.setAlignment(Image.MIDDLE);
            myImgAva.scaleAbsolute(100f, 100f);
               myImgAva.setSpacingBefore(40f);
            columnRight.addElement(myImgAva);
           }else {
               showSnackbar("Резюме без фото, пока Вы не сфотографируетсь");
           }



            columnLeft.addElement(myImgExp);
            header1.setSpacingAfter(10f);
            columnLeft.addElement(header1);

            columnLeft.addElement(companyTitle);

            jobPeriod.setSpacingAfter(5f);
            columnLeft.addElement(jobPeriod);

            jobTitle.setSpacingAfter(5f);
            columnLeft.addElement(jobTitle);

            columnLeft.addElement(jobDuty);
            myImgEdu.setSpacingBefore(40f);
            myImgEdu.setSpacingAfter(20f);
            columnLeft.addElement(myImgEdu);

            header2.setSpacingAfter(10f);
            columnLeft.addElement(header2);

            studyTitle.setSpacingAfter(5f);
            columnLeft.addElement(studyTitle);

            studyDescription.setSpacingAfter(5f);
            columnLeft.addElement(studyDescription);

            studyRating.setSpacingAfter(5f);
            columnLeft.addElement(studyRating);

            header3.setSpacingBefore(40f);
            header3.setSpacingAfter(10f);
            columnLeft.addElement(header3);

            columnLeft.addElement(achievements);




            name.setSpacingBefore(10f);
            name.setSpacingAfter(5f);

            columnRight.addElement(name);
            nick.setSpacingAfter(20f);
            columnRight.addElement(nick);

            columnRight.addElement(telephone);
            columnRight.addElement(email);

            header4.setSpacingBefore(40f);
            header4.setSpacingAfter(10f);

            columnRight.addElement(header4);
            columnRight.addElement(bio);

            header5.setSpacingBefore(40f);
            header5.setSpacingAfter(10f);

            columnRight.addElement(header5);
            columnRight.addElement(skills);

            header6.setSpacingBefore(40f);
            header6.setSpacingAfter(10f);

            columnRight.addElement(header6);

            subHeader1.setSpacingAfter(5f);
            columnRight.addElement(subHeader1);

            languages.setSpacingAfter(10f);
            columnRight.addElement(languages);

            subHeader2.setSpacingAfter(5f);
            columnRight.addElement(subHeader2);

            columnRight.addElement(hobby);




            columnLeft.go();
            columnRight.go();



        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally
        {
            document.close();
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

    public void loadPhotoFromCamera() {
        Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            mPhotoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка получения фото", Toast.LENGTH_SHORT).show();

        }
        if (mPhotoFile != null) {
            takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
        }

    }
    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_choose_picture)), ConstantManager.REQUEST_GALLERY_PICTURE);


    }

    private void showSnackbar(String message){
        Snackbar.make(mCoordinatorFrame, message,Snackbar.LENGTH_LONG).show();
    }



}


