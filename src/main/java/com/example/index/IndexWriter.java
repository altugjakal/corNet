package com.example.index;



import com.example.index.types.DocumentMetaItem;
import com.example.index.types.HitItem;
import com.example.index.utils.Config;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class IndexWriter implements ApplicationListener<ContextClosedEvent> {
    private Dictionary dictionary;
    private PostingsList postingsList;
    private final String configPath = "src/files/config.cfg";
    private SideFileWriter sideFileWriter;
    public static int saveCount;
    public DocumentMetaIndex documentMetaIndex;
    private int insertCount;
    private GlobalStatistics globalStatistics;



    public IndexWriter(DictionaryRouter dictionaryRouter, SideFileWriter sideFileWriter, DocumentMetaIndex documentMetaIndex, GlobalStatistics globalStatistics) {
        this.documentMetaIndex = documentMetaIndex;
        this.globalStatistics = globalStatistics;
        this.dictionary = new Dictionary();
        this.postingsList = new PostingsList(dictionary, globalStatistics);
        this.sideFileWriter = sideFileWriter;
        load();



    }

    public void decrementSaveCount() {
        saveCount = saveCount-1;
    }


    public void load() {
        Path path = Paths.get(configPath);
        if (Files.exists(path)) {
            try (Scanner scanner = new Scanner(path)) {
                if (scanner.hasNextInt()) {
                    saveCount = scanner.nextInt();
                    return;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        saveCount = 0;
    }



    public void write(int docId, Map<String, List<HitItem>> pairs, Boolean meta) {
        globalStatistics.increaseIndexSize(1);
        if(insertCount < Config.getDumpMaxInsertions()) {
            insertCount++;
        } else {
            insertCount = 1;
            sideFileWriter.addToQueue(saveCount, dictionary, postingsList);

            this.dictionary = new Dictionary();
            this.postingsList = new PostingsList(dictionary, globalStatistics);

            saveCount++;
        }

        if(meta == false) {
            DocumentMetaItem documentMetaItem = this.postingsList.add(docId, pairs); // done - maybe return total length here, instead of re-iterating something like count = this.postingslist.add

            //mega TODO: before doing this check if anchors are inserted or not
            documentMetaIndex.add(docId, documentMetaItem);
        }

    }

    public void emergencyDump() {

        this.documentMetaIndex.save();
        this.globalStatistics.save();


        if (!postingsList.map.isEmpty()) {

            sideFileWriter.addToQueue(saveCount, dictionary, postingsList);
            this.dictionary = new Dictionary();
            this.postingsList = new PostingsList(dictionary, globalStatistics);

            saveCount++;
        }
        try (Writer wr = new FileWriter( configPath)) { wr.write(Integer.toString(saveCount)); } catch (IOException e) { e.printStackTrace(); }

    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event){
        //if the thing shuts down, save what's left

        emergencyDump();

    }



}
