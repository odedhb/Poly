package com.robinlabs.poly;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oded on 2/10/14.
 */
public class Need {
    String name;
    String uri;
    List<Property> properties;
    String NAME = "name";
    String URI = "uri";
    String PROPERTIES = "properties";
    long lastRejectDate;
    boolean isApproved;

    Need() {
        this.properties = new ArrayList<Property>();
    }

    Need(String jsonString) {

        JSONObject jsonNeed = null;
        try {
            jsonNeed = new JSONObject(jsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        this.name = jsonNeed.optString(NAME);
        this.uri = jsonNeed.optString(URI);

        JSONArray ja = jsonNeed.optJSONArray(PROPERTIES);

        this.properties = new ArrayList<Property>();

        for (int i = 0; i < ja.length(); i++) {
            Property p = new Property(ja.optJSONObject(i));
            this.properties.add(p);
        }


    }

    boolean isNeedReadyToExecute() {
        if (getNextProperty() == null) {
            return true;
        }
        return false;
    }

    public Property getNextProperty() {
        for (Property property : properties) {
            if (property.value == null && property.lastRejectDate == 0) return property;
        }
        return null;
    }

    public String toScreen() {
        String result = "";
        result += name + "\n\n";

        for (Property p : properties) {
            result += p.getQuestion() + "\n";

            if (p.value == null) {
                result += "\n\n";
            } else {
                result += p.value + "\n\n";
            }
        }

        result += "\n" + uri;

        return result;
    }

    JSONObject toJSON() {
        JSONArray ja = new JSONArray();
        for (Property p : properties) {
            ja.put(p.toJSON());
        }

        JSONObject jo = new JSONObject();

        try {
            jo.put(NAME, name);
            jo.put(URI, uri);
            jo.put(PROPERTIES, ja);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jo;
    }

    void saveToStorage(Context context) {
        Storage.putEntry(context, Storage.NEEDS_TABLE, this.name, this.toJSON().toString());
    }

    public String describeContent() {

        String result = "";

        for (Property p : properties) {
            if (p.value != null) {
                result += p.value + ", ";
            }
        }


        return result;
    }

    public String describeContentLong() {

        String result = "";
        String dontCare = null;

        for (Property p : properties) {
            if (p.value == null) {

                if (dontCare == null) {
                    dontCare = "";
                }

                dontCare += p.getQuestion() + ", ";
            } else {
                result += p.value + " is the answer for " + p.getQuestion() + ".\n";
            }
        }

        if (dontCare != null) {
            result += "And you don't care about " + dontCare;
        }

        return result;
    }
}
