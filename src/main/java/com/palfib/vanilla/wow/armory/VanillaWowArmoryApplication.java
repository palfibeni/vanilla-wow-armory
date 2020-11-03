package com.palfib.vanilla.wow.armory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VanillaWowArmoryApplication {

    private static final Logger log = LoggerFactory.getLogger(VanillaWowArmoryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(VanillaWowArmoryApplication.class, args);
    }

}
