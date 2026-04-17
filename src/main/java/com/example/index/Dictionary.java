package com.example.index;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class Dictionary   {
    private List<DictItem> items;
    private String filePath;

    public Dictionary(String filePath) {
        this.filePath = filePath;
        this.items = new ArrayList<>();


    }



    public void load() {

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            items = (List<DictItem>) ois.readObject();



        } catch (EOFException e){

            items = new ArrayList<>();
        } catch (IOException e) {

            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            items = new ArrayList<>();
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
        boolean exists = items.stream()
                .anyMatch(var -> var.token.equals(token));

        if (!exists) {
            DictItem new_token = new DictItem();
            new_token.offset = offset;
            new_token.token = token;
            items.add(new_token);
        }
    }

    public List<Integer> getOffsets(List<String> tokens) {
        List<Integer> offsets = new ArrayList<Integer>();
        for(String token : tokens){

            offsets.add(this.items.stream().filter(var -> var.token.equals(token))
                    .findFirst()
                    .map(item -> item.offset)
                    .orElse(-1)); //neg here
        }



        return offsets;
    }
}
