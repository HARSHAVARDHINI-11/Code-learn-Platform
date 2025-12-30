package com.codelearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class CodeLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeLearnApplication.class, args);
        System.out.println("✓ CodeLearn Platform API is running on port 5000");
        System.out.println("✓ Swagger UI: http://localhost:5000/swagger-ui.html");
    }
}
