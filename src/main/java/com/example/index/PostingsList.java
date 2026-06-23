package com.example.index;

import java.io.*;
import java.util.*;

public class PostingsList {
    private String filePath;
    private Dictionary dictionary;
    private HashMap<String, List<PostingItem>> map;



    public PostingsList(Dictionary dictionary, String filePath) {
        this.dictionary = dictionary;
        this.filePath = filePath;
        this.map = new HashMap<>();

    }


    public void add(String term, int docId, float score) {
        PostingItem item = new PostingItem();
        item.docId = docId;
        item.score = score;
        map.computeIfAbsent(term, k -> new ArrayList<>())
                .add(item);
    }

    public List<PostingItem> getByOffset(int offset) throws IOException {
        try(RandomAccessFile raf = new RandomAccessFile(this.filePath, "r")) {
        List<PostingItem> results = new ArrayList<>();
        raf.seek(offset);

        int count = raf.readInt();
        float df = count;
        for(int i = 0; i < count; i++) {
            int docId = raf.readInt();
            float score = raf.readFloat();

            PostingItem posting = new PostingItem();
            posting.docId = docId;
            posting.score = score;
            posting.idf = (1 / df);

            results.add(posting);
        }

        return results;
        }
    }

    public void save(){
        File file = new File(filePath);
        int offset = (int) file.length();

        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, true))) {
            for(Map.Entry<String, List<PostingItem>> entry : map.entrySet()) {




                    int setOffset = offset;
                    dos.writeInt(entry.getValue().size());

                    offset += 4;
                    for (PostingItem p : entry.getValue()){
                        dos.writeInt(p.docId);
                        dos.writeFloat(p.score);

                        offset += 8;
                    }
                    dictionary.add(entry.getKey(), setOffset);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
