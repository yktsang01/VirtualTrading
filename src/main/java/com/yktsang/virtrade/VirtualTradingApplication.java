/*
 * VirtualTradingApplication.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Provides the entry point to the application.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@SpringBootApplication
public class VirtualTradingApplication {

    /**
     * Provides the entry point to the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(VirtualTradingApplication.class, args);
    }

}
