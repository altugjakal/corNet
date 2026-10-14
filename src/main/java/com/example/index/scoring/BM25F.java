package com.example.index.scoring;

import com.example.index.DocumentMetaIndex;

public class BM25F {

    private static float idf(int df, int indexSize) {

        return (float) Math.log(1 + (indexSize - df + 0.5D) / (df + 0.5D));
        //brong
    }

    public static double score(int tf, int df, double b, double k, float averageDocumentFrequency, int fieldLength, float filedWeight, int indexSize) {
       float idf = idf(df, indexSize);



       double lenNorm = 1.0 - b + (b * (fieldLength / averageDocumentFrequency));
       double weightedTf = tf * filedWeight;
       double bm25f = idf * (weightedTf / (weightedTf + k * lenNorm));




       return bm25f;


    }
}
