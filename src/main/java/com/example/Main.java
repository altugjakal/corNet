package com.example;

import com.example.index.utils.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;

@SpringBootApplication
@EnableAsync
public class Main {

    public static void main(String[] args) throws IOException {

        Config.init();
        SpringApplication.run(Main.class, args);
    }

}
