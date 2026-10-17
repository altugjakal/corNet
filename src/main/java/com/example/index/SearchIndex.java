package com.example.index;

import com.example.index.scoring.BM25F;
import com.example.index.scoring.TfIdf;
import com.example.index.types.*;
import com.example.index.utils.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class SearchIndex {
    private DictionaryRouter dictionaryRouter;
    public DocumentMetaIndex documentMetaIndex;
    public GlobalStatistics globalStatistics;


    public SearchIndex(DictionaryRouter dictionaryRouter, DocumentMetaIndex documentMetaIndex, GlobalStatistics globalStatistics) {
        this.dictionaryRouter = dictionaryRouter;
        this.documentMetaIndex = documentMetaIndex;
        this.globalStatistics = globalStatistics;

    }

    public HashMap<Integer, Document> searchByTokens(List<String> tokens) {
        //combine idf's somehow?? read more -read more, use vector model on the other end - done
        List<String> uniqueTokens = tokens.stream()
                .distinct()
                .toList();



        HashMap<Integer, Document> documentScores = new HashMap<>();
        for (int i = 0; i < dictionaryRouter.dictionaries.size(); i++) {
            DictPostingPair pair = this.dictionaryRouter.dictionaries.get(i);

            for (String token: uniqueTokens ) {

                Integer offset = pair.dictionary.getOffset(token);


                if (offset == null) {
                    continue;
                }



                try {

                    List<OffsetItem> offsetItems = pair.postingsList.getByOffset(offset, pair.postingsList.lastSavePath);
                    int df = offsetItems.size();
                    int indexSize = documentMetaIndex.getIndexSize();


                    for (OffsetItem offsetItem : offsetItems) {
                        DocumentMetaItem documentMetaItem = documentMetaIndex.get(offsetItem.postingItem.docId);
                        float tf = offsetItem.postingItem.hits.size() / documentMetaIndex.get(offsetItem.postingItem.docId).contentLength;


                        /*not too
                        * happy of these double loops here get rid of them when better solution*/

                        HashMap<Integer, Integer> fieldTermFrequencies = new HashMap<>();
                        for (HitItem hitItem: offsetItem.postingItem.hits)
                        {
                            fieldTermFrequencies.put(hitItem.fieldId, fieldTermFrequencies.getOrDefault(hitItem.fieldId, 0) + 1);
                        }


                        for (var entry : fieldTermFrequencies.entrySet())
                        {
                            int fieldTermFrequency = entry.getValue();
                            float avgFieldLength = globalStatistics.getFieldAverage(entry.getKey());
                            int fieldLength = documentMetaItem.fieldLengths.get(entry.getKey());
                            float fieldWeight = Config.getFieldWeight(entry.getKey());
                            double bm25Score = BM25F.score(fieldTermFrequency, df, 1, 0.85, avgFieldLength, fieldLength, fieldWeight, indexSize);

                            documentScores.computeIfAbsent(offsetItem.postingItem.docId, id -> new Document(id, 0d)).bm25Score += bm25Score;
                        }


                        ScoredPostingItem scoredPostingItem = new ScoredPostingItem();
                        scoredPostingItem.broadTermWeight = TfIdf.score(tf, df); //not used yet

                        scoredPostingItem.hits = offsetItem.postingItem.hits;
                        scoredPostingItem.docId = offsetItem.postingItem.docId;


                    }



                } catch (IOException e) {
                    assert true;
                }

            }
        }



        return documentScores;
    }
    
}