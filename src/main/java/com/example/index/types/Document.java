package com.example.index.types;

public class Document {
    public Integer docId;
    public Double bm25Score;

    public Document(Integer docId, Double bm25Score) {
        this.docId = docId;
        this.bm25Score = bm25Score;

    }

}
