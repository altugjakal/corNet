package com.example.index;

import java.io.*;
import java.util.*;

import com.example.index.types.HitItem;
import com.example.index.types.OffsetItem;
import com.example.index.types.PostingItem;

import java.util.concurrent.ConcurrentHashMap;

public class PostingsList {
    private final Dictionary dictionary;
    public final ConcurrentHashMap<String, List<PostingItem>> map;
    public String lastSavePath = "";

    public PostingsList(Dictionary dictionary) {
        this.dictionary = dictionary;
        this.map = new ConcurrentHashMap<>();
    }


    public void add(int docId, Map<String, List<HitItem>> pairs ) {


        for (var entry : pairs.entrySet()) {
            String word = entry.getKey();
            List<HitItem> hits = entry.getValue();

            PostingItem item = new PostingItem();
            item.docId = docId;
            item.hits.addAll(hits);





            map.computeIfAbsent(word, k -> new ArrayList<>())
                    .add(item);
        }
    }

    public List<OffsetItem> getByOffset(int offset, String filePath) throws IOException {
        // loop through all files in the files directory on the other side

        try (RandomAccessFile raf = FileManager.startReadStreamer(
                filePath == null ? lastSavePath : filePath
        )) {
        List<OffsetItem> results = new ArrayList<>();
        raf.seek(offset);


        int count = raf.readInt();



        Float idf = (1 / (float) count);

        for(int i = 0; i < count; i++) {
            int docId = raf.readInt();


            int hitLength = raf.readInt();
            ArrayList<HitItem> hits = new ArrayList<>(hitLength);

            for (int j = 0; j < hitLength; j++){
                HitItem hit = new HitItem();
                hit.weight = raf.readInt();
                hit.position = raf.readInt();
                hits.add(hit);

            }


            OffsetItem offsetItem = new OffsetItem();
            offsetItem.postingItem = new PostingItem();
            offsetItem.postingItem.docId = docId;

            int tf = hitLength;
            //tf is per document and hits are also returned so you might just not return tf at all, send idf as an extra variable, or dont

            offsetItem.postingItem.hits = hits;
            // length of positions is basically the tf per passage,
            // or store weight and position as pairs and read together in the loop, --ended up doing this
            // the latter is probably a better idea

            results.add(offsetItem);
            //send tfidf with it, the other end has to get data in this format: docData: tfidf for vecor calculation
        }

            //return results and idf as idf is global for this, or multiply tf's and idf's to get a general score in the result object
        return results;
        }
    }

    public void save(String filePath){




        for (Map.Entry<String, List<PostingItem>> entry : map.entrySet()) {
            System.out.println("key: " + entry.getKey());

            // Nested loop to iterate over the list of PostingItems
            for (PostingItem item : entry.getValue()) {
                System.out.println("  PostingItem docId: " + item.docId);
                System.out.println("  PostingItem docId: " + item.hits.size());
            }
        }




        File file = new File(filePath);
        lastSavePath = filePath;
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        int offset = 0;

        try {
            for(Map.Entry<String, List<PostingItem>> entry : map.entrySet()) {



                    List<PostingItem> postings = entry.getValue();
                    postings.sort(Comparator.comparingInt(p -> p.docId));

                    int setOffset = offset;
                    FileManager.writeInt(entry.getValue().size());
                    offset += 4;
                    for (PostingItem p : postings){
                        int hitsSize = p.hits.size();


                        FileManager.writeInt(p.docId);
                        FileManager.writeInt(hitsSize);
                        offset += 8;

                        for (int i = 0; i < hitsSize; i++) {
                            FileManager.writeInt(p.hits.get(i).weight);
                            FileManager.writeInt(p.hits.get(i).position);
                            offset += 8;
                        }
                    }
                    dictionary.add(entry.getKey(), setOffset);
            }

            FileManager.startWriteStream(file);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
