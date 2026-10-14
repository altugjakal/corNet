package com.example.index;

import com.example.index.types.DocumentMetaItem;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;


public class DocumentMetaIndex {
    private ConcurrentHashMap<Integer, DocumentMetaItem> map;
    public String loadPath;

    public DocumentMetaIndex() {
        map = new ConcurrentHashMap<>();

    }

    public void load(String filePath) {
        File file = new File(filePath);

        //could get rid of the filePath parameter as there will only be a single file
        System.out.println("entering");
        this.loadPath = filePath;
        System.out.println(this.loadPath);

        if (!file.exists() || file.length() == 0) {
            map = new ConcurrentHashMap<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            map = (ConcurrentHashMap<Integer, DocumentMetaItem>) ois.readObject();
        } catch (EOFException | ClassNotFoundException e) {
            map = new ConcurrentHashMap<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read dictionary file", e);
        }




    }

    public void add(Integer docId, DocumentMetaItem documentMetaItem) {
        map.put(docId, documentMetaItem);

    }

    public DocumentMetaItem get(Integer docId) {
        return map.get(docId);
    }

    public int getIndexSize() {
        return map.size();
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
            oos.writeObject(map);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
