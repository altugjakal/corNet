package com.example.index.scoring;

public class TfIdf {

    public static float score(float tf, int df) {
        return (float) (tf * Math.log(1/df));
    }
}
