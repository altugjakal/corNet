package com.example.api.controllers;

import com.example.index.*;

import com.example.index.merge.MergeScheduler;
import com.example.index.types.Document;
import com.example.index.types.HitItem;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class IndexController {
    private DictionaryRouter dictionaryRouter;
    private IndexWriter indexWriter;
    public DocumentMetaIndex documentMetaIndex;
    public GlobalStatistics globalStatistics;



    public IndexController() {
            globalStatistics = new GlobalStatistics();
            globalStatistics.load("src/files/statistics.meta");
            dictionaryRouter =  new DictionaryRouter(globalStatistics);
            documentMetaIndex = new DocumentMetaIndex();
            documentMetaIndex.load("src/files/details.meta"); // enter filepath from config or not
            SideFileWriter sideFileWriter = new SideFileWriter(dictionaryRouter);
            sideFileWriter.start();
            indexWriter = new IndexWriter(dictionaryRouter, sideFileWriter, documentMetaIndex,  globalStatistics);
            MergeScheduler mergeScheduler = new MergeScheduler(50, 2, dictionaryRouter, indexWriter, sideFileWriter, globalStatistics);
            mergeScheduler.start();


    }

    public static class InsertRequest {
        public Integer docId;
        public Map<String, List<HitItem>> pairs;

        public InsertRequest() {}

    }

    @PostMapping({"/insert", "/insert/{meta}"})
    public void index(
            @PathVariable(value = "meta", required = false) String meta,
            @RequestBody InsertRequest insertRequest
    ) {
        Map<String, List<HitItem>> pairs = insertRequest.pairs;

        if (meta == null) {
            indexWriter.write(insertRequest.docId, pairs, false);
        } else {
            indexWriter.write(insertRequest.docId, pairs, true);
        }
    }

    @PostMapping("/commit")
    public void index() {

        indexWriter.emergencyDump();


    }





    @GetMapping("/search/{query}")

    public HashMap<Integer, Document> index(@PathVariable String query) {

        SearchIndex searchIndex = new SearchIndex(dictionaryRouter, documentMetaIndex, globalStatistics);

        String[] tokens = query.split(" ");

        List<String> terms = new ArrayList<String>(Arrays.asList(tokens));

        return searchIndex.searchByTokens(terms);


    }



}