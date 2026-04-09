package com.ai.company.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ai.company.registry.AgentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generates registry_config.json file with all agent metadata.
 * 
 * This generator creates a JSON configuration file listing all registered agents
 * with their metadata for external tools and documentation.
 */
public class RegistryConfigGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(RegistryConfigGenerator.class);
    
    private final ObjectMapper objectMapper;
    
    public RegistryConfigGenerator() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generates registry_config.json file.
     * 
     * @param registry The agent registry
     * @param outputPath The path to write the JSON file
     * @throws IOException If file cannot be written
     */
    public void generate(AgentRegistry registry, String outputPath) throws IOException {
        log.info("Generating registry config to: {}", outputPath);
        
        ObjectNode root = objectMapper.createObjectNode();
        root.put("version", "1.0.0");
        root.put("generated_at", java.time.Instant.now().toString());
        root.put("total_agents", registry.getAgentNames().size());
        
        ArrayNode agentsArray = objectMapper.createArrayNode();
        
        for (String agentName : registry.getAgentNames()) {
            AgentMetadata metadata = registry.getMetadata(agentName);
            if (metadata != null) {
                ObjectNode agentNode = objectMapper.createObjectNode();
                agentNode.put("name", metadata.getName());
                agentNode.put("role", metadata.getRole());
                agentNode.put("description", metadata.getDescription());
                agentNode.put("class_name", metadata.getClassName());
                agentNode.put("zero_impact_mode", metadata.isZeroImpactMode());
                
                ArrayNode capabilitiesArray = objectMapper.createArrayNode();
                for (String capability : metadata.getCapabilities()) {
                    capabilitiesArray.add(capability);
                }
                agentNode.set("capabilities", capabilitiesArray);
                
                ArrayNode responsibilitiesArray = objectMapper.createArrayNode();
                for (String responsibility : metadata.getResponsibilities()) {
                    responsibilitiesArray.add(responsibility);
                }
                agentNode.set("responsibilities", responsibilitiesArray);
                
                agentsArray.add(agentNode);
            }
        }
        
        root.set("agents", agentsArray);
        
        // Write to file
        Path path = Paths.get(outputPath);
        Files.createDirectories(path.getParent());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), root);
        
        log.info("Generated registry config with {} agents", agentsArray.size());
    }
    
    /**
     * Generates registry_config.json to default location.
     * 
     * @param registry The agent registry
     * @throws IOException If file cannot be written
     */
    public void generate(AgentRegistry registry) throws IOException {
        generate(registry, "registry_config.json");
    }
}



