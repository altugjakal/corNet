package com.example.api.controllers;

import com.example.index.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
    public void index() {
        indexWriter.commit();

    }

    @GetMapping("/search/{query}")

    public List<ApiTokenItem> index(@PathVariable String query) {


//its idf time baby
        String[] tokens = query.split(" ");

        SearchIndex searchIndex = new SearchIndex(dictionary, postingsList);


        List<String> terms = new ArrayList<String>(Arrays.asList(tokens));


        return searchIndex.searchByTokens(terms);


    }



}