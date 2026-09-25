package com.example.index;

import java.io.File;
import java.io.IOException;

public class MergeScheduler extends Thread {
    private Integer waitInterval;
    private AutoMerger autoMerger;
    private Integer fileCountThreshold;
    public DictionaryRouter dictionaryRouter;
    public Integer activeFileCount;
    private boolean workerWorking;
    public MergeScheduler(Integer waitInterval, Integer fileCountThreshold,  DictionaryRouter dictionaryRouter, IndexWriter indexWriter, SideFileWriter sideFileWriter) {
        this.waitInterval = waitInterval;
        this.autoMerger = new AutoMerger(indexWriter, dictionaryRouter, sideFileWriter);
        this.dictionaryRouter = dictionaryRouter;

        this.fileCountThreshold = (fileCountThreshold == null ? fileCountThreshold : 2);
        this.workerWorking = true;

    }

    public void run() {
        try {
            mergeWorker();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopWorker() {
        workerWorking = false;
    }
    //hottest oneliners to get chicks
    public void startWorker() {
        workerWorking=true;
    }


    private void mergeWorker() throws IOException, InterruptedException {
        while(workerWorking) {

            if (fileListChecker() && activeFileCount >= 2) {
                System.out.println("checking files " + activeFileCount + " " + (activeFileCount - 2) + " " + (activeFileCount - 1)) ;

                autoMerger.merge(activeFileCount - 2, activeFileCount - 1);
            }
            Thread.sleep(waitInterval * 1000);
        }
    }

    private boolean fileListChecker() throws IOException {
        File postingDir = new File("src/files/postings/");
        File[] postingFileListings = postingDir.listFiles();
        activeFileCount = postingFileListings == null ? 0 : postingFileListings.length;
        if (activeFileCount >= fileCountThreshold) {
            return true;
        }

        return false;

    }
}
