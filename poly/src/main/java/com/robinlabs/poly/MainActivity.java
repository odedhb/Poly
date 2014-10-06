package com.robinlabs.poly;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1987;
    TextToSpeechListener textToSpeechListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Need> allNeeds = Storage.getAllNeeds(this);

        if (allNeeds.size() > 0) {
            TheBrainer.instance.needsInMemory = allNeeds;
        } else {
            for (Need n : TheBrainer.instance.getAllHardcodedNeeds()) {
                n.saveToStorage(this);
            }
        }

        textToSpeechListener = new TextToSpeechListener(this);

        ((AudioManager) getSystemService(AUDIO_SERVICE)).registerMediaButtonEventReceiver(
                new ComponentName(this, MediaButtonIntentReceiver.class.getCanonicalName()));

        findViewById(R.id.words).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TheBrainer.instance.whatsNext(textToSpeechListener);
            }
        });
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        TheBrainer.instance.whatsNext(tts);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeechListener.shutDown();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("Speech", "GOT SPEECH RESULT " + resultCode + " req: "
                + requestCode);
        int status = 0;
        if (requestCode == MainActivity.VOICE_RECOGNITION_REQUEST_CODE) {
            if (resultCode == MainActivity.RESULT_OK) {
                ArrayList<String> matches = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.d("voice", "matches: ");
                for (String match : matches) {
                    Log.d("voice_match", match);
                }

                status = TheBrainer.instance.useSpokenWords(matches);
            }
        }

        if (status == TheBrainer.UNDERSTOOD_CONTINUE_DIALOG) {
            TheBrainer.instance.whatsNext(textToSpeechListener);
        } else if (status == TheBrainer.STOP_DIALOG) {
            //do nothing
            Toast.makeText(this, "Dialog was stopped by the user", Toast.LENGTH_SHORT).show();
        } else if (status == TheBrainer.PROVIDE_NEED) {
            TheBrainer.instance.provideNeed(TheBrainer.instance.currentNeed);
        }
    }

}
