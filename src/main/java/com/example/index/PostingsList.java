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


    public void add(int docId, Map<String, List<HitItem>> pairs ) {
        PostingItem item = new PostingItem();
        item.docId = docId;

        for (var entry : pairs.entrySet()) {
            String word = entry.getKey();
            List<HitItem> hits = entry.getValue();
            item.hits.addAll(hits);
            map.computeIfAbsent(word, k -> new ArrayList<>())
                    .add(item);
        }



        //maybe feed full document as pairs group all occurrences of all words as hit items and insert them as hits of a term, go word by word
        /*
        : Array(weight, score)
        iterate per word:
            create PostingItem
            iterate Array of the current word above:
                create HitItem
                add HitItem to PostingItem hits
            map.computeIfAbsent(word, k -> new ArrayList<>())
                .add(item);
             }
        */


    }

    public List<PostingItem> getByOffset(int offset) throws IOException {
        // make this return an idf score and some items that contain tf's and other data so make a new type please
        try(RandomAccessFile raf = new RandomAccessFile(this.filePath, "r")) {
        List<PostingItem> results = new ArrayList<>();
        raf.seek(offset);

        int count = raf.readInt();
        Float idf = (1 / (float) count);

        for(int i = 0; i < count; i++) {
            int docId = raf.readInt();
            ArrayList<HitItem> hits = new ArrayList<>();

            int hitLength = raf.readInt();
            for (int j = 0; j < hitLength; j++){
                HitItem hit = new HitItem();
                hit.weight = raf.readInt();
                hit.position = raf.readInt();
                hits.add(hit);

            }


            PostingItem posting = new PostingItem();
            posting.docId = docId;

            int tf = hitLength;
            //tf is per document and hits are also returned so you might just not return tf at all, send idf as an extra  variable


            posting.hits = hits;
            // length of positions is basically the tf per passage,
            // or store weight and position as pairs and read together in the loop, --ended up doing this
            // the latter is probably a better idea

            results.add(posting);
        }


            //return results and idf as idf is global for this, or multiply tf's and idf's to get a general score in the result object
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
                        int hitsSize = p.hits.size();
                        dos.writeInt(p.docId);
                        dos.writeFloat(hitsSize);
                        offset += 8;

                        for (int i = 0; i < hitsSize; i++) {
                            dos.writeInt(p.hits.get(i).weight);
                            dos.writeInt(p.hits.get(i).position);
                            offset += 8;
                        }
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
