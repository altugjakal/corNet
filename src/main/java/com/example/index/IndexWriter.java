package com.example.index;



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

@Component
public class IndexWriter implements ApplicationListener<ContextClosedEvent> {
    private Dictionary dictionary;
    private PostingsList postingsList;
    private final DictionaryRouter dictionaryRouter;
    private final String configPath = "src/files/config.cfg";
    public static int saveCount;



    public IndexWriter(DictionaryRouter dictionaryRouter) {
        this.dictionaryRouter = dictionaryRouter;
        this.dictionary = new Dictionary();
        this.postingsList = new PostingsList(dictionary);
        load();



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

    private void saveDisk() {

        String postingFileName = "_" + saveCount + ".bin";
        String dictFileName = "_" + saveCount + ".dic";
        this.postingsList.save("src/files/postings/" + postingFileName);
        this.dictionary.save("src/files/dicts/" + dictFileName);

    }

    public void write(int docId, Map<String, List<HitItem>> pairs) {
        try {
        this.postingsList.add(docId, pairs);
        } catch (OutOfMemoryError e) {


            saveDisk();
            this.dictionaryRouter.submit(saveCount, this.dictionary);
            this.dictionary = new Dictionary();
            this.postingsList = new PostingsList(dictionary);
            this.postingsList.add(docId, pairs);
            saveCount++;
        }


    }

    public void emergencyDump() {

        if (!postingsList.map.isEmpty()) {

            saveDisk();
            this.dictionaryRouter.submit(saveCount, this.dictionary);
            this.dictionary = new Dictionary();
            this.postingsList = new PostingsList(dictionary);

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
