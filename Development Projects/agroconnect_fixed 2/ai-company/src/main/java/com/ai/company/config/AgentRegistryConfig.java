package com.ai.company.config;

import com.ai.company.registry.AgentRegistry;
import com.ai.company.tools.audit.SystemAuditTool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentRegistryConfig implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AgentRegistryConfig.class);

    @Autowired(required = false)
    private SystemAuditTool systemAuditTool;

    /**
     * NEW: Build AgentRegistry using the correct model
     */
    @Bean
    public AgentRegistry agentRegistry(ChatLanguageModel chatModel) {
        return AgentRegistry.getInstance(chatModel);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (systemAuditTool != null) {
            try {
                AgentRegistry.getInstance().updateCTOAgentWithTool(systemAuditTool);
                log.info("✅ SystemAuditTool successfully injected into CTO Agent");
            } catch (Exception e) {
                log.warn("Could not inject SystemAuditTool into CTO Agent: {}", e.getMessage());
            }
        } else {
            log.warn("SystemAuditTool not available - CTO Agent will not have audit capability");
        }
    }
}
