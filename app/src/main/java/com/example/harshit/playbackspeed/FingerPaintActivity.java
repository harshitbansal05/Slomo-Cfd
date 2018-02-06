package com.example.harshit.playbackspeed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class FingerPaintActivity extends AppCompatActivity implements DrawListener{

    private PaintCustomView paintCustomView;
    private float[] speed = null;
    private List<Float> xList;
    private List<Float> yList;
    private Float[] xArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_paint);

        paintCustomView = (PaintCustomView) findViewById(R.id.paint_custom_view);

        if (getIntent().getExtras() != null){
            paintCustomView.setDuration((Integer) getIntent().getExtras().get("duration"));
            speed = new float[(Integer) getIntent().getExtras().get("duration") + 1];
        }
    }

    @Override
    public void setSpeedArray(List<float[]> speeds) {
        for (int i = 0; i < speed.length; i++){
            float mapped = (float) (i * 349.36523 / speed.length);
            float nearestValue = findClosest(xArray, mapped);
            float y = yList.get(xList.indexOf(nearestValue));
            speed[i] = 4 * ((400 - y) / 400);
        }
        MainActivity.speedArray = speed;
        finish();
    }

    @Override
    public void setXList(List<Float> x) {
        xList = x;
        xArray = xList.toArray(new Float[xList.size()]);
    }

    @Override
    public void setYList(List<Float> y) {
        yList = y;
    }

    // Returns element closest to target in arr[]
    public static float findClosest(Float arr[], float target)
    {
        int n = arr.length;

        // Corner cases
        if (target <= arr[0])
            return arr[0];
        if (target >= arr[n - 1])
            return arr[n - 1];

        // Doing binary search
        int i = 0, j = n, mid = 0;
        while (i < j) {
            mid = (i + j) / 2;

            if (arr[mid] == target)
                return arr[mid];

            /* If target is less than array element,
               then search in left */
            if (target < arr[mid]) {

                // If target is greater than previous
                // to mid, return closest of two
                if (mid > 0 && target > arr[mid - 1])
                    return getClosest(arr[mid - 1],
                            arr[mid], target);

                /* Repeat for left half */
                j = mid;
            }

            // If target is greater than mid
            else {
                if (mid < n-1 && target < arr[mid + 1])
                    return getClosest(arr[mid],
                            arr[mid + 1], target);
                i = mid + 1; // update i
            }
        }

        // Only single element left after search
        return arr[mid];
    }

    // Method to compare which one is the more close
    // We find the closest by taking the difference
    //  between the target and both values. It assumes
    // that val2 is greater than val1 and target lies
    // between these two.
    public static float getClosest(float val1, float val2,
                                 float target)
    {
        if (target - val1 >= val2 - target)
            return val2;
        else
            return val2;
    }
}
