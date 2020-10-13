package org.uplus.twspopup.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public final class IntentUtils {
    private static final String TAG = "IntentUtils";

    public static String getStringExtra(Intent intent, String str) {
        if (intent == null) {
            return "";
        }
        try {
            return intent.getStringExtra(str);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return "";
        }
    }

    public static boolean getBooleanExtra(Intent intent, String str, boolean z) {
        if (intent == null) {
            return z;
        }
        try {
            return intent.getBooleanExtra(str, z);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return z;
        }
    }

    public static int getIntExtra(Intent intent, String str, int i) {
        if (intent == null) {
            return i;
        }
        try {
            return intent.getIntExtra(str, i);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return i;
        }
    }

    public static <T extends Parcelable> T getParcelableExtra(Intent intent, String str) {
        if (intent == null) {
            return null;
        }
        try {
            return intent.getParcelableExtra(str);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    public static Bundle getBundleExtra(Intent intent, String str) {
        if (intent == null) {
            return null;
        }
        try {
            return intent.getBundleExtra(str);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    public static ArrayList<String> getStringArrayListExtra(Intent intent, String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (intent == null) {
            return arrayList;
        }
        try {
            return intent.getStringArrayListExtra(str);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return arrayList;
        }
    }
}
