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
    private DictionaryRouter dictionaryRouter = new DictionaryRouter();
    private IndexWriter indexWriter = new IndexWriter(dictionaryRouter);

    private final ExecutorService commitExecutor = Executors.newSingleThreadExecutor();


    public IndexController() {


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
    public void index() {

        indexWriter.emergencyDump();


    }



    @GetMapping("/search/{query}")

    public List<ApiTokenItem> index(@PathVariable String query) {

        SearchIndex searchIndex = new SearchIndex(dictionaryRouter);


        String[] tokens = query.split(" ");



        List<String> terms = new ArrayList<String>(Arrays.asList(tokens));


        return searchIndex.searchByTokens(terms);


    }



}