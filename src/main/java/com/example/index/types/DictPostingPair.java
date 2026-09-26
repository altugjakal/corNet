package com.example.index.types;

import com.example.index.Dictionary;
import com.example.index.PostingsList;

import java.io.Serializable;

public class DictPostingPair implements Serializable {
    public Dictionary dictionary;
    public PostingsList postingsList;

    public DictPostingPair(Dictionary dictionary, PostingsList postingsList) {
        this.dictionary = dictionary;
        this.postingsList = postingsList;
    }
}