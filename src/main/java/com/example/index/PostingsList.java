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

    public List<OffsetItem> getByOffset(int offset) throws IOException {
        // make this return an idf score and some items that contain tf's and other data so make a new type please
        try(RandomAccessFile raf = new RandomAccessFile(this.filePath, "r")) {
        List<OffsetItem> results = new ArrayList<>();
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


            OffsetItem offsetItem = new OffsetItem();
            offsetItem.postingItem = new PostingItem();
            offsetItem.postingItem.docId = docId;

            int tf = hitLength;
            //tf is per document and hits are also returned so you might just not return tf at all, send idf as an extra  variable



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

    public void save(){
        File file = new File(filePath);
        int offset = (int) file.length();

        try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false))) {
            for(Map.Entry<String, List<PostingItem>> entry : map.entrySet()) {






                    List<PostingItem> postings = entry.getValue();
                    postings.sort(Comparator.comparingInt(p -> p.docId));

                    int setOffset = offset;
                    dos.writeInt(entry.getValue().size());
                    offset += 4;
                    for (PostingItem p : postings){
                        int hitsSize = p.hits.size();


                        dos.writeInt(p.docId);
                        dos.writeInt(hitsSize);
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
