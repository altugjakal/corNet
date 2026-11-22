package com.example.index.types;

import java.io.Serializable;
import java.util.HashMap;

public class DocumentMetaItem implements Serializable {
    public Integer contentLength;
    public HashMap<Integer, Integer> fieldLengths;
    public boolean isMeta;

    public DocumentMetaItem(Integer contentLength, HashMap fieldLengths, boolean isMeta) {
        this.contentLength = contentLength;
        this.fieldLengths = fieldLengths;
        this.isMeta = isMeta;
    }
}
