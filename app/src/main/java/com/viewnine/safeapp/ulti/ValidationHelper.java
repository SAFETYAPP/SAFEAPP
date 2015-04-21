package com.viewnine.safeapp.ulti;

import android.text.TextUtils;

import com.viewnine.safeapp.manager.SharePreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 4/18/15.
 */
public class ValidationHelper {
    private static ValidationHelper ourInstance = new ValidationHelper();

    public static ValidationHelper getInstance() {
        return ourInstance;
    }

    private ValidationHelper() {
    }

    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isEqualStringAndMinChars(String str1, String str2, int minChars){
        boolean result = false;
        if(str1.endsWith(str2) && str1.length() >= 4){
            result = true;
        }else {
            result = false;
        }
        return result;
    }

    public boolean isMinimumStringLenght4(String str){
        if(str.length() >= 4){
            return true;
        }else {
            return false;
        }
    }

    public boolean isStringContainsSpecialCharacter(String str){
        Pattern p = Pattern.compile("[^A-Za-z0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        boolean b = m.find();
        return b;
    }

    public boolean isStringHaveNoContainsSpecialCharacter(String str){
        if(TextUtils.isEmpty(str)){
            return false;
        }
        Pattern p = Pattern.compile("[^A-Za-z0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        boolean b = m.find();
        if(b == true){
            return false;
        }else {
            return true;
        }
    }

    public ArrayList<String> convertStringToArraylist(String str, ArrayList<String> arrayCurrentlistTag){
        if (!TextUtils.isEmpty(str)){
            if(arrayCurrentlistTag == null){
                arrayCurrentlistTag = new ArrayList<String>();
            }
            ArrayList<String> arraylistString = new ArrayList<String>(Arrays.asList(str.split(",")));

            Set<String> filterDuplicate = new HashSet<String>();
            filterDuplicate.addAll(arraylistString);
            arraylistString.clear();
            arraylistString.addAll(filterDuplicate);
            Collections.reverse(arraylistString);
            for (int i = 0; i < arraylistString.size(); i++) {
                boolean isNeedToAddNewTag = true;
                for (String tag : arrayCurrentlistTag) {
                    if (tag.trim().equals(arraylistString.get(i).trim())) {
//						arrayTag.remove(tag);
                        isNeedToAddNewTag = false;
                        break;
                    }
                }
                if(isNeedToAddNewTag && !TextUtils.isEmpty(arraylistString.get(i).trim())){
                    arrayCurrentlistTag.add(0, arraylistString.get(i).trim());
                }

            }
        }

        return arrayCurrentlistTag;

    }

    public boolean alreadySetupEmail(){
        if(SharePreferenceManager.getInstance().getPrimaryEmail().isEmpty()){
            return false;
        }else {
            return true;
        }
    }
}
