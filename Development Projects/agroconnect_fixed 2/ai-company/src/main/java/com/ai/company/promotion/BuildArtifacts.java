package com.ai.company.promotion;

import java.util.ArrayList;
import java.util.List;

public class BuildArtifacts {
    private String buildId;
    private String environment;
    private boolean built;
    
    private String frontendImage;
    private String gatewayImage;
    private String authServiceImage;
    private String productServiceImage;
    private String supplierServiceImage;
    private String quoteServiceImage;
    private String orderServiceImage;
    private String contactServiceImage;
    
    private List<String> errors = new ArrayList<>();
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    // Getters and Setters
    public String getBuildId() { return buildId; }
    public void setBuildId(String buildId) { this.buildId = buildId; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public boolean isBuilt() { return built; }
    public void setBuilt(boolean built) { this.built = built; }
    
    public String getFrontendImage() { return frontendImage; }
    public void setFrontendImage(String frontendImage) { this.frontendImage = frontendImage; }
    
    public String getGatewayImage() { return gatewayImage; }
    public void setGatewayImage(String gatewayImage) { this.gatewayImage = gatewayImage; }
    
    public String getAuthServiceImage() { return authServiceImage; }
    public void setAuthServiceImage(String authServiceImage) { this.authServiceImage = authServiceImage; }
    
    public String getProductServiceImage() { return productServiceImage; }
    public void setProductServiceImage(String productServiceImage) { this.productServiceImage = productServiceImage; }
    
    public String getSupplierServiceImage() { return supplierServiceImage; }
    public void setSupplierServiceImage(String supplierServiceImage) { this.supplierServiceImage = supplierServiceImage; }
    
    public String getQuoteServiceImage() { return quoteServiceImage; }
    public void setQuoteServiceImage(String quoteServiceImage) { this.quoteServiceImage = quoteServiceImage; }
    
    public String getOrderServiceImage() { return orderServiceImage; }
    public void setOrderServiceImage(String orderServiceImage) { this.orderServiceImage = orderServiceImage; }
    
    public String getContactServiceImage() { return contactServiceImage; }
    public void setContactServiceImage(String contactServiceImage) { this.contactServiceImage = contactServiceImage; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}



