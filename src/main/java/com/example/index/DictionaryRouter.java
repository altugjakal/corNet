package com.example.index;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DictionaryRouter {

    public static class DictPostingPair {
        public Dictionary dictionary;
        public PostingsList postingsList;

        public DictPostingPair(Dictionary dictionary, PostingsList postingsList) {
            this.dictionary = dictionary;
            this.postingsList = postingsList;
        }

    }

    public ConcurrentHashMap<Integer, DictPostingPair> dictionaries = new ConcurrentHashMap<>();


    public DictionaryRouter() {
        File dictDir = new File("src/files/dicts/");
        File postingDir = new File("src/files/postings/");

        File[] dictFileListings = dictDir.listFiles();
        File[] postingFileListings = postingDir.listFiles();


        if (dictFileListings == null || dictFileListings.length == 0) {
            return;
        }
        else {
            Arrays.sort(dictFileListings);
            Arrays.sort(postingFileListings);
        }

        for(int i = 0; i < dictFileListings.length; i++){
            Dictionary dictionary = new Dictionary();
            dictionary.load(dictFileListings[i].getPath());
            PostingsList postingsList = new PostingsList(dictionary);
            postingsList.lastSavePath = postingFileListings[i].getPath();
            DictPostingPair dictPostingPair = new DictPostingPair(dictionary, postingsList);
            submit(i, dictPostingPair);
        }

    }

    public void submit(Integer id, DictPostingPair dictPostingPair) {
        dictionaries.put(id, dictPostingPair);
    }

    public void delete(Integer id) {dictionaries.remove(id);}


}
