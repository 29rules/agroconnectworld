package com.ai.company.api;

import com.ai.company.registry.AgentRegistry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot application for AI Company API.
 *
 * This API provides endpoints for:
 * - Agent execution
 * - Workflow orchestration
 * - CTO approvals
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.ai.company")
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
