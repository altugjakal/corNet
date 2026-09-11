package com.example.index;


import java.io.*;
import java.util.*;


public class Dictionary   {
    public ArrayList<DictItem> items;
    public String loadPath;
    public String savePath;

    public Dictionary() {
        this.items = new ArrayList<>();
        this.loadPath = "";
        this.savePath = "";

    }

    public static class DictItem {
        public String token;
        public Integer offset;

        public DictItem (String token, Integer offset) {
            this.token = token;
            this.offset= offset;
        }

    }

    public void load(String filePath) {

        File file = new File(filePath);




        if (file.length() == 0) {
            this.items = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.items = (ArrayList<DictItem>) ois.readObject();
        } catch (EOFException | ClassNotFoundException e) {
            this.items = new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read dictionary file", e);
        }

        this.loadPath = filePath;
    }


    private static class TokenComparator implements Comparator<DictItem> {
        public int compare(DictItem s1, DictItem s2) {
            return s1.token.compareTo(s2.token);
        }
    }


    public void save(String filePath) {


        items.sort(new TokenComparator());
        //sort here

        File file = new File(filePath);

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

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(items);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.savePath = filePath;

    }



    public void add(String token, Integer offset) {
        if (getOffset(token) == -1) { // TODO: this will almost never receive a dupe, so might as well get rid of it but check first
            DictItem dictItem = new DictItem(token, offset);
            items.add(dictItem);
        }
    }

    public Integer getOffset(String token) {

        int dictIndex = Collections.binarySearch(items, new DictItem(token, 1), new TokenComparator());
        if(dictIndex == -1) {
            return -1;
        }
        DictItem dictItem = items.get(dictIndex);

        return dictItem.offset;
    }
}
