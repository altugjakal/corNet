package com.example.index.scoring;

import com.example.index.DocumentMetaIndex;

public class BM25F {

    private static float idf(int df) {
        return (float) Math.log(1 + (DocumentMetaIndex.getIndexSize() - df + 0.5D)/(df + 0.5D));
    }

    public static float score(int tf, int df, float b, float k, float avgdf) {
       float idf = idf(df);
       float bm25f = idf * ((tf*(1+k)) / (tf + b * (df/avgdf) ));

       return bm25f;


    }
}
