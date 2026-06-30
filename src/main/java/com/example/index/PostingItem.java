package com.example.index;

import java.io.Serializable;
import java.util.ArrayList;

public class PostingItem implements Serializable {
    public Integer docId;
    public ArrayList<HitItem> hits = new ArrayList<>();

}
