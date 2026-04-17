package com.example.index;

public class IndexWriter {
    private Dictionary dictionary;
    private PostingsList postingsList;

    public IndexWriter(Dictionary dictionary, PostingsList postingsList) {
        this.dictionary = dictionary;
        this.postingsList = postingsList;

    }



    public void write(Integer docId, String token, Float score) {
        this.postingsList.add(token, docId, score);

    }

    public void commit() {
        this.postingsList.save(); //order matters here
        this.dictionary.save();

    }

}
