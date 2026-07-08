package com.example.index;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class Dictionary   {
    private HashMap<String, Integer> items;
    private String filePath;

    public Dictionary(String filePath) {
        this.filePath = filePath;
        this.items = new HashMap<>();


    }



    public void load() {

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            items = (HashMap<String, Integer>) ois.readObject();



        } catch (EOFException | ClassNotFoundException e){

            items = new HashMap<>();
        } catch (IOException e) {

            throw new RuntimeException(e);
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
