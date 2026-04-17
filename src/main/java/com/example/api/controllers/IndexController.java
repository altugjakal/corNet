package com.example.api.controllers;

import com.example.index.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class IndexController {
    private Dictionary dictionary = new Dictionary("src/files/dictionary.bin");
    private PostingsList postingsList = new PostingsList(dictionary, "src/files/postings.bin");

    public IndexController() {
        dictionary.load();
    }

    public static class InsertRequest {
        public Integer docId;
        public String text;
        public Float score;

        public InsertRequest() {}

    }

    @PostMapping("/insert")
    public String index(@RequestBody InsertRequest insertRequest) {
        String[] tokens = insertRequest.text.split(" ");

        IndexWriter indexWriter = new IndexWriter(dictionary, postingsList);
        for(int i = 0; i < tokens.length; i++ ) {
            indexWriter.write(insertRequest.docId, tokens[i], (float) insertRequest.score);
        }

        indexWriter.commit();

        return "Success";
    }

    @GetMapping("/search/{query}")

    public List<PostingItem> index(@PathVariable String query) {



        String[] tokens = query.split(" ");
        List<String> terms = new ArrayList<String>();


        SearchIndex searchIndex = new SearchIndex(dictionary, postingsList);



        for(int i = 0; i < tokens.length; i++) {
            terms.add(tokens[i]);
        }


        List<PostingItem> results = searchIndex.searchByTokens(terms);


        return results;


    }



}