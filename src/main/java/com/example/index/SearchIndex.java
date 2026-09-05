package com.example.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class SearchIndex {
    private ConcurrentHashMap<Integer, DictPostingPair> dictionaries;
    private DictionaryRouter dictionaryRouter;

    public static class DictPostingPair {
        public Dictionary dictionary;
        public PostingsList postingsList;

        public DictPostingPair(Dictionary dictionary, PostingsList postingsList) {
            this.dictionary = dictionary;
            this.postingsList = postingsList;
        }

    }


    public SearchIndex(DictionaryRouter dictionaryRouter) {
        this.dictionaryRouter = dictionaryRouter;
        this.dictionaries = new ConcurrentHashMap<>();


        for (int i = 0; i < this.dictionaryRouter.dictionaries.size(); i++) {
            Dictionary dictionary = dictionaryRouter.dictionaries.get(i);
            PostingsList postingsList = new PostingsList(dictionary);
            DictPostingPair pair = new DictPostingPair(dictionary, postingsList);

            this.dictionaries.put(i, pair);
        }



    }

    public List<ApiTokenItem> searchByTokens(List<String> tokens) {
        //combine idf's somehow?? read more -read more, use vector model on the other end - done
        List<ApiTokenItem> docs = new ArrayList<>();
        List<String> uniqueTokens = tokens.stream()
                .distinct()
                .toList();


        File postingsDir = new File("src/files/postings/");

        File[] postingFileListings = postingsDir.listFiles();
        Arrays.sort(postingFileListings);

        for (int i = 0; i < dictionaries.size(); i++) {
            DictPostingPair pair = this.dictionaries.get(i);

            for (String token: uniqueTokens ) {

                Integer offset = pair.dictionary.getOffset(token);


                if (offset == null) {
                    continue;
                }

                ApiTokenItem tokenItem = new ApiTokenItem();
                tokenItem.token = token;

                try {

                    List<OffsetItem> offsetItems = pair.postingsList.getByOffset(offset, postingFileListings[i].getPath());
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
        }
        return docs;
    }
    
}