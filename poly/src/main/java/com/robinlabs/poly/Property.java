package com.robinlabs.poly;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by oded on 2/10/14.
 */
public class Property {


    String question;
    String QUESTION = "question";

    Type type;
    String value;
    Integer importance;//0-mandatory/1-recommended/2-optional/null-optional/ and so on, this can also affect the sort order
    public long lastRejectDate;
    Map<String, String> synonyms;

    Property() {

    }

    Property(String question) {
        this.question = question;
    }


    public Property(JSONObject jsonProperty) {
        try {
            this.question = jsonProperty.getString(QUESTION);
        } catch (JSONException e) {
            this.question = null;
        }

    }

    String getQuestion() {
        return question;
    }

    JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(QUESTION, question);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jo;
    }

}
