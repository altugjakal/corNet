package com.example.index.types;

import java.io.Serializable;
import java.util.ArrayList;

public class PostingItem implements Serializable {
    public Integer docId;
    public ArrayList<HitItem> hits = new ArrayList<>();

}
