package com.example.index;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class Dictionary   {
    private ConcurrentHashMap<String, Integer> items;
    private String filePath;

    public Dictionary(String filePath) {
        this.filePath = filePath;
        this.items = new ConcurrentHashMap<>();


    }



    public void load() {



        File file = new File(filePath);

        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            this.items = new ConcurrentHashMap<>();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        if (file.length() == 0) {
            this.items = new ConcurrentHashMap<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.items = (ConcurrentHashMap<String, Integer>) ois.readObject();
        } catch (EOFException | ClassNotFoundException e) {
            this.items = new ConcurrentHashMap<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read dictionary file", e);
        }
    }


    public void save() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(items);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void add(String token, Integer offset) {
        if (!items.containsKey(token)) {
            items.put(token, offset);
        }
    }

    public Integer getOffset(String token) {

        return items.getOrDefault(token, -1);
    }
}
