package com.example.index;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileManager {

    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = LOCK.writeLock();

    private static final List<Integer> buffer = new ArrayList<>();


    public static void startWriteStream(File file) throws IOException {
        writeLock.lock();
        // TODO: create string handling for  file paths instead of files optionally

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file, false))) {

            for (Integer item : buffer) {
                dos.writeInt(item);
            }

        } finally {
            buffer.clear();
            writeLock.unlock();
        }
    }


    public static RandomAccessFile startReadStreamer(String filePath)
            throws FileNotFoundException {

        return new RandomAccessFile(filePath, "r");
    }


    public static void writeInt(Integer number) {
        writeLock.lock();
        try {
            buffer.add(number);
        } finally {
            writeLock.unlock();
        }
    }


    public static void delete(File file) {
        writeLock.lock();
        try {
            file.delete();
        } finally {
            writeLock.unlock();
        }
    }
}