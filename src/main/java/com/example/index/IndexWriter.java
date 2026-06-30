package com.example.index;

import java.util.List;
import java.util.Map;

public class IndexWriter {
    private Dictionary dictionary;
    private PostingsList postingsList;

    public IndexWriter(Dictionary dictionary, PostingsList postingsList) {
        this.dictionary = dictionary;
        this.postingsList = postingsList;

    }



    public void write(int docId, Map<String, List<HitItem>> pairs) {
        this.postingsList.add(docId, pairs);

    }

    public void commit() {
        this.postingsList.save(); //order matters here, don't fuck it up
        this.dictionary.save();

    }

}
