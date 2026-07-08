package com.example;

import com.example.index.*;

import java.util.ArrayList;
import java.util.HashMap; // Added import
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Dictionary dictionary = new Dictionary("src/files/dictionary.bin");
        PostingsList postingsList = new PostingsList(dictionary, "src/files/postings.bin");
        dictionary.load();
        IndexWriter indexWriter = new IndexWriter(dictionary, postingsList);
        SearchIndex searchIndex = new SearchIndex(dictionary, postingsList);

        for (int i = 0; i < 10; i++){

            Map<String, List<HitItem>> map = new HashMap<>();

            List<HitItem> hits = new ArrayList<>();
            HitItem hit = new HitItem();
            hit.weight = 5;
            hit.position = i * 23;
            hits.add(hit);

            map.put("hi", hits);
            indexWriter.write(i, map);

        }




        indexWriter.commit();

        List<String> terms = new ArrayList<String>();
        terms.add("hi");

        List<ApiTokenItem> results = searchIndex.searchByTokens(terms);



        for (int i = 0; i < results.size(); i++) {

            System.out.println(results.get(i).token);
            for (int j = 0; j < results.get(i).postingItems.size(); j++) {
                System.out.println(results.get(i).postingItems.get(j).docId);
                System.out.println(results.get(i).postingItems.size());

            }
        }

        long endTime = System.nanoTime();
        System.out.println(String.valueOf((endTime - startTime)/1000000) + "ms");
    }
}
