package com.example.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchIndex {
    private Dictionary dictionary;
    private PostingsList postingsList;

    public SearchIndex(Dictionary dictionary, PostingsList postingsList) {
        this.dictionary = dictionary;
        this.postingsList = postingsList;

    }

    public List<PostingItem> searchByTokens(List<String> tokens) {
        List<Integer> offsets = this.dictionary.getOffsets(tokens);

        List<PostingItem> docs = new ArrayList<PostingItem>();
        for(int i=0; i < offsets.size(); i++){
            try {
                docs.addAll(this.postingsList.getByOffset(offsets.get(i)));

            } catch (IOException e) {
                assert true;
            }
        }

        return docs;
    }
}
