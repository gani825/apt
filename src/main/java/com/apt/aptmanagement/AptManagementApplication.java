package com.apt.aptmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AptManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AptManagementApplication.class, args);
    }
}
