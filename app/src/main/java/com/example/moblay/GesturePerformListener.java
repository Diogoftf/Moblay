package com.example.moblay;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.widget.Toast;

import java.util.ArrayList;

public class GesturePerformListener implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary gestureLibrary = null;
    private Context context;

    public GesturePerformListener(GestureLibrary gestureLibrary, Context context) {
        this.gestureLibrary = gestureLibrary;
        this.context = context;
    }

    /* When GestureOverlayView widget capture a user gesture it will run the code in this method.
       The first parameter is the GestureOverlayView object, the second parameter store user gesture information.*/
    @Override
    public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {

        // Recognize the gesture and return prediction list.
        ArrayList<Prediction> predictionList = gestureLibrary.recognize(gesture);

        int size = predictionList.size();

        if(size > 0)
        {
            StringBuffer messageBuffer = new StringBuffer();

            // Get the first prediction.
            Prediction firstPrediction = predictionList.get(0);

            /* Higher score higher gesture match. */
            if(firstPrediction.score > 1)
            {
                String action = firstPrediction.name;

                messageBuffer.append("Your gesture match " + action);
            }else
            {
                messageBuffer.append("Your gesture do not match any predefined gestures.");
            }

            // Display a snackbar with related messages.
            Toast toast = Toast.makeText(this.context, messageBuffer.toString(), Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
