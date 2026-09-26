package com.example.index.utils;
import jakarta.el.PropertyNotFoundException;

import java.io.*;
import java.util.Properties;

public class Config {

    private static Properties properties;

    public static void init() throws IOException {
        properties = new Properties();
        try {
            InputStream in = Config.class.getResourceAsStream("/config.properties");
            properties.load(in);

        }
        catch (IOException e) {
            throw new IOException();
        }



    }

    public static Integer getDumpMaxInsertions() {
        try {
            String dumpMaxInsertionProperty = properties.getProperty("fileInsertionThreshold", "-1");

            if (dumpMaxInsertionProperty == null) {
                throw new PropertyNotFoundException("The property was not defined in configuration, have you configured it?");
            }
            return Integer.parseInt(dumpMaxInsertionProperty);

        } catch (NullPointerException e) {
            throw new PropertyNotFoundException("The property was not found in configuration.");

        }
    }
}
