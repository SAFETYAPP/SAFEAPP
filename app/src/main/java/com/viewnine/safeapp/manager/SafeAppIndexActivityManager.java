package com.viewnine.safeapp.manager;

import java.util.Stack;

/**
 * Created by user on 4/28/15.
 */
public class SafeAppIndexActivityManager {

    private static Stack<Integer> mStackIndex = new Stack<Integer>();

    public static int getCurrent(){
        if(!mStackIndex.isEmpty())
            return mStackIndex.elementAt(mStackIndex.size()-1);
        return -1;
    }

    public static int getPrevious(){
        if(mStackIndex.size() > 1)
            return mStackIndex.elementAt(mStackIndex.size()-2);
        return -1;
    }

    public static void setCurrent(int currentIndex) {
//        if(!mStackIndex.isEmpty()){
//            mPrevious = mStackIndex.elementAt(mStackIndex.size()-1);
//        }

        mStackIndex.push(currentIndex);
//        mCurrent = currentIndex;
    }

    public static void pop(){
        if(!mStackIndex.isEmpty()){
            mStackIndex.pop();
        }
    }

//    public static void setPrevious(int previousIndex){
//        mPrevious = previousIndex;
//    }
//
//    public static void changePrevious(){
//        mPrevious = mCurrent;
//    }

    public static void reset(){
        mStackIndex.clear();
//        mPrevious = -1;
//        mCurrent = -1;
    }
}
