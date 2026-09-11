package com.example.index;

import java.util.ArrayList;

public class AutoMerger {
    private DictionaryRouter dictionaryRouter;

    public void merge(DictionaryRouter dictionaryRouter) {
        //implement two pointer

        Dictionary newDictionary = new Dictionary();
        PostingsList newPostingslist = new PostingsList(newDictionary);



        ArrayList<Dictionary.DictItem> dictionaryOne = dictionaryRouter.dictionaries.get(0).items;
        ArrayList<Dictionary.DictItem> dictionaryTwo = dictionaryRouter.dictionaries.get(1).items;

        int i;
        int j;
        for(i = 0; i < dictionaryOne.toArray().length; i++) {
            for(j = 0; j < dictionaryTwo.toArray().length; j++) {
                int difference = dictionaryOne.get(i).token.compareTo(dictionaryTwo.get(j).token);

                // TODO: compare negative positive - when this was written, it was already implemented
                //I left that there because the color linting in intellij is so cool

                if(difference < 0) {

                    dictionaryRouter.
                    i++;

                }
                else if(difference > 0) {
                    j++;
                } else {
                    i++;
                    j++;

                }



            }

        }




    }
}
