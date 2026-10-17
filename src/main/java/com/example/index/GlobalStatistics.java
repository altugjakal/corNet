package com.example.index;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;


public class GlobalStatistics {

    private HashMap<Integer, Float> avgFieldLengths;
    public int indexSize;
    public String loadPath;

    public GlobalStatistics() {
        avgFieldLengths = new HashMap<>();
        indexSize = 0;
    }

    public void updateFieldAverage(int fieldId, int fieldLength) {

        avgFieldLengths.putIfAbsent(fieldId, (avgFieldLengths.getOrDefault(fieldId, 0f) + fieldLength)/ indexSize);
    }

    public float getFieldAverage(int fieldId) {
        return avgFieldLengths.getOrDefault(fieldId, 0f);
    }

    public void increaseIndexSize(int indexAdditions) {
        indexSize += indexAdditions;
    }

    public void load(String filePath) {
        File file = new File(filePath);

        this.loadPath = filePath;
        //could get rid of the filePath parameter as there will only be a single file


        if (!file.exists() || file.length() == 0) {
            avgFieldLengths = new HashMap<>();
            indexSize = 0;
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            avgFieldLengths = (HashMap<Integer, Float>) ois.readObject();
            indexSize = (Integer) ois.readObject();

        } catch (EOFException | ClassNotFoundException e) {
            avgFieldLengths = new HashMap<>();
            indexSize = 0;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read dictionary file", e);
        }

    }

    public void save()  {
        if (loadPath == null) {
            throw new IllegalStateException("Cannot save index: loadPath has not been initialized. Call load() first.");
        }

        File file = new File(loadPath);

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

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(loadPath, false))) {
            oos.writeObject(avgFieldLengths);
            oos.writeObject(indexSize);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
