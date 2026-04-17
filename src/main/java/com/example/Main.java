package com.example;

import com.example.index.*;

import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Dictionary dictionary = new Dictionary("src/files/dictionary.bin");
        PostingsList postingsList = new PostingsList(dictionary, "src/files/postings.bin");
        dictionary.load();
        IndexWriter indexWriter = new IndexWriter(dictionary, postingsList);
        SearchIndex searchIndex = new SearchIndex(dictionary, postingsList);
        indexWriter.write(23435767, "crumbs", (float) 0);
        indexWriter.commit();

        List<String> terms = new ArrayList<String>();
        terms.add("hi");



        List<PostingItem> results = searchIndex.searchByTokens(terms);
        for (int i=0; i < results.size(); i++) {
            System.out.println(results.get(0).docId);
        }
        long endTime = System.nanoTime();

        System.out.println(String.valueOf((endTime - startTime)/1000000) + "ms");


    }
}