package com.example.index.types;

import java.io.Serializable;
import java.util.HashMap;

public class DocumentMetaItem implements Serializable {
    public Integer contentLength;
    public HashMap<Integer, Integer> fieldLengths;

    public DocumentMetaItem(Integer contentLength, HashMap fieldLengths) {
        this.contentLength = contentLength;
        this.fieldLengths = fieldLengths;
    }
}
