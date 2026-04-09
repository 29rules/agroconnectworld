package com.ai.company.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Bean
    public ChatLanguageModel chatLanguageModel() {

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("❌ No API key found! Set OPENROUTER_API_KEY or OPENAI_API_KEY");
        }

        System.out.println("🔑 Loaded API Key (first 10 chars): " + apiKey.substring(0, 10));

        return OpenAiChatModel.builder()
                .apiKey(apiKey)

                // ✅ IMPORTANT: Use OpenRouter base URL
                .baseUrl("https://openrouter.ai/api/v1")

                // ✅ Use OpenRouter model
                .modelName("openai/gpt-4o-mini")

                .temperature(0.7)
                .maxTokens(2000)
                .build();
    }
}
