package com.example.index;

import org.springframework.scheduling.annotation.Async;

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

    @Async
    public synchronized void commit() {
        try {
            // Order matters here, don't fuck it up
            this.postingsList.save();
            this.dictionary.save();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
