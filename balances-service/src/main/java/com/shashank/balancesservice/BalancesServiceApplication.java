package com.shashank.balancesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
public class BalancesServiceApplication {
    private static final Logger logger = LogManager.getLogger(BalancesServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BalancesServiceApplication.class, args);
        logger.info("Balances Service started successfully");
    }
}
