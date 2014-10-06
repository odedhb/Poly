package com.robinlabs.poly;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oded on 2/18/14.
 */
public class Storage {

    static String NEEDS_TABLE = "NEEDS_TABLE";

    static SharedPreferences sp(Context ctx, String whichSharedPrefs) {
        return ctx.getSharedPreferences(whichSharedPrefs, ctx.MODE_PRIVATE);
    }

    static HashMap<String, String> getAll(Context ctx, String tableName) {
        return (HashMap<String, String>) sp(ctx, tableName).getAll();
    }

    static List<Need> getAllNeeds(Context ctx) {
        List<Need> allNeeds = new ArrayList<Need>();
        HashMap<String, String> storedNeeds = getAll(ctx, Storage.NEEDS_TABLE);
        for (String jsonString : storedNeeds.values()) {
            allNeeds.add(new Need(jsonString));
        }
        return allNeeds;
    }

    static void putEntry(Context ctx, String tableName, String key, String value) {
        sp(ctx, tableName).edit().putString(key, value).commit();
    }
}
