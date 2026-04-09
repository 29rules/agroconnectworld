package com.ai.company.registry;

import java.util.Set;

/**
 * Metadata about an AI agent.
 * 
 * Contains information about:
 * - Agent name and role
 * - Capabilities and responsibilities
 * - Zero-impact mode compliance
 */
public class AgentMetadata {
    
    private final String name;
    private final String role;
    private final String description;
    private final Set<String> capabilities;
    private final Set<String> responsibilities;
    private final boolean zeroImpactMode;
    private final String className;
    
    /**
     * Creates agent metadata.
     * 
     * @param name Agent name (e.g., "cto_agent")
     * @param role Agent role (e.g., "Chief Technology Officer")
     * @param description Agent description
     * @param capabilities Set of agent capabilities
     * @param responsibilities Set of agent responsibilities
     * @param zeroImpactMode Whether agent operates in zero-impact mode
     * @param className Fully qualified class name
     */
    public AgentMetadata(String name, String role, String description,
                        Set<String> capabilities, Set<String> responsibilities,
                        boolean zeroImpactMode, String className) {
        this.name = name;
        this.role = role;
        this.description = description;
        this.capabilities = capabilities != null ? Set.copyOf(capabilities) : Set.of();
        this.responsibilities = responsibilities != null ? Set.copyOf(responsibilities) : Set.of();
        this.zeroImpactMode = zeroImpactMode;
        this.className = className;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getRole() {
        return role;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Set<String> getCapabilities() {
        return capabilities;
    }
    
    public Set<String> getResponsibilities() {
        return responsibilities;
    }
    
    public boolean isZeroImpactMode() {
        return zeroImpactMode;
    }
    
    public String getClassName() {
        return className;
    }
    
    /**
     * Checks if agent has a specific capability.
     * 
     * @param capability The capability to check
     * @return true if agent has the capability
     */
    public boolean hasCapability(String capability) {
        return capabilities.contains(capability.toLowerCase());
    }
    
    /**
     * Checks if agent has a specific responsibility.
     * 
     * @param responsibility The responsibility to check
     * @return true if agent has the responsibility
     */
    public boolean hasResponsibility(String responsibility) {
        return responsibilities.contains(responsibility.toLowerCase());
    }
    
    @Override
    public String toString() {
        return String.format("AgentMetadata[name=%s, role=%s, capabilities=%d, zeroImpact=%s]",
            name, role, capabilities.size(), zeroImpactMode);
    }
}



