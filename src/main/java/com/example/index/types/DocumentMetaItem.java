package com.example.index.types;

import java.util.HashMap;

public class DocumentMetaItem {
    public Integer contentLength;
    public HashMap<Integer, Integer> fieldLengths;

    public DocumentMetaItem(Integer contentLength, HashMap fieldLengths) {
        this.contentLength = contentLength;
        this.fieldLengths = fieldLengths;
    }
}
