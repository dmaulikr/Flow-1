package com.pressurelabs.flow;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import java.util.concurrent.TimeUnit;

/**
 * Flow_app
 *
 * @author Robert Simoes, 2016-08-30
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */
public class AppUtils {

    /**
     * Calculates total minutes from millisecond input
     *
     * @param millis
     * @return
     */
    public static int calculateTotalMinutes(int millis) {
        return (millis/(1000*60));
    }
    
    /**
     * Calculates remaining minutes (ie. <60mins) from millis input
     *
     */
    public static int calcRemainderMins(int millis) {
        return calculateTotalMinutes(millis)%60;
    }

    /**
     * Calculates whole hours from millisecond input
     *
     * @param millis
     * @return
     */
    public static int calcHours(int millis) {
        return (int) calculateTotalMinutes(millis)/60;
    }

    /**
     * Builds String output of time in the style of: hrs H mins M
     *
     * @param millis
     * @return
     */
    public static String buildCardViewStyleTime(int millis) {
        return String.valueOf(calcHours(millis)) +"H " + String.valueOf(calcRemainderMins(millis)) +"M";
    }

    /**
     * Builds String output of time in the style of: HH:MM:SS
     * @param millis
     * @return
     */
    public static String buildTimerStyleTime(long millis) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                        - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millis)
                ),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millis)
                )

        );
    }

    public static String buildStandardTimeOutput(int millis) {
        int hrs = calcHours(millis);
        int mins = calcRemainderMins(millis);
        if (hrs>0 && mins >=0) {
            return hrs + " hrs\n" + mins + " mins";
        } else if (hrs>0) {
            return hrs + " hours.";
        } else if (mins>=0) {
            return mins + " minutes";
        } else {
            return "";
        }
    }

    public static int minsToMillis(int mins) {
        return (mins*60*1000);
    }

    public static int hrsToMillis(int hrs) {
        return (hrs*60*60*1000);
    }


    public static int millisToSecs(int millis) {
        return (millis/1000);
    }

    public static void animateViewRotateFade(View v, String animationState) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
        rotate.setDuration(250);
        AnimatorSet animSetFS = new AnimatorSet();
        switch (animationState) {
            case AppConstants.ANIMATION_ENTRY:
                ObjectAnimator alphaEntry = ObjectAnimator.ofFloat(v, "alpha",0f, 1f);
                alphaEntry.setDuration(200);
                animSetFS.play(alphaEntry).before(rotate);
                animSetFS.start();
                break;
            case AppConstants.ANIMATION_EXIT:
                ObjectAnimator alphaExit = ObjectAnimator.ofFloat(v, "alpha",1f, 0f);
                alphaExit.setDuration(200);
                animSetFS.play(rotate).before(alphaExit);
                animSetFS.start();
                break;
        }
    }

}
