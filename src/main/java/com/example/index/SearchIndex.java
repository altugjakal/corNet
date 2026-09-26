package com.example.index;

import com.example.index.types.ApiTokenItem;
import com.example.index.types.DictPostingPair;
import com.example.index.types.OffsetItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SearchIndex {
    private DictionaryRouter dictionaryRouter;


    public SearchIndex(DictionaryRouter dictionaryRouter) {
        this.dictionaryRouter = dictionaryRouter;

    }

    public List<ApiTokenItem> searchByTokens(List<String> tokens) {
        //combine idf's somehow?? read more -read more, use vector model on the other end - done
        List<ApiTokenItem> docs = new ArrayList<>();
        List<String> uniqueTokens = tokens.stream()
                .distinct()
                .toList();




        for (int i = 0; i < dictionaryRouter.dictionaries.size(); i++) {
            DictPostingPair pair = this.dictionaryRouter.dictionaries.get(i);

            for (String token: uniqueTokens ) {

                Integer offset = pair.dictionary.getOffset(token);
                System.out.println(offset);


                if (offset == null) {
                    continue;
                }

                ApiTokenItem tokenItem = new ApiTokenItem();
                tokenItem.token = token;

                try {

                    List<OffsetItem> offsetItems = pair.postingsList.getByOffset(offset, pair.postingsList.lastSavePath);
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