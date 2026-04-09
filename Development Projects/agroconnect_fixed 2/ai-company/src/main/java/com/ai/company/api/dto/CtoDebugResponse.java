package com.ai.company.api.dto;

import java.time.Instant;
import java.util.List;

public class CtoDebugResponse {

    private String agent = "cto_agent";
    private boolean ready;
    private String model;
    private List<String> tools;
    private MemoryInfo memory;
    private String apiKeyPrefix;
    private String constructorUsed;
    private Instant timestamp;

    // ----------- Memory Info inner class ------------
    public static class MemoryInfo {
        private int windowSize;
        private int messagesPresent;

        public MemoryInfo(int windowSize, int messagesPresent) {
            this.windowSize = windowSize;
            this.messagesPresent = messagesPresent;
        }

        public int getWindowSize() { return windowSize; }
        public int getMessagesPresent() { return messagesPresent; }

        public void setWindowSize(int windowSize) { this.windowSize = windowSize; }
        public void setMessagesPresent(int messagesPresent) { this.messagesPresent = messagesPresent; }
    }

    // ----------- Getters & Setters --------------------
    public String getAgent() { return agent; }

    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<String> getTools() { return tools; }
    public void setTools(List<String> tools) { this.tools = tools; }

    public MemoryInfo getMemory() { return memory; }
    public void setMemory(MemoryInfo memory) { this.memory = memory; }

    public String getApiKeyPrefix() { return apiKeyPrefix; }
    public void setApiKeyPrefix(String apiKeyPrefix) { this.apiKeyPrefix = apiKeyPrefix; }

    public String getConstructorUsed() { return constructorUsed; }
    public void setConstructorUsed(String constructorUsed) { this.constructorUsed = constructorUsed; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
