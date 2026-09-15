package com.example.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutoMerger {
    private DictionaryRouter dictionaryRouter;
    private IndexWriter indexWriter;

    public AutoMerger(IndexWriter indexWriter, DictionaryRouter dictionaryRouter) {
        this.indexWriter = indexWriter;
        this.dictionaryRouter = dictionaryRouter;
    }

    public void merge( int fileOrderOne, int fileOrderTwo) throws IOException {
        //implement two pointer, file ikds are currently hasrdcoded, get file index as parameter


        indexWriter.decrementSaveCount();
        Dictionary newDictionary = new Dictionary();
        PostingsList newPostingslist = new PostingsList(newDictionary);

        if (dictionaryRouter.dictionaries.size() < 2) {
            throw new FileNotFoundException();
        }


        DictionaryRouter.DictPostingPair dictionaryOne = dictionaryRouter.dictionaries.get(fileOrderOne);
        DictionaryRouter.DictPostingPair dictionaryTwo = dictionaryRouter.dictionaries.get(fileOrderTwo);




        int i = 0;
        int j = 0;
        try {
            while (i < dictionaryOne.dictionary.items.size() || j < dictionaryTwo.dictionary.items.size()) {

                if (i >= dictionaryOne.dictionary.items.size()) {
                    String wordTwo = dictionaryTwo.dictionary.items.get(j).token;
                    int offsetTwo = dictionaryTwo.dictionary.getOffset(wordTwo);
                    List<OffsetItem> offsetItemsTwo = dictionaryTwo.postingsList.getByOffset(offsetTwo, null);
                    for (OffsetItem offsetItem : offsetItemsTwo) {
                        newPostingslist.add(offsetItem.postingItem.docId, Map.of(wordTwo, offsetItem.postingItem.hits));
                    }
                    j++;
                    continue;
                }

                if (j >= dictionaryTwo.dictionary.items.size()) {
                    String wordOne = dictionaryOne.dictionary.items.get(i).token;
                    int offsetOne = dictionaryOne.dictionary.getOffset(wordOne);
                    List<OffsetItem> offsetItemsOne = dictionaryOne.postingsList.getByOffset(offsetOne, null);
                    for (OffsetItem offsetItem : offsetItemsOne) {
                        newPostingslist.add(offsetItem.postingItem.docId, Map.of(wordOne, offsetItem.postingItem.hits));
                    }
                    i++;
                    continue;
                }

                String wordOne = dictionaryOne.dictionary.items.get(i).token;
                String wordTwo = dictionaryTwo.dictionary.items.get(j).token;
                int difference = wordOne.compareTo(wordTwo);


                if (difference < 0) {
                    int offset = dictionaryOne.dictionary.getOffset(wordOne);
                    System.out.println(dictionaryOne.postingsList.lastSavePath + offset + wordOne);
                    List<OffsetItem> offsetItems = dictionaryOne.postingsList.getByOffset(offset, null);
                    for (OffsetItem offsetItem : offsetItems) {
                        newPostingslist.add(offsetItem.postingItem.docId, Map.of(wordOne, offsetItem.postingItem.hits));
                    }
                    i++;


                } else if (difference > 0) {
                    int offset = dictionaryTwo.dictionary.getOffset(wordTwo);
                    System.out.println(dictionaryTwo.postingsList.lastSavePath + offset + wordTwo);
                    List<OffsetItem> offsetItems = dictionaryTwo.postingsList.getByOffset(offset, null);

                    for (OffsetItem offsetItem : offsetItems) {
                        newPostingslist.add(offsetItem.postingItem.docId, Map.of(wordTwo, offsetItem.postingItem.hits));
                    }
                    j++;

                } else {
                    //in the final write use the smaller number so it stacks up from 0 again instead of 1
                    int offsetOne = dictionaryOne.dictionary.getOffset(wordOne);
                    int offsetTwo = dictionaryTwo.dictionary.getOffset(wordTwo);
                    List<OffsetItem> offsetItemsOne = dictionaryOne.postingsList.getByOffset(offsetOne, null);
                    List<OffsetItem> offsetItemsTwo = dictionaryTwo.postingsList.getByOffset(offsetTwo, null);


                    // TODO: TIDY UP THIS FUNCTION + remove attribute pulls and use local variables and test it at the end

                    //offset item docid and hits combine udner docid
                    int x = 0;
                    int y = 0;
                    while (x < offsetItemsOne.size() || y < offsetItemsTwo.size()) {
                        System.out.println(wordOne);

                        if (x >= offsetItemsOne.size()) {
                            newPostingslist.add(
                                    offsetItemsTwo.get(y).postingItem.docId,
                                    Map.of(wordOne, offsetItemsTwo.get(y).postingItem.hits)
                            );
                            y++;
                            continue;
                        }

                        if (y >= offsetItemsTwo.size()) {
                            newPostingslist.add(
                                    offsetItemsOne.get(x).postingItem.docId,
                                    Map.of(wordOne, offsetItemsOne.get(x).postingItem.hits)
                            );
                            x++;
                            continue;
                        }



                            if (offsetItemsOne.get(x).postingItem.docId < offsetItemsTwo.get(y).postingItem.docId) {
                                newPostingslist.add(
                                        offsetItemsOne.get(x).postingItem.docId,
                                        Map.of(wordOne, offsetItemsOne.get(x).postingItem.hits)
                                );
                                x++;
                            } else if(offsetItemsOne.get(x).postingItem.docId > offsetItemsTwo.get(y).postingItem.docId) {
                                newPostingslist.add(
                                        offsetItemsTwo.get(y).postingItem.docId,
                                        Map.of(wordOne, offsetItemsTwo.get(y).postingItem.hits)
                                );
                                y++;
                            } else {
                                OffsetItem newOffsetItem = new OffsetItem();
                                newOffsetItem.postingItem.docId = offsetItemsOne.get(x).postingItem.docId;
                                ArrayList<HitItem> tempHits = new ArrayList<>(offsetItemsTwo.get(y).postingItem.hits);
                                tempHits.addAll(offsetItemsOne.get(x).postingItem.hits);
                                newOffsetItem.postingItem.hits = tempHits;
                                newPostingslist.add(
                                        offsetItemsTwo.get(y).postingItem.docId,
                                        Map.of(wordOne, tempHits)
                                );
                                x++;
                                y++;
                            }


                    }
                    i++;
                    j++;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }


        String postingFileName = "_" + fileOrderOne + ".bin";
        String dictFileName = "_" + fileOrderOne + ".dic";

        File extinctDictFile = new File("src/files/dicts/" + "_" + fileOrderTwo + ".dic");
        File extinctPostingsFile =  new File("src/files/postings/" + "_" + fileOrderTwo + ".bin");
        extinctPostingsFile.delete();
        extinctDictFile.delete();



        newPostingslist.save("src/files/postings/" + postingFileName);
        newDictionary.save("src/files/dicts/" + dictFileName);


        DictionaryRouter.DictPostingPair dictPostingPair = new DictionaryRouter.DictPostingPair(newDictionary, newPostingslist);
        dictionaryRouter.submit(fileOrderOne, dictPostingPair);
        dictionaryRouter.delete(fileOrderTwo);

    }
}
