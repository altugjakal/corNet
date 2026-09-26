package com.example.index;

import com.example.index.types.DictPostingPair;
import org.springframework.stereotype.Component;


import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class SideFileWriter extends Thread {
    public BlockingQueue<CountPair> saveQueue;
    public DictionaryRouter dictionaryRouter;
    public SideFileWriter(DictionaryRouter dictionaryRouter) {
        this.saveQueue = new LinkedBlockingQueue<>();
        this.dictionaryRouter = dictionaryRouter;

    }

    public static class CountPair implements Serializable {
        public Integer saveCount;
        public DictPostingPair dictPostingPair;

        public CountPair(Integer saveCount, DictPostingPair dictPostingPair) {
            this.saveCount = saveCount;
            this.dictPostingPair = dictPostingPair;
        }
    }

    public void run() {
        try {
            while(saveQueue != null) {
                CountPair pair = saveQueue.take();
                System.out.println("the queue is full now");
                saveToDisk(pair.saveCount, pair.dictPostingPair.postingsList, pair.dictPostingPair.dictionary);
                this.dictionaryRouter.submit(pair.saveCount, pair.dictPostingPair);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToQueue(Integer saveCount, Dictionary dictionary, PostingsList postingsList) {
        CountPair countPair = new CountPair(saveCount, new DictPostingPair(dictionary, postingsList));
        saveQueue.add(countPair);
    }

    private void saveToDisk(Integer saveCount, PostingsList postingsList, Dictionary dictionary) {

        String postingFileName = "_" + saveCount + ".bin";
        String dictFileName = "_" + saveCount + ".dic";

        postingsList.save("src/files/postings/" + postingFileName);
        dictionary.save("src/files/dicts/" + dictFileName);


    }


}
