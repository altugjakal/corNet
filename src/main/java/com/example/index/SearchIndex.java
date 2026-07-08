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

    public List<ApiTokenItem> searchByTokens(List<String> tokens) {
        //combine idf's somehow?? read more -read more use vector model on the other end
        List<ApiTokenItem> docs = new ArrayList<>();
        for (String token: tokens ) {
            Integer offset = this.dictionary.getOffset(token);


            if (offset == null) {
                continue;
            }

            ApiTokenItem tokenItem = new ApiTokenItem();
            tokenItem.token = token;

            try {

                List<OffsetItem> offsetItems = this.postingsList.getByOffset(offset);

                int df = offsetItems.size();
                // this is here for demonstration, each token items postingItems size gives the df



                for (OffsetItem offsetItem : offsetItems) {
                    tokenItem.postingItems.add(offsetItem.postingItem);


                }



                docs.add(tokenItem);


            } catch (IOException e) {
                assert true;
            }






        }
        return docs;
    }
    
}