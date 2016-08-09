package org.hr24.almel.testchallenge.utils;

import android.content.SharedPreferences;
import android.net.Uri;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexey on 09.08.16.
 */
public class PreferenceManager {

    private SharedPreferences mSharedPreferences;

    private static  final String[] USER_FIELDS = {
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


    public PreferenceManager() {
        this.mSharedPreferences = ResumeBuilderApplication.getSharedPreferences();
    }

    /**
     * сохраняем поля пользователя
     * @param userFields
     */
    public void saveUserProfileData(List<String> userFields){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i =0; i<USER_FIELDS.length; i++){
            editor.putString(USER_FIELDS[i], userFields.get(i));
        }
        editor.apply();

    }

    /**
     * метод возвращающий поля пользователя для их последующего восстановления
     * @return
     */
    public List<String> loadUserProfileData(){
        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_NAME_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_NICK_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_BIO_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_SKILLS_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_LANGUAGES_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_HOBBY_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_JOB_PERIOD_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_COMPANY_TITLE_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_JOB_TITLE_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_JOB_DUTY_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_STUDY_TITLE_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_STUDY_DESCRIPTION_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_RATING_KEY, "null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_ACHIEVEMENTS_KEY, "null"));

        return userFields;
    }

    /**
     * сохраняем ссылку на фото пользователя
     * @param uri
     */
    public void saveUserPhoto(Uri uri){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }



    /**
     * возвращаем ссылку на фото пользователя
     * @return
     */
    public Uri loadUserPhoto(){
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY, "android.resource://org.hr24.almel.testchallenge/drawable/ic_add_a_photo_black_24dp.xml"));
    }

}
