package com.example.index;

import com.example.index.scoring.TfIdf;
import com.example.index.types.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SearchIndex {
    private DictionaryRouter dictionaryRouter;
    public DocumentMetaIndex documentMetaIndex;


    public SearchIndex(DictionaryRouter dictionaryRouter, DocumentMetaIndex documentMetaIndex) {
        this.dictionaryRouter = dictionaryRouter;
        this.documentMetaIndex = documentMetaIndex;

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


                    for (OffsetItem offsetItem : offsetItems) {
                        DocumentMetaItem documentMetaItem = DocumentMetaIndex.get(offsetItem.postingItem.docId);
                        float tf = offsetItem.postingItem.hits.size() / DocumentMetaIndex.get(offsetItem.postingItem.docId).contentLength;
                        for (HitItem hitItem: offsetItem.postingItem.hits)
                        {
                            int fieldLength = documentMetaItem.fieldLengths.get(hitItem.weight);
                            int fieldWeight = hitItem.weight;
                        }
                        ScoredPostingItem scoredPostingItem = new ScoredPostingItem();
                        scoredPostingItem.broadTermWeight = TfIdf.score(tf, df); //encapsulate this - done
                        scoredPostingItem.hits = offsetItem.postingItem.hits;
                        scoredPostingItem.docId = offsetItem.postingItem.docId;

                        tokenItem.postingItems.add(scoredPostingItem);

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