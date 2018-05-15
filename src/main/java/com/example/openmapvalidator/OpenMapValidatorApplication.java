package com.example.openmapvalidator;

import com.example.openmapvalidator.service.FileToDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class OpenMapValidatorApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(OpenMapValidatorApplication.class, args);

        FileToDB fileToDB = (FileToDB) context.getBean("fileToDB");
        fileToDB.saveToDB();

    }
}
