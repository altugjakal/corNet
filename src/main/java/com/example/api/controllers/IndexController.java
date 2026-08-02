package com.example.api.controllers;

import com.example.index.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class IndexController {
    private Dictionary dictionary = new Dictionary("src/files/dictionary.bin");
    private PostingsList postingsList = new PostingsList(dictionary, "src/files/postings.bin");
    private IndexWriter indexWriter = new IndexWriter(dictionary, postingsList);

    private final ExecutorService commitExecutor = Executors.newSingleThreadExecutor();


    public IndexController() {
        dictionary.load();
    }

    public static class InsertRequest {
        public Integer docId;
        public Map<String, List<HitItem>> pairs;

        public InsertRequest() {}

    }

    @PostMapping("/insert")
    public void index(@RequestBody InsertRequest insertRequest) {

        Map<String, List<HitItem>> pairs = insertRequest.pairs;

        indexWriter.write(insertRequest.docId, pairs);


    }

    @PostMapping("/commit")
    public ResponseEntity<Void> index() {

        commitExecutor.submit(() -> {
            try {
                indexWriter.commit();
            } catch (Exception e) {
                System.err.println("Failed to execute heavy commit: " + e.getMessage());
            }
        });
        return ResponseEntity.accepted().build();

    }

    @GetMapping("/search/{query}")

    public List<ApiTokenItem> index(@PathVariable String query) {



        String[] tokens = query.split(" ");

        SearchIndex searchIndex = new SearchIndex(dictionary, postingsList);

        List<String> terms = new ArrayList<String>(Arrays.asList(tokens));


        return searchIndex.searchByTokens(terms);


    }



}