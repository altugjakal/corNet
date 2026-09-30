package com.example.index;

import com.example.index.types.DocumentMetaItem;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentMetaIndex {
    private static ConcurrentHashMap<Integer, DocumentMetaItem> map;
    public static String loadPath;

    public void load(String filePath) {
        File file = new File(filePath);
        //could get rid of the filePath parameter as there will only be a single file

        if (file.length() == 0) {
            this.map = new ConcurrentHashMap<Integer, DocumentMetaItem>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.map = (ConcurrentHashMap<Integer, DocumentMetaItem>) ois.readObject();
        } catch (EOFException | ClassNotFoundException e) {
            this.map = new ConcurrentHashMap<Integer, DocumentMetaItem>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read dictionary file", e);
        }

        this.loadPath = filePath;

    }

    public void add(Integer docId, DocumentMetaItem documentMetaItem) {
        map.put(docId, documentMetaItem);

    }

    public static DocumentMetaItem get(Integer docId) {
        return map.get(docId);
    }

    public static int getIndexSize() {
        return map.size();
    }

    public static void save()  {
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
