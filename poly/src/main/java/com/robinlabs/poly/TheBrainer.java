package com.robinlabs.poly;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//assumptions
//people don't like to give detailed orders, they prefer to be asked questions and have a conversation.

public class TheBrainer extends Application {


    public static TheBrainer instance;
    List<Need> needsInMemory;
    Need currentNeed;
    Property currentProperty;

    static int UNDERSTOOD_CONTINUE_DIALOG = 1001;
    static int STOP_DIALOG = 1002;
    static int CONTINUE_TRYING_TO_UNDERSTAND = 1003;
    static int PROVIDE_NEED = 1004;


    public TheBrainer() {
        instance = this;
        needsInMemory = getAllHardcodedNeeds();
    }

    List<Need> getAllHardcodedNeeds() {

        List<Need> needs = new ArrayList<Need>();

        Need gSearchNeed = new Need();
        gSearchNeed.name = "search gmail";
        gSearchNeed.uri = "https://mail.google.com/mail/mu/mp/300/#tl/search/from:Q1 to:Q2 +Q3 -Q4";
        gSearchNeed.properties = new ArrayList<Property>();
        gSearchNeed.properties.add(new Property("Who sent the email?"));
        gSearchNeed.properties.add(new Property("Who is the recipient?"));
        gSearchNeed.properties.add(new Property("Words that appeared in the email"));
        gSearchNeed.properties.add(new Property("Words that did not appear"));

        Need advancedSearchNeed = new Need();
        advancedSearchNeed.name = "search google";
        advancedSearchNeed.uri = "https://www.google.com/search?as_q=Q1&as_eq=Q2";
        advancedSearchNeed.properties = new ArrayList<Property>();
        advancedSearchNeed.properties.add(new Property("Which words would you like to search?"));
        advancedSearchNeed.properties.add(new Property("Which words would you like to exclude from your search?"));
//        advancedSearchNeed.properties.add(new MultiChoiceProperty("&as_eq=",String[]));

        needs.add(gSearchNeed);
        needs.add(advancedSearchNeed);

//        Storage.putEntry(this, Storage.NEEDS_TABLE, gSearchNeed.name, gSearchNeed.toJSON().toString());
//        Storage.putEntry(this, Storage.NEEDS_TABLE, advancedSearchNeed.name, advancedSearchNeed.toJSON().toString());

        return needs;
    }


    int useSpokenWords(ArrayList<String> speechList) {
        int status = 0;
        for (String speech : speechList) {
            speech = speech.toLowerCase();

            status = understandSpeech(speech);

            if (status != CONTINUE_TRYING_TO_UNDERSTAND) {
                break;
            }
        }
        return status;
    }


    private int understandSpeech(String speech) {

        //general actions
        if (Meaning.contains(Meaning.STOP, speech)) {
            return STOP_DIALOG;
        }
        if (Meaning.contains(Meaning.TEACH, speech)) {
            startActivity(new Intent(this, NeedListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return UNDERSTOOD_CONTINUE_DIALOG;
        }

        //need actions

        //reject need
        if (currentNeed != null && Meaning.equals(Meaning.NO, speech)) {
            currentNeed.lastRejectDate = System.currentTimeMillis();
            currentNeed = null;
            return UNDERSTOOD_CONTINUE_DIALOG;
        }

        //provide need
        if (currentNeed != null
                && currentNeed.isNeedReadyToExecute()
                && currentNeed.isApproved
                && Meaning.equals(Meaning.OK, speech)) {
            return PROVIDE_NEED;
        }

        //approve need
        if (currentNeed != null && Meaning.equals(Meaning.OK, speech)) {
            currentNeed.isApproved = true;
            return UNDERSTOOD_CONTINUE_DIALOG;
        }


        //property actions

        //skip property
        if (currentNeed != null && currentProperty != null && Meaning.contains(Meaning.NEXT, speech)) {
            currentProperty.lastRejectDate = System.currentTimeMillis();
            currentProperty = null;
            return UNDERSTOOD_CONTINUE_DIALOG;
        }

        //eventually, set the value of the property
        if (currentProperty != null) {
            currentProperty.value = speech;
            return UNDERSTOOD_CONTINUE_DIALOG;
        }

        return CONTINUE_TRYING_TO_UNDERSTAND;
    }

    /*public void fillNeed(Need need) {

        need.properties.get(0).value = "robin";
        need.properties.get(1).value = "ben";
        need.properties.get(2).value = "oded";
        need.properties.get(3).value = "test";

        provideNeed(need);
    }*/

    void whatsNext(TextToSpeechListener ttsl) {

        String textToShow = null;
        String textToRead = null;
        if (currentNeed != null && currentNeed.isNeedReadyToExecute()) {
            textToShow = currentNeed.name;
            textToRead = "OK, we have " + currentNeed.describeContent() + ". lets " + currentNeed.name + " right now, OK?";
        } else if (currentNeed == null) {

            currentNeed = needsInMemory.get((new Random()).nextInt(needsInMemory.size() - 1));

            for (Need need : needsInMemory) {

                if (need.lastRejectDate < currentNeed.lastRejectDate) {
                    currentNeed = need;
                }
            }
            textToShow = currentNeed.name;
            textToRead = "Would you like to " + currentNeed.name;

        } else if (currentNeed != null && currentNeed.isApproved) {
            Property property = currentNeed.getNextProperty();
            currentProperty = property;
            textToShow = currentProperty.getQuestion();
            textToRead = currentProperty.getQuestion();
        } else if (currentNeed != null && !currentNeed.isApproved) {
            textToShow = currentNeed.name;
            textToRead = "Would you like to " + currentNeed.name;
        }


        if (textToShow != null && textToRead != null) {
            ttsl.speakOut(textToShow, textToRead);
        } else {
            Log.e("Error TTS", "Sent null to TTS engine");
        }

    }


    boolean provideNeed(Need need) {

        String stringUri = new String(need.uri);

        for (int i = 0; i < need.properties.size(); i++) {
            int sIndex = i + 1;
            String value = need.properties.get(i).value;
            if (value != null) {
                stringUri = stringUri.replace("Q" + sIndex, value);
            } else {
                stringUri = stringUri.replace("Q" + sIndex, "");
            }
        }

        currentNeed = null;

        Uri uri = Uri.parse(stringUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);

        return true;
    }

}
