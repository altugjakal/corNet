package com.example.index;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DictionaryRouter {
    public ConcurrentHashMap<Integer, Dictionary> dictionaries = new ConcurrentHashMap<>();


    public DictionaryRouter() {
        File postingsDir = new File("src/files/dicts/");

        File[] postingFileListings = postingsDir.listFiles();

        if (postingFileListings == null || postingFileListings.length == 0) {
            return;
        }

        for(int i = 0; i < postingFileListings.length; i++){
            Dictionary dictionary = new Dictionary();
            dictionary.load(postingFileListings[i].getPath());
            submit(i, dictionary);
        }

    }

    public void submit(Integer id, Dictionary dictionary) {
        dictionaries.put(id, dictionary);
    }


}
